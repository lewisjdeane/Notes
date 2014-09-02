package uk.me.lewisdeane.materialnotes.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;

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

        // Get the current NoteItem.
        NoteItem noteItem = mNoteItems.get(position);

        // Inflate layout.
        if (v == null) {
            // If it's a folder inflate folder view.
            if (noteItem.getIsFolder())
                v = LayoutInflater.from(getContext()).inflate(R.layout.item_folder, null);
                // Otherwise inflate note view.
            else
                v = LayoutInflater.from(getContext()).inflate(R.layout.item_note, null);
        }

        if (noteItem.getIsFolder()) {
            CustomTextView title = (CustomTextView) v.findViewById(R.id.item_folder_title);
            CustomTextView subItems = (CustomTextView) v.findViewById(R.id.item_folder_subitems);

            ImageView overflow = (ImageView) v.findViewById(R.id.item_folder_overflow);
            CustomTextView lastModified = (CustomTextView) v.findViewById(R.id.item_folder_last_modified);

            LinearLayout overflowContainer = (LinearLayout) v.findViewById(R.id.item_folder_overflow_info);

            title.setText(noteItem.getTitle());
            subItems.setText(new DatabaseHelper(mContext).getSubItems(noteItem));
            lastModified.setText(noteItem.getLastModifiedFormatted());

            title.setTextColor(Colours.getPrimaryColour());

            setOverflowListener(overflowContainer, noteItem, overflow);
        } else {
            // Initialise views.
            CustomTextView title = (CustomTextView) v.findViewById(R.id.item_note_title);
            CustomTextView item = (CustomTextView) v.findViewById(R.id.item_note_item);
            CustomTextView time = (CustomTextView) v.findViewById(R.id.item_note_time);
            CustomTextView date = (CustomTextView) v.findViewById(R.id.item_note_date);
            CustomTextView link = (CustomTextView) v.findViewById(R.id.item_note_link);
            CustomTextView lastModified = (CustomTextView) v.findViewById(R.id.item_note_last_modified);
            ImageView overflow = (ImageView) v.findViewById(R.id.item_note_overflow);

            LinearLayout itemContainer = (LinearLayout) v.findViewById(R.id.item_note_item_container);
            LinearLayout timeContainer = (LinearLayout) v.findViewById(R.id.item_note_time_container);
            LinearLayout dateContainer = (LinearLayout) v.findViewById(R.id.item_note_date_container);
            LinearLayout linkContainer = (LinearLayout) v.findViewById(R.id.item_note_link_container);
            LinearLayout overflowContainer = (LinearLayout) v.findViewById(R.id.item_note_overflow_info);

            // Apply data from NoteItems.
            title.setText(noteItem.getTitle());
            item.setText(noteItem.getIsFolder() ? new DatabaseHelper(mContext).getSubItems(noteItem) : noteItem.getItem());
            time.setText(noteItem.getTime());
            date.setText(noteItem.getDate());
            link.setText(noteItem.getLink());
            lastModified.setText(noteItem.getLastModifiedFormatted());

            itemContainer.setVisibility(item.getText().length() > 0 ? View.VISIBLE : View.GONE);
            timeContainer.setVisibility(time.getText().length() > 0 ? View.VISIBLE : View.GONE);
            dateContainer.setVisibility(date.getText().length() > 0 ? View.VISIBLE : View.GONE);
            linkContainer.setVisibility(link.getText().length() > 0 ? View.VISIBLE : View.GONE);

            // Apply properties to item.
            title.setTextColor(Colours.getPrimaryColour());

            setOverflowListener(overflowContainer, noteItem, overflow);
        }

        return v;
    }

    private void setOverflowListener(LinearLayout _overflowContainer, final NoteItem _noteItem, final View _anchor) {
        // Set listener so popup menu shows on overflow click.
        _overflowContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context themedContext = mContext;
                themedContext.setTheme(android.R.style.Theme_Holo_Light);

                PopupMenu popupMenu = new PopupMenu(themedContext, _anchor);
                MenuInflater inflater = popupMenu.getMenuInflater();
                inflater.inflate(R.menu.popup, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.menu_popup_open:
                                MainActivity.openNote(false, _noteItem);
                                break;
                            case R.id.menu_popup_edit:
                                MainActivity.openNote(true, _noteItem);
                                break;
                            case R.id.menu_popup_delete:
                                MainActivity.deleteNote(_noteItem);
                                break;
                        }
                        return false;
                    }
                });

                popupMenu.show();
            }
        });
    }

}
