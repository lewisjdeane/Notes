package uk.me.lewisdeane.materialnotes.objects;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by Lewis on 05/08/2014.
 */
public class NoteItem {

    private Context mContext;
    private String mTitle;
    private boolean isFolder;
    private ArrayList<String> mItems = new ArrayList<String>();

    public NoteItem(Context _context, String _title, ArrayList<String> _items, boolean _isFolder) {
        mContext = _context;
        setTitle(_title);
        setItems(_items);
        setIsFolder(_isFolder);
    }

    public void setTitle(String _title){
        mTitle = _title;
    }

    public void setIsFolder(boolean _isFolder){
        isFolder = _isFolder;
    }

    public void setItems(ArrayList<String> _items){
        mItems = _items;
    }

    public String getTitle(){
        return mTitle;
    }

    public boolean getIsFolder(){
        return isFolder;
    }

    public String getItem(){
        if(mItems != null)
            return mItems.get(0);

        return "";
    }
}
