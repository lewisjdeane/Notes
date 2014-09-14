package uk.me.lewisdeane.materialnotes.objects;

import java.util.HashMap;

import uk.me.lewisdeane.materialnotes.R;
import uk.me.lewisdeane.materialnotes.activities.MainActivity;
import uk.me.lewisdeane.materialnotes.utils.DatabaseHelper;

import static uk.me.lewisdeane.materialnotes.activities.MainActivity.mContext;

/**
 * Created by Lewis on 05/08/2014.
 */
public final class NoteItem {

    public enum NoteType {
        NOTE, FOLDER;
    }

    private final String mTitle, mItem, mTime, mDate, mLink, mPath;
    private final long mLastModified;
    private final NoteType mNoteType;

    private NoteItem(Builder _builder) {
        this.mPath = _builder.mPath;
        this.mTitle = _builder.mTitle;
        this.mItem = _builder.mItem;
        this.mTime = _builder.mTime;
        this.mDate = _builder.mDate;
        this.mLink = _builder.mLink;
        this.mNoteType = _builder.mNoteType;
        this.mLastModified = _builder.mLastModified;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getPath() {
        return mPath;
    }

    public NoteType getNoteType() {
        return mNoteType;
    }

    public String getItem() {
        return mItem;
    }

    public String getTime() {
        return mTime;
    }

    public String getDate() {
        return mDate;
    }

    public String getLink() {
        return mLink;
    }

    public String getLastModifiedFormatted() {
        return formatDifference(getDifference());
    }

    public long getLastModified() {
        return mLastModified;
    }

    private String formatDifference(long _difference) {
        HashMap<String, Long> timeParts = new HashMap<String, Long>(5);

        char[] letters = "smhdw".toCharArray();

        timeParts.put("s", _difference / 1000 % 60);
        timeParts.put("m", _difference / (60 * 1000) % 60);
        timeParts.put("h", _difference / (60 * 60 * 1000) % 24);
        timeParts.put("d", _difference / (24 * 60 * 60 * 1000));
        timeParts.put("w", _difference / (7 * 24 * 60 * 60 * 1000));

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

    private long getDifference() {
        return System.currentTimeMillis() - mLastModified;
    }

    public void addToDatabase() {
        new DatabaseHelper(mContext).addNoteToDatabase(false, this);
    }

    public void editToDatabase(NoteItem _oldItem) {
        if (this.mNoteType == NoteType.NOTE) {
            new DatabaseHelper(mContext).editNoteToDatabase(_oldItem, this);
        } else {
            new DatabaseHelper(mContext).editFolderToDatabase(_oldItem, this);
        }
    }

    public void deleteFromDatabase() {
        if (this.mNoteType == NoteType.NOTE) {
            new DatabaseHelper(mContext).deleteNoteFromDatabase(this);
        } else {
            new DatabaseHelper(mContext).deleteFolderFromDatabase(this);
        }
    }

    public final static class Builder {

        // Required params.
        private final String mTitle, mPath;
        private final NoteType mNoteType;

        // Optional params.
        private String mItem = "", mTime = "", mDate = "", mLink = "";
        private long mLastModified = System.currentTimeMillis();

        // Basic constructor for required params.
        public Builder(String _path, NoteType _noteType, String _title) {
            this.mPath = _path;
            this.mNoteType = _noteType;
            this.mTitle = _title;
        }

        public final Builder item(String _item) {
            this.mItem = _item;
            return this;
        }

        public final Builder time(String _time) {
            this.mTime = _time;
            return this;
        }

        public final Builder date(String _date) {
            this.mDate = _date;
            return this;
        }

        public final Builder link(String _link) {
            this.mLink = _link;
            return this;
        }

        public final Builder lastModified(Long _lastModified) {
            this.mLastModified = _lastModified;
            return this;
        }

        public final NoteItem build() {
            return new NoteItem(this);
        }
    }
}