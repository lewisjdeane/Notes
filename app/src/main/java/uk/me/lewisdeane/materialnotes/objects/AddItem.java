package uk.me.lewisdeane.materialnotes.objects;

import android.graphics.drawable.Drawable;

/**
 * Created by Lewis on 19/08/2014.
 */
public class AddItem {

    private String mHint = "", mText = "";
    private int mPos;
    private Drawable mImg;

    public AddItem(int _pos, String _hint, Drawable _img){
        setPosition(_pos);
        setHint(_hint);
        setImg(_img);
    }

    public AddItem(int _pos, String _hint, Drawable _img, String _text){
        this(_pos, _hint, _img);
        setText(_text);
    }

    public AddItem setHint(String _hint){
        mHint = _hint;
        return this;
    }

    public AddItem setImg(Drawable _img){
        mImg = _img;
        return this;
    }

    public AddItem setPosition(int _pos){
        mPos = _pos;
        return this;
    }

    public AddItem setText(String _text){
        mText = _text;
        return this;
    }

    public String getHint(){
        return mHint;
    }

    public Drawable getImg(){
        return mImg;
    }

    public int getPos(){
        return mPos;
    }

    public String getText(){
        return mText;
    }

}
