package uk.me.lewisdeane.materialnotes.objects;

import java.util.HashMap;

import uk.me.lewisdeane.materialnotes.R;
import uk.me.lewisdeane.materialnotes.activities.MainActivity;
import uk.me.lewisdeane.materialnotes.utils.DatabaseHelper;

import static uk.me.lewisdeane.materialnotes.activities.MainActivity.mContext;

/**
 * Created by Lewis on 05/08/2014.
 */
public class NoteItem {

    private String mTitle = "", mItem = "", mTime = "", mDate = "", mLink = "", mPath = "";
    private long mLastModified;
    private boolean mIsFolder;

    public NoteItem(Builder _builder){
        setPath(_builder.mPath);
        setIsFolder(_builder.mIsFolder);
        setTitle(_builder.mTitle);
        setItem(_builder.mItem);
        setTime(_builder.mTime);
        setDate(_builder.mDate);
        setLink(_builder.mLink);
        if(_builder.mLastModified == 0)
            setLastModified();
        else
            setLastModified(_builder.mLastModified);
    }

    public NoteItem setTitle(String _title){
        this.mTitle = _title;
        return this;
    }

    public NoteItem setPath(String _path){
        this.mPath = _path;
        return this;
    }

    public NoteItem setIsFolder(boolean _mIsFolder){
        this.mIsFolder = _mIsFolder;
        return this;
    }

    public NoteItem setItem(String _item){
        this.mItem = _item;
        return this;
    }

    private NoteItem setLastModified(){
        this.mLastModified = System.currentTimeMillis();
        return this;
    }

    private NoteItem setLastModified(long _lastModified){
        this.mLastModified = _lastModified;
        return this;
    }

    public NoteItem setTime(String _time){
        this.mTime = _time;
        return this;
    }

    public NoteItem setDate(String _date){
        this.mDate = _date;
        return this;
    }

    public NoteItem setLink(String _link){
        this.mLink = _link;
        return this;
    }

    public String getTitle(){
        return mTitle;
    }

    public String getPath(){ return mPath; }

    public boolean getIsFolder(){
        return mIsFolder;
    }

    public String getItem(){
        return mItem;
    }

    public String getTime(){
        return mTime;
    }

    public String getDate(){
        return mDate;
    }

    public String getLink(){
        return mLink;
    }

    public String getLastModifiedFormatted(){
        return formatDifference(getDifference());
    }

    public long getLastModified(){
        return mLastModified;
    }

    private String formatDifference(long _difference){
        HashMap<String, Long> timeParts = new HashMap<String, Long>(5);

        char[] letters = "smhdw".toCharArray();

        timeParts.put("s", _difference / 1000 % 60);
        timeParts.put("m", _difference / (60 * 1000) % 60);
        timeParts.put("h", _difference / (60 * 60 * 1000) % 24);
        timeParts.put("d", _difference / (24 * 60 * 60 * 1000));
        timeParts.put("w", _difference / (7 * 24 * 60 * 60 * 1000));

        if(timeParts.get("w") > 0)
            return (timeParts.get("w") + 1) + (letters[4] + "");
        else if(timeParts.get("d") > 0 && timeParts.get("d") != 6)
            return (timeParts.get("d") + 1) + (letters[3] + "");
        else if(timeParts.get("d") == 6)
            return "1" + letters[4];
        else if(timeParts.get("h") > 0 && timeParts.get("h") != 23)
            return (timeParts.get("h") + 1) + (letters[2] + "");
        else if(timeParts.get("h") == 23)
            return "1" + letters[3];
        else if (timeParts.get("m") > 0 && timeParts.get("m") != 59)
            return (timeParts.get("m") + 1) + (letters[1] + "");
        else if(timeParts.get("m") == 59)
            return "1" + letters[2];
        else if(timeParts.get("s") > 0)
            return "1" + letters[1];
        else
            return MainActivity.mContext.getString(R.string.time_now);
    }

    private long getDifference(){
        return System.currentTimeMillis() - mLastModified;
    }

    public void addToDatabase(){
        new DatabaseHelper(mContext).addNoteToDatabase(null, this);
    }

    public void editToDatabase(NoteItem _oldItem){ new DatabaseHelper(mContext).addNoteToDatabase(_oldItem, this); }

    public void deleteFromDatabase(){
        if(!this.getIsFolder()){
            new DatabaseHelper(mContext).deleteNoteFromDatabase(this);
        } else {
            new DatabaseHelper(mContext).deleteFolderFromDatabase(this);
        }
    }

    public static class Builder{

        // Required params.
        private final String mTitle, mPath;
        private final boolean mIsFolder;

        // Optional params.
        private String mItem = "", mTime = "", mDate = "", mLink = "";
        private long mLastModified = 0;

        // Basic constructor for required params.
        public Builder(String _path, boolean _isFolder, String _title){
            this.mPath = _path;
            this.mIsFolder = _isFolder;
            this.mTitle = _title;
        }

        public Builder item(String _item){
            this.mItem = _item;
            return this;
        }

        public Builder time(String _time){
            this.mTime = _time;
            return this;
        }

        public Builder date(String _date){
            this.mDate = _date;
            return this;
        }

        public Builder link(String _link){
            this.mLink = _link;
            return this;
        }

        public Builder lastModified(Long _lastModified){
            this.mLastModified = _lastModified;
            return this;
        }

        public NoteItem build(){
            return new NoteItem(this);
        }
    }
}
