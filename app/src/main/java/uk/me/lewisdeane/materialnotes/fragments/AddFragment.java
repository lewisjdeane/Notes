package uk.me.lewisdeane.materialnotes.fragments;

import android.app.Fragment;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;

import uk.me.lewisdeane.materialnotes.R;
import uk.me.lewisdeane.materialnotes.activities.MainActivity;
import uk.me.lewisdeane.materialnotes.adapters.AddAdapter;
import uk.me.lewisdeane.materialnotes.objects.AddItem;
import uk.me.lewisdeane.materialnotes.objects.NoteItem;

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

    public static NoteItem ORIGINAL_NOTE;

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
                if (MainActivity.isInAdd == 1) {
                    mIsFolder = !mIsFolder;
                    mFolder.setImageDrawable(getActivity().getResources().getDrawable(mIsFolder ? R.drawable.ic_action_folder_white_selected : R.drawable.ic_action_folder_white_not_selected));
                }
            }
        });
    }

    public void setUp(NoteItem _noteItem){
        ORIGINAL_NOTE = null;
        mIsFolder = false;
        mFolder.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_action_folder_white_not_selected));

        mTitle.setText("");
        mTitle.setClickable(_noteItem != null && MainActivity.isInAdd == 2 ? false : true);
        mTitle.setFocusable(_noteItem != null && MainActivity.isInAdd == 2 ? false : true);
        mTitle.setFocusableInTouchMode(_noteItem != null && MainActivity.isInAdd == 2 ? false : true);

        for(AddItem addItem : mAddItems)
            addItem.setText("");

        if(_noteItem != null) {
            ORIGINAL_NOTE = _noteItem;
            mAddItems.get(0).setText(_noteItem.getItem());
            mAddItems.get(1).setText(_noteItem.getTime());
            mAddItems.get(2).setText(_noteItem.getDate());
            mAddItems.get(3).setText(_noteItem.getTags());
            mAddItems.get(4).setText(_noteItem.getLink());

            mTitle.setText(_noteItem.getTitle());
            mFolder.setImageDrawable(getActivity().getResources().getDrawable(mIsFolder ? R.drawable.ic_action_folder_white_selected : R.drawable.ic_action_folder_white_not_selected));
        }

        mAddAdapter.notifyDataSetChanged();
    }

    public void setEditable(){
        mTitle.setClickable(true);
        mTitle.setFocusable(true);
        mTitle.setFocusableInTouchMode(true);

        mAddAdapter.notifyDataSetChanged();
    }

    private void loadList() {
        mAddItems.clear();
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
}
