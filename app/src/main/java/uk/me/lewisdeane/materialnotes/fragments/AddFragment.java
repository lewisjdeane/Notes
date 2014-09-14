package uk.me.lewisdeane.materialnotes.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TimePicker;

import uk.me.lewisdeane.ldialogs.CustomDialog;
import uk.me.lewisdeane.materialnotes.R;
import uk.me.lewisdeane.materialnotes.activities.MainActivity;
import uk.me.lewisdeane.materialnotes.objects.NoteItem;
import uk.me.lewisdeane.materialnotes.utils.Animations;

/**
 * Created by Lewis on 13/08/2014.
 */
public class AddFragment extends Fragment {

    private View mRootView;
    public static LinearLayout mContainer, mPrimaryContainer;
    public static EditText mTitle;
    public static ImageButton mFolder;

    public static String[] mItems = new String[4];

    public static NoteItem ORIGINAL_NOTE;

    private static Context mContext;

    public static EditText[] mItemViews = new EditText[4];
    private LinearLayout[] mContainerViews = new LinearLayout[4];
    private ImageView[] mImageViews = new ImageView[4], mClearViews = new ImageView[4];
    public static ScrollView mScrollView;
    public static boolean mIsFolder = false;

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

        mScrollView = (ScrollView) mRootView.findViewById(R.id.item_add_scroll_container);

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

        for (int i = 0; i < mContainerViews.length; i++) {
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

        mItemViews[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final TimePicker timePicker = new TimePicker(getActivity());
                if (mItemViews[1].getText().toString().length() > 0) {
                    String[] timeParts = mItemViews[1].getText().toString().split(":");
                    timePicker.setCurrentHour(Integer.parseInt(timeParts[0]));
                    timePicker.setCurrentMinute(Integer.parseInt(timeParts[1]));
                }
                CustomDialog.Builder builder = new CustomDialog.Builder(getActivity(), "Time", "Done");
                builder.negativeText("Cancel");
                builder.positiveColor("#4285F4");
                CustomDialog customDialog = builder.build();
                customDialog.setCustomView(timePicker);
                customDialog.show();

                customDialog.setClickListener(new CustomDialog.ClickListener() {
                    @Override
                    public void onConfirmClick() {
                        mItemViews[1].setText(timePicker.getCurrentHour() + ":" + timePicker.getCurrentMinute());
                    }

                    @Override
                    public void onCancelClick() {
                    }
                });
            }
        });

        mItemViews[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DatePicker datePicker = new DatePicker(getActivity());
                datePicker.setCalendarViewShown(false);
                if (mItemViews[2].getText().toString().length() > 0) {
                    String[] dateParts = mItemViews[2].getText().toString().split("/");
                    datePicker.init(Integer.parseInt(dateParts[2]), Integer.parseInt(dateParts[1])-1, Integer.parseInt(dateParts[0]), null);
                }
                CustomDialog.Builder builder = new CustomDialog.Builder(getActivity(), "Date", "Done");
                builder.positiveColor("#4285F4");
                builder.negativeText("Cancel");
                CustomDialog customDialog = builder.build();
                customDialog.setCustomView(datePicker);
                customDialog.show();

                customDialog.setClickListener(new CustomDialog.ClickListener() {
                    @Override
                    public void onConfirmClick() {
                        mItemViews[2].setText(datePicker.getDayOfMonth() + "/" + (datePicker.getMonth() + 1) + "/" + datePicker.getYear());
                    }

                    @Override
                    public void onCancelClick() {
                    }
                });
            }
        });


        loadList();
    }

    private void setListeners() {
        mFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MainActivity.mAddMode == MainActivity.AddMode.ADD) {
                    Animations.animateScroll(mScrollView, mIsFolder);
                    mIsFolder = !mIsFolder;
                    mFolder.setImageDrawable(getActivity().getResources().getDrawable(mIsFolder ? R.drawable.ic_action_folder_white_selected : R.drawable.ic_action_folder_white_not_selected));
                }
            }
        });
    }

    public void setUp(boolean _shouldEdit, NoteItem _note) {
        Animations.putScrollBack(mScrollView);
        ORIGINAL_NOTE = _note;
        mFolder.setVisibility(_shouldEdit ? View.VISIBLE : View.GONE);

        mIsFolder = false;

        mTitle.setText("");
        mTitle.clearFocus();
        mTitle.setClickable(_shouldEdit);
        mTitle.setFocusable(_shouldEdit);
        mTitle.setFocusableInTouchMode(_shouldEdit);

        for (int i = 0; i < mItems.length; i++)
            mItems[i] = "";

        if (_note != null) {
            ORIGINAL_NOTE = _note;
            mItems[0] = _note.getItem();
            mItems[1] = _note.getTime();
            mItems[2] = _note.getDate();
            mItems[3] = _note.getLink();

            mTitle.setText(_note.getTitle());
            mTitle.clearFocus();
            mFolder.setVisibility(View.GONE);
            mIsFolder = _note.getNoteType() == NoteItem.NoteType.NOTE ? false : true;

            mScrollView.setVisibility(mIsFolder ? View.GONE : View.VISIBLE);
        } else {
            mFolder.setVisibility(View.VISIBLE);
            mScrollView.setVisibility(View.VISIBLE);
        }
        mFolder.setImageDrawable(getActivity().getResources().getDrawable(mIsFolder ? R.drawable.ic_action_folder_white_selected : R.drawable.ic_action_folder_white_not_selected));
        updateViews(_shouldEdit);
    }

    private void updateViews(boolean _editable) {
        for (int i = 0; i < mContainerViews.length; i++) {
            mItemViews[i].setText(mItems[i]);
            mItemViews[i].setClickable(_editable);
            if (i != 1 && i != 2) {
                mItemViews[i].setFocusable(_editable);
                mItemViews[i].setFocusableInTouchMode(_editable);
            } else {
                mItemViews[i].setFocusable(false);
                mItemViews[i].setFocusableInTouchMode(false);
            }
            mContainerViews[i].setVisibility(!_editable && mItems[i].length() == 0 ? View.GONE : View.VISIBLE);
            mClearViews[i].setVisibility(_editable && mItems[i].length() > 0 ? View.VISIBLE : View.GONE);
        }
    }

    private void loadList() {
        for (int i = 0; i < mContainerViews.length; i++) {
            mItems[i] = mItemViews[i].getText().toString();
        }
    }
}
