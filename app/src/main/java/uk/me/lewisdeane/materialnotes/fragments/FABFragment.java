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
import uk.me.lewisdeane.materialnotes.utils.DatabaseHelper;
import uk.me.lewisdeane.materialnotes.utils.Misc;

import static uk.me.lewisdeane.materialnotes.activities.MainActivity.AddMode;
import static uk.me.lewisdeane.materialnotes.activities.MainActivity.NoteMode;
import static uk.me.lewisdeane.materialnotes.activities.MainActivity.PATH;
import static uk.me.lewisdeane.materialnotes.activities.MainActivity.closeAdd;
import static uk.me.lewisdeane.materialnotes.activities.MainActivity.loadNotes;
import static uk.me.lewisdeane.materialnotes.activities.MainActivity.mActionBarFragment;
import static uk.me.lewisdeane.materialnotes.activities.MainActivity.mAddFragment;
import static uk.me.lewisdeane.materialnotes.activities.MainActivity.mAddMode;
import static uk.me.lewisdeane.materialnotes.activities.MainActivity.mContext;
import static uk.me.lewisdeane.materialnotes.activities.MainActivity.mIsCurrentlyShowing;
import static uk.me.lewisdeane.materialnotes.activities.MainActivity.mNoteMode;
import static uk.me.lewisdeane.materialnotes.activities.MainActivity.openAdd;

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

                /*
                Here we check current state:

                Check if fab is hidden and if so animate it back up.

                FAB on click.
                None - Animate icon to tick, animate it up to correct position, change Mode to edit.
                View - Animate icon to tick, change mode to edit.
                Edit - Animate icon to add, animate it back down to correct position, change mode to none.

                Note on click open.
                None - animate icon to edit, animate to correct position, change mode to view.

                Note on click edit.
                None - animate icon to tick, animate to correct position, change mode to edit and populate data.

                Folder on click edit.
                None - animate icon to tick, animate to correct position, change mode to edit and populate data.
                 */


                if(mNoteMode == NoteMode.ARCHIVE){
                    new DatabaseHelper(mContext).deleteArchiveDatabase();
                } else {
                    if (mAddMode == AddMode.NONE) {

                        mAddMode = AddMode.ADD;
                        openAdd(true, null);

                        if (mIsCurrentlyShowing) {
                            MainActivity ma = (MainActivity) getActivity();
                            ma.finishSnackbar(false);
                            ma.mSnackbar.dismiss();
                        }

                    } else if (mAddMode == AddMode.ADD) {

                        boolean isFolder = mAddFragment.mIsFolder;
                        EditText titleView = mAddFragment.mTitle;
                        EditText[] itemViews = mAddFragment.mItemViews;

                        if (titleView.getText().toString().trim().length() > 0 && !titleView.getText().toString().contains("/")) {
                            closeAdd();

                            // Build a new NoteItem from the inputted data.
                            NoteItem.Builder builder = new NoteItem.Builder(PATH + titleView.getText().toString().trim(), isFolder, titleView.getText().toString());
                            builder.item(itemViews[0].getText().toString())
                                    .time(itemViews[1].getText().toString())
                                    .date(itemViews[2].getText().toString())
                                    .link(itemViews[3].getText().toString());
                            NoteItem noteItem = builder.build();

                            if (mAddFragment.ORIGINAL_NOTE == null)
                                noteItem.addToDatabase();
                            else
                                noteItem.editToDatabase(mAddFragment.ORIGINAL_NOTE);

                            mActionBarFragment.goBack(false);
                            mAddMode = MainActivity.AddMode.NONE;

                            mAddFragment.mTitle.clearFocus();

                        } else if (titleView.getText().toString().length() == 0) {
                            Misc.toast("Title required.");
                        } else {
                            Misc.toast("'/' is not an allowed character in the title");
                        }

                    } else if (mAddMode == AddMode.VIEW) {
                        mAddMode = MainActivity.AddMode.ADD;
                        Animations.animateFAB(false, false, MainActivity.mContext.getResources().getDrawable(R.drawable.ic_fab_done));
                        mAddFragment.setUp(true, MainActivity.mAddFragment.ORIGINAL_NOTE);
                    }
                }

                loadNotes();
            }
        });
    }

    public void hide(){
        Animations.animateFABInOut(false, mRootView);
        MainActivity.FAB_HIDDEN = true;
    }

    public void show(){
        Animations.animateFABInOut(true, mRootView);
        MainActivity.FAB_HIDDEN = false;
    }
}
