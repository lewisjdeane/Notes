package uk.me.lewisdeane.materialnotes.utils;

import android.graphics.ColorFilter;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;

import uk.me.lewisdeane.materialnotes.R;
import uk.me.lewisdeane.materialnotes.activities.MainActivity;

/**
 * Created by Lewis on 23/08/2014.
 */
public abstract class Colours {

    public static Drawable getColouredDrawable(Drawable _drawable, int _color){
        int iColor = _color;

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

    public static int getPrimaryColour(){
        return MainActivity.mContext.getResources().getColor(R.color.blue_primary);
    }

    public static int getDarkColour(){
        return MainActivity.mContext.getResources().getColor(R.color.dark_grey);
    }
}
