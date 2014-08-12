package uk.me.lewisdeane.materialnotes.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import uk.me.lewisdeane.materialnotes.R;

/**
 * Created by Lewis on 17/07/2014.
 */
public class CustomTextView extends TextView {

    String def = "Roboto-Light.ttf";

    public CustomTextView(Context _context, AttributeSet _attrs, int _defStyle) {
        super(_context, _attrs, _defStyle);
        init(_attrs);
    }

    public CustomTextView(Context _context, AttributeSet _attrs) {
        super(_context, _attrs);
        init(_attrs);

    }

    public CustomTextView(Context _context) {
        super(_context);
        init(null);
    }

    private void init(AttributeSet _attrs){
        if (_attrs!=null) {
            TypedArray a = getContext().obtainStyledAttributes(_attrs, R.styleable.CustomTextView);
            setFont(a.getString(R.styleable.CustomTextView_font) != null ? a.getString(R.styleable.CustomTextView_font) : def);
            a.recycle();
        }
    }
    public void setFont(String _font){
        setTypeface(Typeface.createFromAsset(getResources().getAssets(), _font));
    }
}
