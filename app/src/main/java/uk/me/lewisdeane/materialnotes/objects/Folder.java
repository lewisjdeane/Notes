package uk.me.lewisdeane.materialnotes.objects;

import android.content.ContentValues;
import android.content.Context;

import java.util.HashMap;
import java.util.Map;

import uk.me.lewisdeane.materialnotes.R;
import uk.me.lewisdeane.materialnotes.activities.MainActivity;
import uk.me.lewisdeane.materialnotes.utils.DatabaseHelper;

import static uk.me.lewisdeane.materialnotes.activities.MainActivity.PATH;

/**
 * Created by Lewis on 13/09/2014.
 */
public class Folder {

    private final String mPath;
    private final String mTitle;
    private final String mItem;
    private final long mLastModified;

    public enum NoteType {
        NOTE, FOLDER;
    }

    Folder(Builder _builder) {
        this.mPath = _builder.mPath;
        this.mTitle = _builder.mTitle;
        this.mItem = _builder.mItem;
        this.mLastModified = _builder.mLastModified;
    }

    public final long getLastModified() {
        return this.mLastModified;
    }

    public final String getPath() {
        return this.mPath;
    }

    public final String getTitle() {
        return this.mTitle;
    }

    public final String getItem() {
        return this.mItem;
    }

    public NoteType getNoteType() {
        return NoteType.FOLDER;
    }

    public String getLastModifiedFormatted() {
        Map<String, Long> timeParts = new HashMap<String, Long>(5);

        long difference = System.currentTimeMillis() - this.mLastModified;

        char[] letters = "smhdw".toCharArray();

        timeParts.put("s", difference / 1000 % 60);
        timeParts.put("m", difference / (60 * 1000) % 60);
        timeParts.put("h", difference / (60 * 60 * 1000) % 24);
        timeParts.put("d", difference / (24 * 60 * 60 * 1000));
        timeParts.put("w", difference / (7 * 24 * 60 * 60 * 1000));

        if (timeParts.get("w") > 0)
            return (timeParts.get("w") + 1) + (letters[4] + "");
        else if (timeParts.get("d") > 0 && timeParts.get("d") != 6)
            return (timeParts.get("d") + 1) + (letters[3] + "");
        else if (timeParts.get("d") == 6)
            return "1" + letters[4];
        else if (timeParts.get("h") > 0 && timeParts.get("h") != 23)
            return (timeParts.get("h") + 1) + (letters[2] + "");
        else if (timeParts.get("h") == 23)
            return "1" + letters[3];
        else if (timeParts.get("m") > 0 && timeParts.get("m") != 59)
            return (timeParts.get("m") + 1) + (letters[1] + "");
        else if (timeParts.get("m") == 59)
            return "1" + letters[2];
        else if (timeParts.get("s") > 0)
            return "1" + letters[1];
        else
            return MainActivity.mContext.getString(R.string.time_now);
    }

    public void addToDatabase() {
    }

    public void editToDatabase(Folder _folder) {
        new FolderDatabaseHelper(MainActivity.mContext).editFolderToDatabase(this, this);
    }

    public void deleteFromDatabase() {
        new FolderDatabaseHelper(MainActivity.mContext).deleteFolderFromDatabase(this);
    }

    public static class Builder {
        // Required params.
        private final String mTitle, mPath, mItem;

        // Optional params.
        private long mLastModified = System.currentTimeMillis();

        // Basic constructor for required params.
        public Builder(String _path, String _title, String _item) {
            this.mPath = _path;
            this.mTitle = _title;
            this.mItem = _item;
        }

        public final Builder lastModified(Long _lastModified) {
            this.mLastModified = _lastModified;
            return this;
        }

        public Folder build() {
            return new Folder(this);
        }
    }


    private class FolderDatabaseHelper extends DatabaseHelper {

        private final Context mContext;

        private FolderDatabaseHelper(Context _context) {
            super(_context);
            this.mContext = _context;
        }


        private final void editFolderToDatabase(Folder _oldFolder, Folder _newFolder) {

        }

        private final void deleteFolderFromDatabase(Folder _folder) {

        }

        /*
    Gets the content values from the passed in note.

    @param - boolean containing whether or not to put in archive, note to be added.
    @return - Content values of the note.
     */
        private ContentValues getContentVals(boolean _shouldArchive, Folder _folder) {
            // Creates a new ContentValues from the NoteItem.
            ContentValues contentValues = new ContentValues();

            // Get the path of the note to be added.
            String path = _folder.getPath() != null ? _folder.getPath() : PATH + _folder.getTitle();

            // Set appropriate fields.
            contentValues.put("PATH", path);
            contentValues.put("FOLDER", "true");
            contentValues.put("TITLE", _folder.getTitle());
            contentValues.put("ITEM", _folder.getItem());
            contentValues.put("TIME", "");
            contentValues.put("DATE", "");
            contentValues.put("LINK", "");
            contentValues.put("LAST_MODIFIED", _folder.getLastModified());
            contentValues.put("ARCHIVE", _shouldArchive + "");

            // Return the content values.
            return contentValues;
        }
    }
}
