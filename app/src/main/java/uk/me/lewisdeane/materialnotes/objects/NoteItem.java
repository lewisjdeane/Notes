package uk.me.lewisdeane.materialnotes.objects;

import android.content.Context;

import java.util.HashMap;

import uk.me.lewisdeane.materialnotes.R;
import uk.me.lewisdeane.materialnotes.utils.DatabaseHelper;

/**
 * Created by Lewis on 05/08/2014.
 */
public class NoteItem {

    private Context mContext;
    private String mTitle, mItem, mTime, mDate, mTags, mLink;
    private long mLastModified;
    private boolean isFolder;

    public NoteItem(Context _context, boolean _isFolder, String _title, String _item, String _time, String _date, String _tags, String _link) {
        mContext = _context;
        setTitle(_title);
        setItem(_item);
        setIsFolder(_isFolder);
        setLastModified();
        setTime(_time);
        setDate(_date);
        setTags(_tags);
        setLink(_link);
    }

    public NoteItem(Context _context, boolean _isFolder, String _title, String _item, String _time, String _date, String _tags, String _link, long _lastModified) {
        this(_context, _isFolder, _title, _item, _time, _date, _tags, _link);
        setLastModified(_lastModified);
    }

    public void setTitle(String _title){
        mTitle = _title;
    }

    public void setIsFolder(boolean _isFolder){
        isFolder = _isFolder;
    }

    public void setItem(String _item){
        mItem = _item;
    }

    private void setLastModified(){
        mLastModified = System.currentTimeMillis();
    }

    private void setLastModified(long _lastModified){
        mLastModified = _lastModified;
    }

    public void setTime(String _time){
        mTime = _time;
    }

    public void setDate(String _date){
        mDate = _date;
    }

    public void setTags(String _tags){
        mTags = _tags;
    }

    public void setLink(String _link){
        mLink = _link;
    }

    public String getTitle(){
        return mTitle;
    }

    public boolean getIsFolder(){
        return isFolder;
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

    public String getTags(){
        return mTags;
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
            return mContext.getString(R.string.time_now);
    }

    private long getDifference(){
        return System.currentTimeMillis() - mLastModified;
    }

    public void addToDatabase(){
        new DatabaseHelper(mContext).addNoteToDatabase(null, this);
    }

    public void editToDatabase(NoteItem _oldItem){ new DatabaseHelper(mContext).addNoteToDatabase(_oldItem, this); }

    public void deleteFromDatabase(){
        new DatabaseHelper(mContext).deleteNoteFromDatabase(this);
    }
}
