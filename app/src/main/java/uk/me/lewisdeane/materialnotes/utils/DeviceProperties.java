package uk.me.lewisdeane.materialnotes.utils;

import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;

/**
 * Created by Lewis on 13/08/2014.
 */
public class DeviceProperties {

    private Context mContext;

    public DeviceProperties(Context _context){
        mContext = _context;
    }

    public int getScreenHeight(){
        return ((WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getHeight();
    }
}
