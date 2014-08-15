package uk.me.lewisdeane.materialnotes.fragments;

import android.animation.ObjectAnimator;
import android.app.Fragment;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.TranslateAnimation;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import java.util.Random;

import uk.me.lewisdeane.materialnotes.R;
import uk.me.lewisdeane.ldialogs.CustomDialog;
import uk.me.lewisdeane.materialnotes.activities.MainActivity;
import uk.me.lewisdeane.materialnotes.objects.NoteItem;
import uk.me.lewisdeane.materialnotes.utils.Animations;
import uk.me.lewisdeane.materialnotes.utils.DeviceProperties;

/**
 * Created by Lewis on 05/08/2014.
 */
public class FABFragment extends Fragment {

    public View mRootView;
    public static Button mFAB;
    private MainFragment mMainFragment;
    private AddFragment mAddFragment;
    private MainActivity mMainActivity;
    private DeviceProperties mDeviceProperties;
    public static int amountToMoveDown;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_fab, container, false);
        init();
        setListeners();
        return mRootView;
    }

    private void init() {
        mFAB = (Button) mRootView.findViewById(R.id.fab);
        mMainFragment = (MainFragment) getFragmentManager().findFragmentById(R.id.fragment_main);
        mAddFragment = (AddFragment) getFragmentManager().findFragmentById(R.id.fragment_add);
        mMainActivity = (MainActivity) getActivity();
        mDeviceProperties = new DeviceProperties(getActivity());

        GradientDrawable bg = (GradientDrawable) mFAB.getBackground();
        bg.setColor(Color.parseColor("#ff9900"));

        amountToMoveDown = -(int) (mDeviceProperties.getHeight() - mDeviceProperties.convertToPx(82));
    }

    private void setListeners() {
        mFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (MainActivity.isInAdd) {
                    if(MainActivity.mAddFragment.mTitle.getText().toString().length() > 0) {
                        NoteItem noteItem = new NoteItem(getActivity(), MainActivity.mAddFragment.mTitle.getText().toString(), MainActivity.mAddFragment.mItem.getText().toString(), MainActivity.mAddFragment.mIsFolder);
                        noteItem.addToDatabase();

                        MainActivity.mMainFragment.mNoteItems.add(noteItem);
                        MainActivity.mMainFragment.mNoteAdapter.notifyDataSetChanged();

                        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(mRootView, "translationY", amountToMoveDown, 0);
                        objectAnimator.setDuration(250);
                        objectAnimator.start();

                        ObjectAnimator objectAnimator2 = ObjectAnimator.ofFloat(MainActivity.mMainFragment.mList, "translationY", mDeviceProperties.getScreenHeightWithoutPadding(), 0);
                        objectAnimator2.setDuration(250);
                        objectAnimator2.start();

                        MainActivity.isInAdd = false;
                    } else{
                    }
                } else {
                    ObjectAnimator objectAnimator2 = ObjectAnimator.ofFloat(MainActivity.mMainFragment.mList, "translationY", 0, mDeviceProperties.getScreenHeightWithoutPadding());
                    objectAnimator2.setDuration(250);
                    objectAnimator2.start();

                    MainActivity.mAddFragment.mTitle.setText("");
                    MainActivity.mAddFragment.mItem.setText("");
                    MainActivity.mAddFragment.mIsFolder = false;
                    MainActivity.mAddFragment.mFolder.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_action_folder_white_not_selected));
                    MainActivity.mAddFragment.mItem.setVisibility(View.VISIBLE);
                    LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) MainActivity.mAddFragment.mSecondaryContainer.getLayoutParams();
                    lp.setMargins(0, 0, 0, 0);
                    MainActivity.mAddFragment.mSecondaryContainer.setLayoutParams(lp);

                    MainActivity.mAddFragment.mTitle.requestFocus();

                    ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(mRootView, "translationY", 0, amountToMoveDown);
                    objectAnimator.setDuration(250);
                    objectAnimator.start();

                    MainActivity.isInAdd = true;
                }
            }
        });
    }
}
