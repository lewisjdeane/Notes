package uk.me.lewisdeane.materialnotes.objects;

/**
 * Created by Lewis on 15/07/2014.
 */
public class DrawerItem {

    private String title;
    private int res;
    private boolean isSelected, isSmall;

    public DrawerItem(int _res, String _title, boolean _isSelected, boolean _isSmall){
        setTitle(_title);
        setRes(_res);
        setIsSelected(_isSelected);
        setIsSmall(_isSmall);
    }

    public void setTitle(String _title){ title=_title; }

    public void setRes(int _res){ res=_res; }

    public void setIsSelected(boolean _isSelected){ isSelected = _isSelected; }

    public void setIsSmall(boolean _isSmall){
        isSmall = _isSmall;
    }

    public String getTitle(){
        return title;
    }

    public int getRes(){
        return res;
    }

    public boolean getIsSelected(){
        return isSelected;
    }

    public boolean getIsSmall(){
        return isSmall;
    }
}
