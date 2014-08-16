package uk.me.lewisdeane.materialnotes.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import uk.me.lewisdeane.materialnotes.R;
import uk.me.lewisdeane.materialnotes.activities.MainActivity;
import uk.me.lewisdeane.materialnotes.customviews.CustomTextView;
import uk.me.lewisdeane.materialnotes.utils.Animations;
import uk.me.lewisdeane.materialnotes.utils.DatabaseHelper;

/**
 * Created by Lewis on 05/08/2014.
 */
public class ActionBarFragment extends Fragment {

    private View mRootView;
    public static LinearLayout mContainer, mActionBar1;
    public static ImageButton mMenu;
    public static CustomTextView mHeader, mSubHeader;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_action_bar, container, false);
        init();
        setListeners();
        return mRootView;
    }

    private void init(){
        mContainer = (LinearLayout) mRootView.findViewById(R.id.action_bar_container);
        mActionBar1 = (LinearLayout) mRootView.findViewById(R.id.action_bar_1);

        mMenu = (ImageButton) mRootView.findViewById(R.id.action_bar_1_toggle);

        mHeader = (CustomTextView) mRootView.findViewById(R.id.action_bar_1_header);
        mSubHeader = (CustomTextView) mRootView.findViewById(R.id.action_bar_1_subheader);
    }

    private void setListeners(){
        mMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!MainActivity.isInAdd) {
                    if (!MainActivity.PATH.equals("")) {
                        if (!MainActivity.PATH.contains("/"))
                            MainActivity.PATH = "";
                        else {
                            String[] split = MainActivity.PATH.split("/");

                            String temp = "";

                            for (int i = 0; i < split.length - 1; i++)
                                temp += split[i] + "/";

                            temp = temp.substring(0, temp.length() - 1);

                            MainActivity.PATH = temp;
                        }
                    } else {
                        // Open drawer...
                    }
                } else{
                    Animations.setAddAnimation(true, MainActivity.mFABFragment.mRootView);
                    Animations.setListAnimation(true, MainActivity.mMainFragment.mList);

                    MainActivity.isInAdd = false;

                    MainActivity.mMainFragment.reloadData();

                    MainActivity.mActionBarFragment.setUp();
                }
                MainActivity.mMainFragment.reloadData();

                setUp();
            }
        });
    }

    public void setUp(){
        String[] split = MainActivity.PATH.split("/");
        mSubHeader.setText(split[0].length() == 0 ? getString(R.string.home) : split[split.length-1]);
        if(MainActivity.PATH.length() == 0 && !MainActivity.isInAdd)
            mMenu.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_action_menu_white));
        else {
            mMenu.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_action_arrow_back_white));
        }

        if(MainActivity.isInAdd)
            mSubHeader.setText(getString(R.string.header_add));
    }
}
