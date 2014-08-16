package uk.me.lewisdeane.materialnotes.objects;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

import uk.me.lewisdeane.materialnotes.R;
import uk.me.lewisdeane.materialnotes.activities.MainActivity;
import uk.me.lewisdeane.materialnotes.databases.Database;
import uk.me.lewisdeane.materialnotes.utils.DatabaseHelper;

/**
 * Created by Lewis on 05/08/2014.
 */
public class NoteItem {

    private Context mContext;
    private String mTitle, mItem;
    private long mTime;
    private boolean isFolder;

    public NoteItem(Context _context, String _title, String _item, boolean _isFolder) {
        mContext = _context;
        setTitle(_title);
        setItem(_item);
        setIsFolder(_isFolder);
        setTime();
    }

    public NoteItem(Context _context, String _title, String _item, boolean _isFolder, long _time) {
        mContext = _context;
        setTitle(_title);
        setItem(_item);
        setIsFolder(_isFolder);
        setTime(_time);
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

    private void setTime(){
        mTime = System.currentTimeMillis();
    }

    private void setTime(long _time){
        mTime = _time;
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

    public String getTimeFormatted(){
        return formatDifference(getDifference());
    }

    public long getTime(){
        return mTime;
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
        return System.currentTimeMillis() - mTime;
    }

    public void addToDatabase(){
        new DatabaseHelper(mContext).addNoteToDatabase(this);
    }

    public void deleteFromDatabase(){
        new DatabaseHelper(mContext).deleteNoteFromDatabase(this);
    }
}
