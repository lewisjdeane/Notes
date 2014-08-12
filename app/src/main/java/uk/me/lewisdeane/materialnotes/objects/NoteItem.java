package uk.me.lewisdeane.materialnotes.objects;

import android.content.Context;

/**
 * Created by Lewis on 05/08/2014.
 */
public class NoteItem {

    private Context mContext;
    private String mTitle;
    private boolean isFolder;

    public NoteItem(Context _context, String _title, boolean _isFolder) {
        mContext = _context;
        setTitle(_title);
        setIsFolder(_isFolder);
    }

    public void setTitle(String _title){
        mTitle = _title;
    }

    public void setIsFolder(boolean _isFolder){
        isFolder = _isFolder;
    }

    public String getTitle(){
        return mTitle;
    }

    public boolean getIsFolder(){
        return isFolder;
    }
}
