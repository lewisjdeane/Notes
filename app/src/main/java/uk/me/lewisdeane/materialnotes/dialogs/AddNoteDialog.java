package uk.me.lewisdeane.materialnotes.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

import uk.me.lewisdeane.materialnotes.R;
import uk.me.lewisdeane.materialnotes.adapters.DialogAdapter;
import uk.me.lewisdeane.materialnotes.fragments.MainFragment;
import uk.me.lewisdeane.materialnotes.objects.NoteItem;

/**
 * Created by Lewis on 06/08/2014.
 */
public class AddNoteDialog extends AlertDialog {

    private EditText mText;
    private CheckBox mCheck;
    private Button mAdd;
    private Context mContext;
    private ListView mList;
    public static DialogAdapter mDialogAdapter;
    public ArrayList<String> mItems = new ArrayList<String>();
    private View mRootView;

    public AddNoteDialog(Context _context){
        super(_context);
        mContext = _context;
        init();
    }

    public AddNoteDialog(Context _context, NoteItem _noteItem){
        super(_context);
        mContext = _context;
        init();
    }

    private void init(){
        mRootView = LayoutInflater.from(mContext).inflate(
                R.layout.dialog_add_note,
                null);

        mItems.add("");
        mDialogAdapter = new DialogAdapter(getContext(), R.layout.item_dialog, mItems);

        mText = (EditText) mRootView.findViewById(R.id.dialog_add_title);
        mList = (ListView) mRootView.findViewById(R.id.dialog_add_list);
        mCheck = (CheckBox) mRootView.findViewById(R.id.dialog_add_folder_checkbox);
        mAdd = (Button) mRootView.findViewById(R.id.dialog_add_confirm);

        mList.setAdapter(mDialogAdapter);

        setListeners();

        super.setView(mRootView);
        super.show();
    }

    private void setListeners(){
        mAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainFragment.mNoteItems.add(new NoteItem(mContext, mText.getText().toString().trim(), mCheck.isChecked()));
                MainFragment.mNoteAdapter.notifyDataSetChanged();
                dismiss();
            }
        });

        mCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mList.setVisibility(View.VISIBLE);
                if(b)
                    mList.setVisibility(View.GONE);
            }
        });
    }

}
