package uk.me.lewisdeane.materialnotes.utils;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.view.View;

import uk.me.lewisdeane.materialnotes.R;
import uk.me.lewisdeane.materialnotes.activities.MainActivity;

/**
 * Created by Lewis on 15/08/2014.
 */
public abstract class Animations {

    private static final float MOVE_TO_ADD = -(int)( DeviceProperties.getHeight());
    private static final float MOVE_TO_ADD_HIDE = (int)DeviceProperties.convertToPx(100);
    private static final float MOVE_TO_LIST = (int)(DeviceProperties.getScreenHeightWithoutPadding());

    private static final String TRANSLATE_Y = "translationY", TRANSLATE_X = "translationX";

    public static void setAddAnimation(boolean _up, View _view){
        ObjectAnimator.ofFloat(_view, TRANSLATE_Y, _up ? MOVE_TO_ADD : 0, _up ? 0 : MOVE_TO_ADD).setDuration(250).start();
    }

    public static void setOpenNote(View _view){
        final ObjectAnimator objectAnimator1 = ObjectAnimator.ofFloat(_view, TRANSLATE_Y, true ? MOVE_TO_ADD : 0, true ? 0 : MOVE_TO_ADD).setDuration(250);

        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(_view, "alpha", 1, 0).setDuration(125);
        final View fab = _view;
        objectAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
objectAnimator1.start();
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                fab.setBackground(MainActivity.mContext.getResources().getDrawable(R.drawable.ic_content));
                ObjectAnimator.ofFloat(fab, "alpha", 0, 1).setDuration(125).start();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        objectAnimator.start();
    }

    public static void setListAnimation(boolean _up, View _view){
        ObjectAnimator.ofFloat(_view, TRANSLATE_Y, _up ? MOVE_TO_LIST : 0, _up ? 0 : MOVE_TO_LIST).setDuration(250).start();
    }

    public static void animateAddOut(View _view){
        ObjectAnimator.ofFloat(_view, TRANSLATE_Y, MOVE_TO_ADD_HIDE, 0).setDuration(250).start();
    }

    public static void animateAddIn(View _view){
        ObjectAnimator.ofFloat(_view, TRANSLATE_Y, 0, MOVE_TO_ADD_HIDE).setDuration(250).start();
    }

    public static void animateAlpha(View _view, boolean _out, int _duration){
        ObjectAnimator.ofFloat(_view, "alpha", _out ? 1 : 0, _out ? 0 : 1).setDuration(_duration);
    }

}
