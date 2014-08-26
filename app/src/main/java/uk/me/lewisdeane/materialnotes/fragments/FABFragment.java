package uk.me.lewisdeane.materialnotes.fragments;

import android.app.Fragment;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import uk.me.lewisdeane.materialnotes.R;
import uk.me.lewisdeane.materialnotes.activities.MainActivity;
import uk.me.lewisdeane.materialnotes.objects.NoteItem;
import uk.me.lewisdeane.materialnotes.utils.Animations;
import uk.me.lewisdeane.materialnotes.utils.Misc;

/**
 * Created by Lewis on 05/08/2014.
 */
public class FABFragment extends Fragment {

    public View mRootView;
    public static ImageButton mFAB;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_fab, container, false);
        init();
        setListeners();
        return mRootView;
    }

    private void init() {
        mFAB = (ImageButton) mRootView.findViewById(R.id.fab);

        GradientDrawable bg = (GradientDrawable) mFAB.getBackground();
        bg.setColor(getResources().getColor(R.color.pink_primary));
    }

    private void setListeners() {
        mFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (MainActivity.ADD_MODE == MainActivity.AddMode.ADD) {
                    if (MainActivity.mAddFragment.mTitle.getText().toString().length() > 0) {

                        boolean isFolder = MainActivity.mAddFragment.mIsFolder;
                        EditText titleView = MainActivity.mAddFragment.mTitle;
                        EditText[] itemViews = MainActivity.mAddFragment.mItemViews;

                        // Build a new NoteItem from the inputted data.
                        NoteItem.Builder builder = new NoteItem.Builder(MainActivity.PATH + titleView.getText().toString().trim(), isFolder, titleView.getText().toString());
                        builder.item(itemViews[0].getText().toString())
                                .time(itemViews[1].getText().toString())
                                .date(itemViews[2].getText().toString())
                                .link(itemViews[3].getText().toString());
                        NoteItem noteItem = builder.build();

                        if(MainActivity.mAddFragment.ORIGINAL_NOTE == null)
                            noteItem.addToDatabase();
                        else
                            noteItem.editToDatabase(MainActivity.mAddFragment.ORIGINAL_NOTE);

                        MainActivity.mActionBarFragment.goBack(false);
                        Misc.hideKeyboard();
                    }
                } else if(MainActivity.ADD_MODE == MainActivity.AddMode.VIEW){
                    MainActivity.ADD_MODE = MainActivity.AddMode.ADD;
                } else {
                    Animations.setAddAnimation(false, mRootView);
                    Animations.setListAnimation(false, MainActivity.mMainFragment.mList);

                    if (MainActivity.ADD_MODE == MainActivity.AddMode.NONE) {
                        MainActivity.ADD_MODE = MainActivity.AddMode.ADD;
                        MainActivity.mActionBarFragment.setUp("");
                        MainActivity.mAddFragment.setUp(null);
                    }
                }

                MainActivity.loadNotes();
            }
        });
    }
}
