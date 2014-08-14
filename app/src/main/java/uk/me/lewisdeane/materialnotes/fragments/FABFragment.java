package uk.me.lewisdeane.materialnotes.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.Button;

import java.util.Random;

import uk.me.lewisdeane.materialnotes.R;
import uk.me.lewisdeane.ldialogs.CustomDialog;
import uk.me.lewisdeane.materialnotes.activities.MainActivity;
import uk.me.lewisdeane.materialnotes.utils.DeviceProperties;

/**
 * Created by Lewis on 05/08/2014.
 */
public class FABFragment extends Fragment{

    private View mRootView;
    public static Button mFAB;
    private MainFragment mMainFragment;
    private AddFragment mAddFragment;
    private MainActivity mMainActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_fab, container, false);
        init();
        setListeners();
        return mRootView;
    }

    private void init(){
        mFAB = (Button) mRootView.findViewById(R.id.fab);
        mMainFragment = (MainFragment) getFragmentManager().findFragmentById(R.id.fragment_main);
        mAddFragment = (AddFragment) getFragmentManager().findFragmentById(R.id.fragment_add);
        mMainActivity = (MainActivity) getActivity();
    }

    private void setListeners(){
        mFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction().hide(mMainFragment).commit();
                MainActivity.mAddFragment.mTitle.setText("");
                MainActivity.mAddFragment.mItem.setText("");
                mFAB.setVisibility(View.GONE);
            }
        });
    }
}
