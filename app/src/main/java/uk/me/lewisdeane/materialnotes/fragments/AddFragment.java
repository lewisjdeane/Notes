package uk.me.lewisdeane.materialnotes.fragments;

import android.app.Fragment;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import uk.me.lewisdeane.materialnotes.R;
import uk.me.lewisdeane.materialnotes.activities.MainActivity;
import uk.me.lewisdeane.materialnotes.adapters.NoteAdapter;
import uk.me.lewisdeane.materialnotes.objects.NoteItem;
import uk.me.lewisdeane.materialnotes.utils.DeviceProperties;

/**
 * Created by Lewis on 13/08/2014.
 */
public class AddFragment extends Fragment {

    private View mRootView;
    public static LinearLayout mContainer;
    private MainFragment mMainFragment;
    private FABFragment mFABFragment;
    private Button mAdd;
    public EditText mTitle, mItem;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_add, container, false);
        init();
        setListeners();
        return mRootView;
    }

    private void init(){
        mContainer = (LinearLayout) mRootView.findViewById(R.id.fragment_add_container);
        mAdd = (Button) mRootView.findViewById(R.id.fragment_add_button);
        mTitle = (EditText) mRootView.findViewById(R.id.fragment_add_title);
        mItem = (EditText) mRootView.findViewById(R.id.fragment_add_item);

        mMainFragment = (MainFragment)getFragmentManager().findFragmentById(R.id.fragment_main);
        mFABFragment = (FABFragment)getFragmentManager().findFragmentById(R.id.fragment_fab);

        GradientDrawable bgShape = (GradientDrawable)mAdd.getBackground();
        bgShape.setColor(Color.parseColor("#ff9900"));
    }

    private void setListeners(){
        mAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.mMainFragment.mNoteItems.add(new NoteItem(getActivity(), mTitle.getText().toString(), mItem.getText().toString(), false));
                MainActivity.mMainFragment.mNoteAdapter.notifyDataSetChanged();
                getFragmentManager().beginTransaction().show(MainActivity.mMainFragment).commit();
                mFABFragment.mFAB.setVisibility(View.VISIBLE);
            }
        });
    }
}
