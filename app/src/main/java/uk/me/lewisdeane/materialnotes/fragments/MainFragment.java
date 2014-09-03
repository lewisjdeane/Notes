package uk.me.lewisdeane.materialnotes.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.nhaarman.listviewanimations.itemmanipulation.DynamicListView;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.OnDismissCallback;

import java.util.ArrayList;

import uk.me.lewisdeane.materialnotes.R;
import uk.me.lewisdeane.materialnotes.activities.MainActivity;
import uk.me.lewisdeane.materialnotes.adapters.NoteAdapter;
import uk.me.lewisdeane.materialnotes.objects.NoteItem;
import uk.me.lewisdeane.materialnotes.utils.DatabaseHelper;
import uk.me.lewisdeane.materialnotes.utils.DeviceProperties;

/**
 * Created by Lewis on 05/08/2014.
 */
public class MainFragment extends Fragment {

    private static View mRootView;
    public static DynamicListView mList;
    public static NoteAdapter mNoteAdapter;
    public static ArrayList<NoteItem> mNoteItems = new ArrayList<NoteItem>();

    private float START_Y, START_X;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_main, container, false);
        init();
        setListeners();
        return mRootView;
    }

    private void init() {
        mList = (DynamicListView) mRootView.findViewById(R.id.main_list);

        mNoteItems = new DatabaseHelper(getActivity()).getNotesFromDatabase("");

        mNoteAdapter = new NoteAdapter(getActivity(), R.layout.item_note, mNoteItems);

        applyListViewFeatures();

        mList.setAdapter(mNoteAdapter);
    }

    public void setListeners() {
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                MainActivity.openNote(false, mNoteItems.get(i));
            }
        });

        mList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        START_Y = motionEvent.getY();
                        START_X = motionEvent.getX();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        // Check to see if swiping item or scrolling
                        if (Math.abs((START_Y - motionEvent.getY()) / (START_X - motionEvent.getX())) > 3 && Math.abs(START_Y - motionEvent.getY()) > DeviceProperties.convertToPx(40)) {
                            if (MainActivity.FAB_HIDDEN && motionEvent.getY() > START_Y) {
                                MainActivity.mFABFragment.show();
                                if (MainActivity.mUndoFABFragment.mIsCurrentlyShowing)
                                    MainActivity.mUndoFABFragment.show();
                            } else if (!MainActivity.FAB_HIDDEN && motionEvent.getY() < START_Y) {
                                MainActivity.mFABFragment.hide();
                                if (MainActivity.mUndoFABFragment.mIsCurrentlyShowing)
                                    MainActivity.mUndoFABFragment.hide();
                            }
                        }
                }
                return false;
            }
        });
    }

    public static void applyListViewFeatures() {
        mList.enableSwipeToDismiss(
                new OnDismissCallback() {
                    @Override
                    public void onDismiss(@NonNull final ViewGroup listView, @NonNull final int[] reverseSortedPositions) {
                        for (int position : reverseSortedPositions) {
                            MainActivity.deleteNote(mNoteItems.get(position));
                        }
                    }
                }
        );
    }
}
