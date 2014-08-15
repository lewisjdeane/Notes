package uk.me.lewisdeane.materialnotes.utils;

import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;

import uk.me.lewisdeane.materialnotes.activities.MainActivity;

/**
 * Created by Lewis on 15/08/2014.
 */
public abstract class Animations {

    private static DeviceProperties mDeviceProperties = new DeviceProperties(MainActivity.mContext);

    private static final float MOVE_TO_ADD = -(int)( mDeviceProperties.getHeight() - mDeviceProperties.convertToPx(82));
    private static final String TRANSLATE_Y = "translationY", TRANSLATE_X = "translationX";

    public static void setAddAnimation(boolean _up, Button _view){
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(_view, TRANSLATE_Y, _up ? MOVE_TO_ADD : 0, _up ? 0 : MOVE_TO_ADD);
        objectAnimator.setDuration(250);
        objectAnimator.start();
    }
}
