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
                    if (MainActivity.mAddFragment.mTitle.getText().toString().length() > 0) {
                        NoteItem noteItem = new NoteItem(getActivity(), MainActivity.mAddFragment.mTitle.getText().toString().trim(), MainActivity.mAddFragment.mItem.getText().toString().trim(), MainActivity.mAddFragment.mIsFolder);

                        noteItem.addToDatabase();
                        MainActivity.mMainFragment.mNoteAdapter.add(noteItem);

                        Animations.setAddAnimation(true, mRootView);
                        Animations.setListAnimation(true, MainActivity.mMainFragment.mList);

                        MainActivity.isInAdd = false;

                        MainActivity.mMainFragment.applyListViewFeatures();
                    }
                } else {
                    MainActivity.mAddFragment.prepare();

                    Animations.setAddAnimation(false, mRootView);
                    Animations.setListAnimation(false, MainActivity.mMainFragment.mList);

                    MainActivity.isInAdd = true;
                }
            }
        });
    }
}
