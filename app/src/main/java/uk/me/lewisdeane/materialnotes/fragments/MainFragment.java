package uk.me.lewisdeane.materialnotes.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import de.timroes.android.listview.EnhancedListView;
import uk.me.lewisdeane.materialnotes.R;
import uk.me.lewisdeane.materialnotes.activities.MainActivity;
import uk.me.lewisdeane.materialnotes.adapters.NoteAdapter;
import uk.me.lewisdeane.materialnotes.objects.NoteItem;
import uk.me.lewisdeane.materialnotes.utils.DatabaseHelper;

/**
 * Created by Lewis on 05/08/2014.
 */
public class MainFragment extends Fragment {

    private View mRootView;
    public EnhancedListView mList;
    public static NoteAdapter mNoteAdapter;
    public static ArrayList<NoteItem> mNoteItems = new ArrayList<NoteItem>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_main, container, false);
        init();
        return mRootView;
    }

    private void init() {
        mList = (EnhancedListView) mRootView.findViewById(R.id.main_list);

        mNoteItems = new DatabaseHelper(getActivity()).getNotesFromDatabase();

        mNoteAdapter = new NoteAdapter(getActivity(), R.layout.item_note, mNoteItems);

        mList.setAdapter(mNoteAdapter);

        applyListViewFeatures();
    }

    private void applyListViewFeatures() {
        mList.setDismissCallback(new EnhancedListView.OnDismissCallback() {
            @Override
            public EnhancedListView.Undoable onDismiss(EnhancedListView enhancedListView, final int i) {

                final NoteItem noteItem = mNoteItems.get(i);
                mNoteAdapter.remove(noteItem);

                return new EnhancedListView.Undoable() {
                    @Override
                    public void undo() {
                        mNoteAdapter.insert(noteItem, i);
                    }

                    @Override
                    public String getTitle() {
                        return noteItem.getTitle() + " deleted!";
                    }

                    @Override
                    public void discard() {
                        noteItem.deleteFromDatabase();
                    }
                };
            }
        })
                .enableSwipeToDismiss()
                .setRequireTouchBeforeDismiss(false)
                .setUndoHideDelay(5000)
                .setShouldSwipeCallback(new EnhancedListView.OnShouldSwipeCallback() {
                    @Override
                    public boolean onShouldSwipe(EnhancedListView enhancedListView, int i) {
                        if (mNoteItems.get(i).getIsFolder())
                            return false;
                        return true;
                    }
                });
    }

    public void onItemClicked(NoteItem _noteItem) {
        if (_noteItem.getIsFolder()) {
            MainActivity.PATH += "/" + _noteItem.getTitle();
            MainActivity.mActionBarFragment.mHeader.setText(_noteItem.getTitle());
            mNoteItems.clear();
            mNoteItems = new DatabaseHelper(getActivity()).getNotesFromDatabase();
            mNoteAdapter.notifyDataSetChanged();

        }
    }
}
