package uk.me.lewisdeane.materialnotes.utils;

import android.content.Context;
import android.util.TypedValue;
import android.view.WindowManager;

/**
 * Created by Lewis on 13/08/2014.
 */
public class DeviceProperties {

    private Context mContext;

    public DeviceProperties(Context _context){
        mContext = _context;
    }

    private int getScreenHeight(){
        return ((WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getHeight();
    }

    public float getScreenHeightWithoutPadding(){
        return getScreenHeight() - getStatusHeight() - convertToPx(56);
    }

    public float getHeight(){
        return getScreenHeight() - getStatusHeight() - convertToPx(56 + 80 + 28 + 16);
    }

    private int getStatusHeight(){
        int resourceId = mContext.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0)
            return mContext.getResources().getDimensionPixelSize(resourceId);
        return 0;
    }

    public float convertToPx(int _dp){
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, _dp, mContext.getResources().getDisplayMetrics());
    }
}
