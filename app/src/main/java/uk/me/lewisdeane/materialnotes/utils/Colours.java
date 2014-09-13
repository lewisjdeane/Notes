package uk.me.lewisdeane.materialnotes.utils;

import android.graphics.ColorFilter;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;

import uk.me.lewisdeane.materialnotes.R;

import static uk.me.lewisdeane.materialnotes.activities.MainActivity.mContext;

/**
 * Created by Lewis on 23/08/2014.
 */
public class Colours {

    public static enum ColourTheme {
        BLUE(getColour(R.color.blue_primary), getColour(R.color.blue_primary)),
        PURPLE(getColour(R.color.blue_primary), getColour(R.color.blue_primary)),
        RED(getColour(R.color.blue_primary), getColour(R.color.blue_primary)),
        GREEN(getColour(R.color.blue_primary), getColour(R.color.blue_primary));

        private final int mPrimaryColour, mSecondaryColour;

        private ColourTheme(int _primaryColour, int _secondaryColour){
            this.mPrimaryColour = _primaryColour;
            this.mSecondaryColour = _secondaryColour;
        }

        @Override
        public String toString(){
            return this.name().substring(0, 1) + this.name().substring(1).toLowerCase();
        }
    }

    public static ColourTheme mColourTheme = ColourTheme.BLUE;


    /*
    Suppress the constructor to prevent instantiation.
     */
    private Colours(){

    }

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
        return mColourTheme.mPrimaryColour;
    }

    public static int getSecondaryColour(){
        return mColourTheme.mSecondaryColour;
    }

    private static int getColour(int _res){
        return mContext.getResources().getColor(_res);
    }
}
