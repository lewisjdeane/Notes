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
    private CustomTextView mTitle, mItem, mLastModified;
    private ImageView mFolder;
    private LinearLayout mOverflowLayout;

    public NoteAdapter(Context context, int resource,
                       ArrayList<NoteItem> _noteItems) {

        super(context, resource, _noteItems);
        this.mContext = context;
        this.mNoteItems = _noteItems;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = convertView;

        // Inflate layout.
        if (v == null)
            v = LayoutInflater.from(getContext()).inflate(R.layout.item_note, null);

        // Get current NoteItem
        NoteItem noteItem = mNoteItems.get(position);

        // Initialise views.
        mTitle = (CustomTextView) v.findViewById(R.id.item_note_title);
        mItem = (CustomTextView) v.findViewById(R.id.item_note_item);
        mLastModified = (CustomTextView) v.findViewById(R.id.item_note_last_modified);

        mFolder = (ImageView) v.findViewById(R.id.item_note_folder);
        final ImageView mOverflow = (ImageView) v.findViewById(R.id.item_note_overflow);

        mOverflowLayout = (LinearLayout) v.findViewById(R.id.item_note_overflow_info);

        // Apply data from NoteItems.
        mTitle.setText(mNoteItems.get(position).getTitle());
        mItem.setText(noteItem.getIsFolder() ? new DatabaseHelper(mContext).getSubItems(noteItem) : noteItem.getItem());
        mLastModified.setText(noteItem.getLastModifiedFormatted());

        // Apply properties to item.
        mTitle.setTextColor(Colours.getPrimaryColour());
        mFolder.setVisibility(noteItem.getIsFolder() ? View.VISIBLE : View.INVISIBLE);

        // Set listener so popup menu shows on overflow click.
        mOverflowLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context themedContext = mContext;
                themedContext.setTheme(android.R.style.Theme_Holo_Light);

                PopupMenu popupMenu = new PopupMenu(themedContext, mOverflow);
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
                                break;
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
