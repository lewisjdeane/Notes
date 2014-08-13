package uk.me.lewisdeane.materialnotes.objects;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by Lewis on 05/08/2014.
 */
public class NoteItem {

    private Context mContext;
    private String mTitle, mItem;
    private boolean isFolder;

    public NoteItem(Context _context, String _title, String _item, boolean _isFolder) {
        mContext = _context;
        setTitle(_title);
        setItems(_item);
        setIsFolder(_isFolder);
    }

    public void setTitle(String _title){
        mTitle = _title;
    }

    public void setIsFolder(boolean _isFolder){
        isFolder = _isFolder;
    }

    public void setItems(String _item){
        mItem = _item;
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
}
