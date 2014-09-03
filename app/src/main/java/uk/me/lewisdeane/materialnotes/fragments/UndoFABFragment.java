package uk.me.lewisdeane.materialnotes.fragments;

import android.app.Fragment;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import uk.me.lewisdeane.materialnotes.R;
import uk.me.lewisdeane.materialnotes.activities.MainActivity;
import uk.me.lewisdeane.materialnotes.objects.NoteItem;
import uk.me.lewisdeane.materialnotes.utils.Animations;
import uk.me.lewisdeane.materialnotes.utils.DatabaseHelper;

/**
 * Created by Lewis on 05/08/2014.
 */
public class UndoFABFragment extends Fragment {

    public View mRootView;
    public static ImageButton mUndoFAB;
    public static boolean mIsCurrentlyShowing = false;
    private CountDownTimer mTimer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_fab_undo, container, false);
        init();
        setListeners();
        return mRootView;
    }

    private void init() {
        mUndoFAB = (ImageButton) mRootView.findViewById(R.id.fab_undo);

        GradientDrawable bg = (GradientDrawable) mUndoFAB.getBackground();
        bg.setColor(getResources().getColor(R.color.pink_primary));
    }

    private void setListeners() {
        mUndoFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                restoreNotes();
                finish();
            }
        });
    }

    public void hide(){
        Animations.animateUndoFABOut(mRootView);
        MainActivity.UNDO_FAB_HIDDEN = true;
    }

    public void show(){
        Animations.animateUndoFABIn(mRootView);
        MainActivity.UNDO_FAB_HIDDEN = false;
    }

    public void create(){
        if(MainActivity.UNDO_FAB_HIDDEN)
            show();

        mIsCurrentlyShowing = true;

        mTimer = new CountDownTimer(10000, 1000){
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                finish();
            }
        };
        mTimer.start();
    }

    private void finish(){
        hide();
        MainActivity.mDeletedNotes.clear();
        mIsCurrentlyShowing = false;
    }

    private void restoreNotes(){
        for(int i = 0; i < MainActivity.mDeletedNotes.size(); i++){
            NoteItem noteItem = MainActivity.mDeletedNotes.get(i);
            new DatabaseHelper(MainActivity.mContext).addNoteToDatabase(null, noteItem);
        }
        MainActivity.loadNotes();
        mTimer.cancel();
    }
}
