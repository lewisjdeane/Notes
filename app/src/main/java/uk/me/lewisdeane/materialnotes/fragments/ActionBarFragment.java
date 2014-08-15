package uk.me.lewisdeane.materialnotes.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import uk.me.lewisdeane.materialnotes.R;
import uk.me.lewisdeane.materialnotes.customviews.CustomTextView;

/**
 * Created by Lewis on 05/08/2014.
 */
public class ActionBarFragment extends Fragment {

    private View mRootView;
    public static LinearLayout mContainer, mActionBar1;
    public static ImageButton mMenu;
    public static CustomTextView mHeader;

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
    }

    private void setListeners(){

    }
}
