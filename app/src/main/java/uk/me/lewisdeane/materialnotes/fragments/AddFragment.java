package uk.me.lewisdeane.materialnotes.fragments;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
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
import android.widget.ImageButton;
import android.widget.ImageView;
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
    public static LinearLayout mContainer, mPrimaryContainer, mSecondaryContainer;
    private MainFragment mMainFragment;
    private FABFragment mFABFragment;
    public static EditText mTitle, mItem;
    public static ImageButton mFolder;
    public static boolean mIsFolder;
    private float heightToMove;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_add, container, false);
        init();
        setListeners();
        return mRootView;
    }

    private void init() {
        mContainer = (LinearLayout) mRootView.findViewById(R.id.fragment_add_container);
        mPrimaryContainer = (LinearLayout) mRootView.findViewById(R.id.fragment_add_primary_container);
        mSecondaryContainer = (LinearLayout) mRootView.findViewById(R.id.fragment_add_secondary_container);

        mTitle = (EditText) mRootView.findViewById(R.id.fragment_add_title);
        mItem = (EditText) mRootView.findViewById(R.id.fragment_add_item);
        mFolder = (ImageButton) mRootView.findViewById(R.id.fragment_add_folder);

        mMainFragment = (MainFragment) getFragmentManager().findFragmentById(R.id.fragment_main);
        mFABFragment = (FABFragment) getFragmentManager().findFragmentById(R.id.fragment_fab);

        heightToMove = mItem.getLayoutParams().height;
    }

    private void setListeners() {
        mFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mIsFolder = !mIsFolder;
                mFolder.setImageDrawable(getActivity().getResources().getDrawable(mIsFolder ? R.drawable.ic_action_folder_white_selected : R.drawable.ic_action_folder_white_not_selected));

                float toMove = -(new DeviceProperties(getActivity()).convertToPx(80));

                ValueAnimator varl = ValueAnimator.ofInt(!mIsFolder ? -(int)heightToMove : 0, !mIsFolder ? 0 : -(int)heightToMove);
                varl.setDuration(250);
                varl.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mSecondaryContainer.getLayoutParams();
                        lp.setMargins(0, (Integer) animation.getAnimatedValue(), 0, 0);
                        mSecondaryContainer.setLayoutParams(lp);
                    }
                });
                varl.start();

                ObjectAnimator.ofFloat(MainActivity.mFABFragment.mRootView, "translationY", !mIsFolder ? MainActivity.mFABFragment.amountToMoveDown + toMove : MainActivity.mFABFragment.amountToMoveDown, !mIsFolder ? MainActivity.mFABFragment.amountToMoveDown : MainActivity.mFABFragment.amountToMoveDown + toMove).setDuration(250).start();

            }
        });
    }

    public void prepare(){
        mTitle.setText("");
        mItem.setText("");
        mIsFolder = false;
        mFolder.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_action_folder_white_not_selected));
        mItem.setVisibility(View.VISIBLE);
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mSecondaryContainer.getLayoutParams();
        lp.setMargins(0, 0, 0, 0);
        mSecondaryContainer.setLayoutParams(lp);

        mTitle.requestFocus();
    }
}
