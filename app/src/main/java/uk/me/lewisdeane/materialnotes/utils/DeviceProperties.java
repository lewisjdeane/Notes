package uk.me.lewisdeane.materialnotes.utils;

import android.content.Context;
import android.util.TypedValue;
import android.view.WindowManager;

import uk.me.lewisdeane.materialnotes.activities.MainActivity;

/**
 * Created by Lewis on 13/08/2014.
 */
public abstract class DeviceProperties {

    private static int getScreenHeight(){
        return ((WindowManager) MainActivity.mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getHeight();
    }

    public static float getScreenHeightWithoutPadding(){
        return getScreenHeight() - getStatusHeight() - convertToPx(56);
    }

    public static float getHeight(){
        return getScreenHeight() - getStatusHeight() - convertToPx(56 + 80 + 56 + 16);
    }

    private static int getStatusHeight(){
        int resourceId = MainActivity.mContext.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0)
            return MainActivity.mContext.getResources().getDimensionPixelSize(resourceId);
        return 0;
    }

    public static float convertToPx(int _dp){
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, _dp, MainActivity.mContext.getResources().getDisplayMetrics());
    }
}
