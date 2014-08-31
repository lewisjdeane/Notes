package uk.me.lewisdeane.materialnotes.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import uk.me.lewisdeane.materialnotes.R;
import uk.me.lewisdeane.materialnotes.activities.MainActivity;
import uk.me.lewisdeane.materialnotes.objects.NoteItem;

/**
 * Created by Lewis on 13/08/2014.
 */
public class AddFragment extends Fragment {

    private View mRootView;
    public static LinearLayout mContainer, mPrimaryContainer;
    public static EditText mTitle;
    public static ImageButton mFolder;
    public static boolean mIsFolder;

    public static String[] mItems = new String[4];

    public static NoteItem ORIGINAL_NOTE;

    private static Context mContext;

    public static EditText[] mItemViews = new EditText[4];
    private LinearLayout[] mContainerViews = new LinearLayout[4];
    private ImageView[] mImageViews = new ImageView[4], mClearViews = new ImageView[4];

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_add, container, false);
        init();
        setListeners();
        return mRootView;
    }

    private void init() {
        mContext = getActivity();

        mContainer = (LinearLayout) mRootView.findViewById(R.id.fragment_add_container);
        mPrimaryContainer = (LinearLayout) mRootView.findViewById(R.id.fragment_add_primary_container);

        mTitle = (EditText) mRootView.findViewById(R.id.fragment_add_title);
        mFolder = (ImageButton) mRootView.findViewById(R.id.fragment_add_folder);

        mContainerViews[0] = (LinearLayout) mRootView.findViewById(R.id.item_add_container_1);
        mContainerViews[1] = (LinearLayout) mRootView.findViewById(R.id.item_add_container_2);
        mContainerViews[2] = (LinearLayout) mRootView.findViewById(R.id.item_add_container_3);
        mContainerViews[3] = (LinearLayout) mRootView.findViewById(R.id.item_add_container_4);

        mItemViews[0] = (EditText) mRootView.findViewById(R.id.item_add_text_1);
        mItemViews[1] = (EditText) mRootView.findViewById(R.id.item_add_text_2);
        mItemViews[2] = (EditText) mRootView.findViewById(R.id.item_add_text_3);
        mItemViews[3] = (EditText) mRootView.findViewById(R.id.item_add_text_4);

        mImageViews[0] = (ImageView) mRootView.findViewById(R.id.item_add_img_1);
        mImageViews[1] = (ImageView) mRootView.findViewById(R.id.item_add_img_2);
        mImageViews[2] = (ImageView) mRootView.findViewById(R.id.item_add_img_3);
        mImageViews[3] = (ImageView) mRootView.findViewById(R.id.item_add_img_4);

        mClearViews[0] = (ImageView) mRootView.findViewById(R.id.item_add_clear_1);
        mClearViews[1] = (ImageView) mRootView.findViewById(R.id.item_add_clear_2);
        mClearViews[2] = (ImageView) mRootView.findViewById(R.id.item_add_clear_3);
        mClearViews[3] = (ImageView) mRootView.findViewById(R.id.item_add_clear_4);

        for(int i = 0; i < mContainerViews.length; i++){
            final int k = i;
            mClearViews[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mItemViews[k].setText("");
                }
            });

            mItemViews[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                    mClearViews[k].setVisibility(mItemViews[k].getText().toString().length() > 0 ? View.VISIBLE : View.GONE);
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
        }


        loadList();
    }

    private void setListeners() {
        mFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MainActivity.ADD_MODE == MainActivity.AddMode.ADD) {
                    mIsFolder = !mIsFolder;
                    mFolder.setImageDrawable(getActivity().getResources().getDrawable(mIsFolder ? R.drawable.ic_action_folder_white_selected : R.drawable.ic_action_folder_white_not_selected));
                }
            }
        });
    }

    public void setUp(boolean _shouldEdit, NoteItem _noteItem){
        ORIGINAL_NOTE = _noteItem;
        mIsFolder = false;
        mFolder.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_action_folder_white_not_selected));

        mTitle.setText("");;
        mTitle.setClickable(_shouldEdit);
        mTitle.setFocusable(_shouldEdit);
        mTitle.setFocusableInTouchMode(_shouldEdit);

        for(int i = 0; i < mItems.length; i++)
            mItems[i] = "";

        if(_noteItem != null) {
            ORIGINAL_NOTE = _noteItem;
            mItems[0] = _noteItem.getItem();
            mItems[1] = _noteItem.getTime();
            mItems[2] = _noteItem.getDate();
            mItems[3] = _noteItem.getLink();

            mTitle.setText(_noteItem.getTitle());
            mFolder.setImageDrawable(getActivity().getResources().getDrawable(mIsFolder ? R.drawable.ic_action_folder_white_selected : R.drawable.ic_action_folder_white_not_selected));
        }
        updateViews(_shouldEdit);
    }

    private void updateViews(boolean _editable){
        for(int i = 0; i < mContainerViews.length; i++){
            mItemViews[i].setText(mItems[i]);
            mItemViews[i].setClickable(_editable);
            mItemViews[i].setFocusable(_editable);
            mItemViews[i].setFocusableInTouchMode(_editable);
        }
    }

    private void loadList() {
        for (int i = 0; i < mContainerViews.length; i++) {
            mItems[i] = mItemViews[i].getText().toString();
        }
    }
}
