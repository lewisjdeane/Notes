package uk.me.lewisdeane.materialnotes.fragments;

import android.app.Fragment;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import uk.me.lewisdeane.materialnotes.R;
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

        amountToMoveDown = -(int) (mDeviceProperties.getHeight());
    }

    private void setListeners() {
        mFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (MainActivity.isInAdd == 1) {
                    if (MainActivity.mAddFragment.mTitle.getText().toString().length() > 0) {
                        NoteItem noteItem = new NoteItem(getActivity(), MainActivity.mAddFragment.mIsFolder, MainActivity.mAddFragment.mTitle.getText().toString().trim(), MainActivity.mAddFragment.mAddItems.get(0).getText().toString().trim(), MainActivity.mAddFragment.mAddItems.get(1).getText().toString(), MainActivity.mAddFragment.mAddItems.get(2).getText().toString(), MainActivity.mAddFragment.mAddItems.get(3).getText().toString(), MainActivity.mAddFragment.mAddItems.get(4).getText().toString());

                        if(MainActivity.mAddFragment.ORIGINAL_NOTE == null) {
                            noteItem.addToDatabase();
                        }
                        else
                            noteItem.editToDatabase(MainActivity.mAddFragment.ORIGINAL_NOTE);

                        Animations.setAddAnimation(true, mRootView);
                        Animations.setListAnimation(true, MainActivity.mMainFragment.mList);

                        MainActivity.isInAdd = 0;

                        MainActivity.mActionBarFragment.setUp(null);
                    }
                } else if(MainActivity.isInAdd == 2){
                    MainActivity.isInAdd = 1;
                    MainActivity.mAddFragment.setEditable();
                } else {
                    Animations.setAddAnimation(false, mRootView);
                    Animations.setListAnimation(false, MainActivity.mMainFragment.mList);

                    if (MainActivity.isInAdd == 0) {
                        MainActivity.isInAdd = 1;
                        MainActivity.mActionBarFragment.setUp(getString(R.string.header_add));
                        MainActivity.mAddFragment.setUp(null);
                    }
                }

                MainActivity.loadNotes();
            }
        });
    }
}
