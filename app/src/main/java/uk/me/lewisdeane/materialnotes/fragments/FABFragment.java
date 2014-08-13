package uk.me.lewisdeane.materialnotes.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.Random;

import uk.me.lewisdeane.materialnotes.R;
import uk.me.lewisdeane.ldialogs.CustomDialog;

/**
 * Created by Lewis on 05/08/2014.
 */
public class FABFragment extends Fragment{

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
                Random random = new Random();
                int i = random.nextInt(3);
                if(i == 0) {
                    CustomDialog customDialog = new CustomDialog(getActivity(), "Thanks", "", "KK", "NP");

                    customDialog.show();
                }

                if(i == 1){
                    CustomDialog customDialog = new CustomDialog(getActivity());
                    customDialog.setTitle("Permissions");
                    customDialog.setContent("This app determines your phone's location and shares it with Google in order to serve personalised alerts to you. This allows for a better overall experience.");
                    customDialog.setConfirm("Accept");
                    customDialog.setCancel("Decline");
                    customDialog.setConfirmColour("#5677fc");
                    customDialog.show();
                }

                if(i == 2){
                    CustomDialog customDialog = new CustomDialog(getActivity(), "Wanker", "Yes you.", "SORRY");
                    customDialog.show();
                }
            }
        });
    }
}
