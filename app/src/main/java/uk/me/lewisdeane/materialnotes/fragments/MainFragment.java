package uk.me.lewisdeane.materialnotes.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.nhaarman.listviewanimations.itemmanipulation.DynamicListView;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.OnDismissCallback;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.SwipeDismissAdapter;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.undo.SimpleSwipeUndoAdapter;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.undo.TimedUndoAdapter;

import java.util.ArrayList;

import de.timroes.android.listview.EnhancedListView;
import uk.me.lewisdeane.materialnotes.R;
import uk.me.lewisdeane.materialnotes.activities.MainActivity;
import uk.me.lewisdeane.materialnotes.adapters.NoteAdapter;
import uk.me.lewisdeane.materialnotes.objects.NoteItem;
import uk.me.lewisdeane.materialnotes.utils.Animations;
import uk.me.lewisdeane.materialnotes.utils.DatabaseHelper;

/**
 * Created by Lewis on 05/08/2014.
 */
public class MainFragment extends Fragment {

    private static View mRootView;
    public static DynamicListView mList;
    public static NoteAdapter mNoteAdapter;
    public static ArrayList<NoteItem> mNoteItems = new ArrayList<NoteItem>();
    private SwipeDismissAdapter mSwipeDismissAdapter;

    private static Context mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_main, container, false);
        init();
        setListeners();
        return mRootView;
    }

    private void init() {
        mContext = getActivity();

        mList = (DynamicListView) mRootView.findViewById(R.id.main_list);

        mNoteItems = new DatabaseHelper(getActivity()).getNotesFromDatabase();

        mNoteAdapter = new NoteAdapter(getActivity(), R.layout.item_note, mNoteItems);

        applyListViewFeatures();
        mList.setAdapter(mNoteAdapter);
    }

    public void setListeners(){
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (mNoteItems.get(i).getIsFolder()) {
                    if(MainActivity.PATH.length() > 0)
                        MainActivity.PATH += "/" + mNoteItems.get(i).getTitle();
                    else
                        MainActivity.PATH += mNoteItems.get(i).getTitle();
                } else {
                    MainActivity.isInAdd = 2;
                    MainActivity.mAddFragment.setUp(mNoteItems.get(i));

                    Animations.setAddAnimation(false, MainActivity.mFABFragment.mRootView);
                    Animations.setListAnimation(false, MainActivity.mMainFragment.mList);
                }
                MainActivity.mActionBarFragment.setUp(mNoteItems.get(i).getTitle());
                MainActivity.loadNotes();
            }
        });
    }

    public static void applyListViewFeatures() {
        mList.enableSwipeToDismiss(
                new OnDismissCallback() {
                    @Override
                    public void onDismiss(@NonNull final ViewGroup listView, @NonNull final int[] reverseSortedPositions) {
                        for (int position : reverseSortedPositions) {
                            NoteItem noteItem = mNoteItems.get(position);
                            noteItem.deleteFromDatabase();
                            mNoteAdapter.remove(noteItem);
                            mNoteAdapter.notifyDataSetChanged();
                        }
                    }
                }
        );
    }
}
