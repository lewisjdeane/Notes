package uk.me.lewisdeane.materialnotes.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import uk.me.lewisdeane.materialnotes.R;
import uk.me.lewisdeane.materialnotes.dialogs.AddNoteDialog;
import uk.me.lewisdeane.materialnotes.objects.NoteItem;

/**
 * Created by Lewis on 05/08/2014.
 */
public class FABFragment extends Fragment {

    private View mRootView;
    private Button mFAB;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_fab, container, false);
        init();
        setListeners();
        return mRootView;
    }

    private void init(){
        mFAB = (Button) mRootView.findViewById(R.id.fab);
    }

    private void setListeners(){
        mFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AddNoteDialog(getActivity());
            }
        });
    }
}
