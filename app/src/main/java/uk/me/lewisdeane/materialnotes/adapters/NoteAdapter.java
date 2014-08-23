package uk.me.lewisdeane.materialnotes.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;

import java.util.ArrayList;

import uk.me.lewisdeane.materialnotes.R;
import uk.me.lewisdeane.materialnotes.activities.MainActivity;
import uk.me.lewisdeane.materialnotes.customviews.CustomTextView;
import uk.me.lewisdeane.materialnotes.objects.NoteItem;
import uk.me.lewisdeane.materialnotes.utils.Colours;
import uk.me.lewisdeane.materialnotes.utils.DatabaseHelper;

public class NoteAdapter extends ArrayAdapter<NoteItem> {

    private ArrayList<NoteItem> mNoteItems = new ArrayList<NoteItem>();
    private Context mContext;

    public NoteAdapter(Context context, int resource,
                       ArrayList<NoteItem> _noteItems) {

        super(context, resource, _noteItems);
        this.mContext = context;
        this.mNoteItems = _noteItems;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            v = LayoutInflater.from(getContext()).inflate(R.layout.item_note, null);
        }

        /*
        If it's a folder do not show the item text, instead replace it with a list of subitems exc. subfolders in a smaller font.
         */

        CustomTextView mText = (CustomTextView) v.findViewById(R.id.item_note_title);
        mText.setText(mNoteItems.get(position).getTitle());

        CustomTextView mTime = (CustomTextView) v.findViewById(R.id.item_note_time);
        mTime.setText(mNoteItems.get(position).getLastModifiedFormatted());

        CustomTextView mItem = (CustomTextView) v.findViewById(R.id.item_note_item);

        mItem.setVisibility(View.VISIBLE);
        if(!mNoteItems.get(position).getIsFolder())
            mItem.setText(mNoteItems.get(position).getItem());
        else {
            mItem.setText(new DatabaseHelper(mContext).getSubItems(mNoteItems.get(position)));
            mItem.setTextSize(14);
        }

        mText.setTextColor(Colours.getSelectedColour());

        ImageView mImg = (ImageView) v.findViewById(R.id.item_note_folder);

        mImg.setVisibility(View.INVISIBLE);

        if(mNoteItems.get(position).getIsFolder()){
            mImg.setVisibility(View.VISIBLE);
        }

        final ImageButton overflow = (ImageButton) v.findViewById(R.id.item_note_overflow);

        final LinearLayout overflowLayout = (LinearLayout) v.findViewById(R.id.item_note_overflow_info);

        overflowLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context themedContext = mContext;
                themedContext.setTheme(android.R.style.Theme_Holo_Light);
                PopupMenu popupMenu = new PopupMenu(themedContext, overflow);
                MenuInflater inflater = popupMenu.getMenuInflater();
                inflater.inflate(R.menu.popup, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()){
                            case R.id.menu_popup_open:
                                MainActivity.openNote(mNoteItems.get(position));
                                break;
                            case R.id.menu_popup_edit:
                                MainActivity.editNote(mNoteItems.get(position));
                                break;
                            case R.id.menu_popup_delete:
                                MainActivity.deleteNote(mNoteItems.get(position));
                        }
                        return false;
                    }
                });

                popupMenu.show();
            }
        });

        return v;
    }

}
