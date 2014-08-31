package uk.me.lewisdeane.materialnotes.utils;

import android.content.Context;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import uk.me.lewisdeane.materialnotes.activities.MainActivity;

/**
 * Created by Lewis on 23/08/2014.
 */
public abstract class Misc {

    public static void toast(String _text){
        Toast.makeText(MainActivity.mContext, _text, Toast.LENGTH_SHORT).show();
    }

    public static void hideKeyboard(){
        InputMethodManager inputManager = (InputMethodManager) MainActivity.mContext
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        // Check if view has focus and if so hide keyboard
        View view = MainActivity.mAddFragment.getActivity().getCurrentFocus();
        if (view == null)
            return;

        inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public static int getOccurences(String _text, String _lookingFor){
        int count = 0;

        for(int i = 0; i < _text.length(); i++){
            if((_text.charAt(i) + "").equals(_lookingFor))
                count++;
        }

        return count;
    }

    public static Drawable getColouredDrawable(Drawable _drawable, int _colour){
        int iColor = _colour;
        int red = (iColor & 0xFF0000) / 0xFFFF;
        int green = (iColor & 0xFF00) / 0xFF;
        int blue = iColor & 0xFF;

        float[] matrix = { 0, 0, 0, 0, red
                , 0, 0, 0, 0, green
                , 0, 0, 0, 0, blue
                , 0, 0, 0, 1, 0 };

        ColorFilter colorFilter = new ColorMatrixColorFilter(matrix);

        _drawable.setColorFilter(colorFilter);

        return _drawable;
    }
}
