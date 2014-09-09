package uk.me.lewisdeane.materialnotes.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import uk.me.lewisdeane.materialnotes.R;
import uk.me.lewisdeane.materialnotes.activities.MainActivity;
import uk.me.lewisdeane.materialnotes.customviews.CustomTextView;
import uk.me.lewisdeane.materialnotes.utils.DatabaseHelper;

/**
 * Created by Lewis on 05/08/2014.
 */
public class ActionBarFragment extends Fragment {

    private View mRootView;
    public static LinearLayout mContainer, mActionBar1, mActionBar2;
    public static ImageButton mMenu, mSearch, mMic;
    public static CustomTextView mHeader;
    public static EditText mSearchBox;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_action_bar, container, false);
        init();
        checkIfSpeechAvailable();
        setListeners();
        return mRootView;
    }

    private void init(){
        mContainer = (LinearLayout) mRootView.findViewById(R.id.fragment_action_bar_container);
        mActionBar1 = (LinearLayout) mRootView.findViewById(R.id.fragment_action_bar_1);
        mActionBar2 = (LinearLayout) mRootView.findViewById(R.id.fragment_action_bar_2);

        mMenu = (ImageButton) mRootView.findViewById(R.id.fragment_action_bar_1_toggle);
        mSearch = (ImageButton) mRootView.findViewById(R.id.fragment_action_bar_1_search);
        mMic = (ImageButton) mRootView.findViewById(R.id.fragment_action_bar_2_voice_search);

        mHeader = (CustomTextView) mRootView.findViewById(R.id.fragment_action_bar_1_subheader);

        mSearchBox = (EditText) mRootView.findViewById(R.id.fragment_action_bar_2_search_box);
    }

    private void checkIfSpeechAvailable(){
        PackageManager pm = getActivity().getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(
                new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        if (activities.size() == 0)
            mMic.setVisibility(View.GONE);
    }

    private void setListeners(){
        mMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goBack(false);
                mActionBar1.setVisibility(View.VISIBLE);
                mActionBar2.setVisibility(View.GONE);
                InputMethodManager inputMethodManager=(InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(mContainer.getWindowToken(), 0);
            }
        });
        mSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActionBar1.setVisibility(View.GONE);
                mActionBar2.setVisibility(View.VISIBLE);
                InputMethodManager inputMethodManager=(InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.toggleSoftInputFromWindow(mContainer.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);

            }
        });
        mMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                startActivityForResult(intent, 1234);
            }
        });
        mSearchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                MainActivity.loadSearchResults(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(data != null) {
            ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (results.size() != 0) {
                mSearchBox.setText(results.get(0));
                mSearchBox.setSelection(results.get(0).length());
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void goBack(boolean _backKey){
        MainActivity.mAddFragment.mTitle.clearFocus();
        mSearchBox.setText("");
        MainActivity.PATH = getNewPath(_backKey);
        MainActivity.loadNotes();
        setUp(null);
    }

    public String getNewPath(boolean _backKey){
        // If we are viewing the note or folder go back to the directory containing it.

        if(MainActivity.ADD_MODE == MainActivity.AddMode.NONE && MainActivity.PATH.length() > 1){
            return DatabaseHelper.getPrevPath(MainActivity.PATH);
        } else if(MainActivity.ADD_MODE == MainActivity.AddMode.NONE && MainActivity.PATH.equals("/")){
            if(_backKey && MainActivity.DRAWER_OPEN)
                getActivity().finish();
            else
                MainActivity.mNavigationDrawerFragment.mDrawerLayout.openDrawer(Gravity.LEFT);
        } else {
            MainActivity.closeAdd();
            MainActivity.ADD_MODE = MainActivity.AddMode.NONE;
            return DatabaseHelper.getPrevPath(MainActivity.PATH+"/");
        }
        return MainActivity.PATH;
    }

    public void setUp(String _text){
        String[] split = MainActivity.PATH.split("/");
        mHeader.setText(MainActivity.PATH.equals("/") ? getSelectedItem() : split[split.length-1]);

        mMenu.setImageDrawable(getActivity().getResources().getDrawable(MainActivity.PATH.equals("/") && MainActivity.ADD_MODE == MainActivity.AddMode.NONE ? R.drawable.ic_action_menu_white : R.drawable.ic_action_arrow_back_white));

        if(_text != null)
            mHeader.setText(MainActivity.ADD_MODE == MainActivity.AddMode.NONE ? _text : "");
    }

    private String getSelectedItem(){
        switch(MainActivity.CURRENT_SELECTED_POSITION){
            case 0:
                return getString(R.string.navigation_item_1);
            case 1:
                return getString(R.string.navigation_item_2);
            case 2:
                return getString(R.string.navigation_item_3);
            default:
                return getString(R.string.navigation_item_1);
        }
    }

    public void onDrawerOpened(){
        showSearchIcon(false);
    }

    public void onDrawerClosed(){
        showSearchIcon(true);
    }

    public void showSearchIcon(boolean _shouldShow){
        MainActivity.mActionBarFragment.mSearch.setClickable(_shouldShow);
    }
}
