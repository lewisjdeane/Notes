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
import uk.me.lewisdeane.materialnotes.customviews.CustomTextView;
import uk.me.lewisdeane.materialnotes.objects.NoteItem;
import uk.me.lewisdeane.materialnotes.utils.Colours;
import uk.me.lewisdeane.materialnotes.utils.DatabaseHelper;

import static uk.me.lewisdeane.materialnotes.activities.MainActivity.NoteMode;
import static uk.me.lewisdeane.materialnotes.activities.MainActivity.deleteNote;
import static uk.me.lewisdeane.materialnotes.activities.MainActivity.mNoteMode;
import static uk.me.lewisdeane.materialnotes.activities.MainActivity.openNote;
import static uk.me.lewisdeane.materialnotes.activities.MainActivity.restoreNote;

public class NoteAdapter extends ArrayAdapter<NoteItem> {

    private ArrayList<NoteItem> mNotes = new ArrayList<NoteItem>();
    private Context mContext;

    public NoteAdapter(Context context, int resource,
                       ArrayList<NoteItem> _notes) {

        super(context, resource, _notes);
        this.mContext = context;
        this.mNotes = _notes;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = convertView;

        // Get the current NoteItem.
        NoteItem note = mNotes.get(position);

        // Inflate layout.
        if (v == null) {
            v = LayoutInflater.from(getContext()).inflate(R.layout.item_note, null);
        }

        LinearLayout folderContainer = (LinearLayout) v.findViewById(R.id.item_folder_container);
        LinearLayout noteContainer = (LinearLayout) v.findViewById(R.id.item_note_container);

        folderContainer.setVisibility(note.getIsFolder() ? View.VISIBLE : View.GONE);
        noteContainer.setVisibility(note.getIsFolder() ? View.GONE : View.VISIBLE);


        if (note.getIsFolder()) {
            CustomTextView title = (CustomTextView) v.findViewById(R.id.item_folder_title);
            CustomTextView subItems = (CustomTextView) v.findViewById(R.id.item_folder_subitems);

            ImageView overflow = (ImageView) v.findViewById(R.id.item_folder_overflow);
            CustomTextView lastModified = (CustomTextView) v.findViewById(R.id.item_folder_last_modified);

            LinearLayout overflowContainer = (LinearLayout) v.findViewById(R.id.item_folder_overflow_info);

            title.setText(note.getTitle());
            subItems.setText(new DatabaseHelper(mContext).getSubItems(note));
            lastModified.setText(note.getLastModifiedFormatted());

            title.setTextColor(Colours.getPrimaryColour());

            setOverflowListener(overflowContainer, note, overflow);
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
            title.setText(note.getTitle());
            item.setText(note.getItem());
            time.setText(note.getTime());
            date.setText(note.getDate());
            link.setText(note.getLink());
            lastModified.setText(note.getLastModifiedFormatted());

            itemContainer.setVisibility(item.getText().length() > 0 ? View.VISIBLE : View.GONE);
            timeContainer.setVisibility(time.getText().length() > 0 ? View.VISIBLE : View.GONE);
            dateContainer.setVisibility(date.getText().length() > 0 ? View.VISIBLE : View.GONE);
            linkContainer.setVisibility(link.getText().length() > 0 ? View.VISIBLE : View.GONE);

            // Apply properties to item.
            title.setTextColor(Colours.getPrimaryColour());
            link.setLinkTextColor(Colours.getPrimaryColour());

            setOverflowListener(overflowContainer, note, overflow);
        }

        return v;
    }

    private void setOverflowListener(LinearLayout _overflowContainer, final NoteItem _note, final View _anchor) {
        // Set listener so popup menu shows on overflow click.
        _overflowContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context themedContext = mContext;
                themedContext.setTheme(android.R.style.Theme_Holo_Light);

                PopupMenu popupMenu = new PopupMenu(themedContext, _anchor);
                MenuInflater inflater = popupMenu.getMenuInflater();

                if (mNoteMode != NoteMode.ARCHIVE) {
                    inflater.inflate(R.menu.popup, popupMenu.getMenu());

                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            switch (menuItem.getItemId()) {
                                case R.id.menu_popup_open:
                                    openNote(false, _note);
                                    break;
                                case R.id.menu_popup_edit:
                                    openNote(true, _note);
                                    break;
                                case R.id.menu_popup_delete:
                                    deleteNote(_note);
                                    break;
                            }
                            return false;
                        }
                    });
                } else {
                    inflater.inflate(R.menu.archive_popup, popupMenu.getMenu());

                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            switch (menuItem.getItemId()) {
                                case R.id.menu_archive_popup_restore:
                                    restoreNote(_note);
                                    break;
                                case R.id.menu_archive_popup_delete:
                                    deleteNote(_note);
                                    break;
                            }
                            return false;
                        }
                    });
                }

                popupMenu.show();
            }
        });
    }

}
