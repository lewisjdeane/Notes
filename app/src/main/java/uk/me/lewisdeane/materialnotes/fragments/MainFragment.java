package uk.me.lewisdeane.materialnotes.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import uk.me.lewisdeane.materialnotes.R;
import uk.me.lewisdeane.materialnotes.adapters.NoteAdapter;
import uk.me.lewisdeane.materialnotes.objects.NoteItem;

/**
 * Created by Lewis on 05/08/2014.
 */
public class MainFragment extends Fragment {

    private View mRootView;
    public ListView mList;
    public static NoteAdapter mNoteAdapter;
    public static ArrayList<NoteItem> mNoteItems = new ArrayList<NoteItem>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_main, container, false);
        init();
        setListeners();
        return mRootView;
    }

    private void init(){
        mList = (ListView) mRootView.findViewById(R.id.main_list);

        mNoteAdapter = new NoteAdapter(getActivity(), R.layout.item_note, mNoteItems);

        mList.setAdapter(mNoteAdapter);
    }

    private void setListeners(){

    }
}
