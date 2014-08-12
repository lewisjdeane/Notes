package uk.me.lewisdeane.materialnotes.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by Lewis on 17/07/2014.
 */
public class CustomEditText extends EditText{

    String def = "Roboto-Light.ttf";
    Context context;

    public CustomEditText(Context _context, AttributeSet _attrs, int _defStyle) {
        super(_context, _attrs, _defStyle);
        init(_attrs, _context);
    }

    public CustomEditText(Context _context, AttributeSet _attrs) {
        super(_context, _attrs);
        init(_attrs, _context);

    }

    public CustomEditText(Context _context) {
        super(_context);
        init(null, _context);
    }

    private void init(AttributeSet _attrs, Context _context) {
        setFont(def);
        context = _context;

        this.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    public void setFont(String _font) {
        setTypeface(Typeface.createFromAsset(getResources().getAssets(), _font));
    }
}
