package uk.me.lewisdeane.materialnotes.fragments;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Fragment;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import uk.me.lewisdeane.materialnotes.R;
import uk.me.lewisdeane.materialnotes.activities.MainActivity;
import uk.me.lewisdeane.materialnotes.adapters.AddAdapter;
import uk.me.lewisdeane.materialnotes.adapters.NoteAdapter;
import uk.me.lewisdeane.materialnotes.objects.AddItem;
import uk.me.lewisdeane.materialnotes.objects.NoteItem;
import uk.me.lewisdeane.materialnotes.utils.DeviceProperties;

/**
 * Created by Lewis on 13/08/2014.
 */
public class AddFragment extends Fragment {

    private View mRootView;
    public static LinearLayout mContainer, mPrimaryContainer;
    public static EditText mTitle;
    public static ListView mList;
    public static ImageButton mFolder;
    public static boolean mIsFolder;

    private static AddAdapter mAddAdapter;
    public static ArrayList<AddItem> mAddItems = new ArrayList<AddItem>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_add, container, false);
        init();
        setListeners();
        return mRootView;
    }

    private void init() {
        mContainer = (LinearLayout) mRootView.findViewById(R.id.fragment_add_container);
        mPrimaryContainer = (LinearLayout) mRootView.findViewById(R.id.fragment_add_primary_container);

        mTitle = (EditText) mRootView.findViewById(R.id.fragment_add_title);
        mFolder = (ImageButton) mRootView.findViewById(R.id.fragment_add_folder);
        mList = (ListView) mRootView.findViewById(R.id.fragment_add_list);

        loadList();

        mAddAdapter = new AddAdapter(getActivity(), R.layout.item_add, mAddItems);
        mList.setAdapter(mAddAdapter);
    }

    private void setListeners() {
        mFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MainActivity.isInAdd && !MainActivity.isInView) {
                    mIsFolder = !mIsFolder;
                    mFolder.setImageDrawable(getActivity().getResources().getDrawable(mIsFolder ? R.drawable.ic_action_folder_white_selected : R.drawable.ic_action_folder_white_not_selected));
                }
            }
        });
    }

    private void loadList() {
        for (int i = 0; i < 5; i++) {
            mAddItems.add(new AddItem(i, getHintFromPos(i), getImgFromPos(i)));
        }
    }

    public String getHintFromPos(int _pos) {
        switch (_pos) {
            case 0:
                return getString(R.string.add_item_hint);
            case 1:
                return getString(R.string.add_time_hint);
            case 2:
                return getString(R.string.add_date_hint);
            case 3:
                return getString(R.string.add_tags_hint);
            case 4:
                return getString(R.string.add_link_hint);
            default:
                return getString(R.string.add_item_hint);
        }
    }

    public Drawable getImgFromPos(int _pos) {
        switch (_pos) {
            case 0:
                return getActivity().getResources().getDrawable(R.drawable.ic_content);
            case 1:
                return getActivity().getResources().getDrawable(R.drawable.ic_time);
            case 2:
                return getActivity().getResources().getDrawable(R.drawable.ic_date);
            case 3:
                return getActivity().getResources().getDrawable(R.drawable.ic_tags);
            case 4:
                return getActivity().getResources().getDrawable(R.drawable.ic_link);
            default:
                return getActivity().getResources().getDrawable(R.drawable.ic_content);
        }
    }

    public void prepare(NoteItem _noteItem) {
        /*
        if(_noteItem == null) {
            mTitle.setText("");
            mItem.setText("");
            mIsFolder = false;
            mFolder.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_action_folder_white_not_selected));
            mItem.setVisibility(View.VISIBLE);

            mTitle.requestFocus();
        } else{
            mTitle.setText(_noteItem.getTitle());
            mItem.setText(_noteItem.getItem());
            mIsFolder = _noteItem.getIsFolder();
            mFolder.setImageDrawable(getActivity().getResources().getDrawable(mIsFolder ? R.drawable.ic_action_folder_white_selected : R.drawable.ic_action_folder_white_not_selected));
        }
    }
    */
    }
}
