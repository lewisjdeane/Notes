package uk.me.lewisdeane.materialnotes.utils;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import uk.me.lewisdeane.materialnotes.activities.MainActivity;

/**
 * Created by Lewis on 15/08/2014.
 */
public abstract class Animations {

    private static float MOVE_TO_ADD = -(int)(DeviceProperties.getHeight());
    private static float MOVE_TO_ADD_HIDE = (int)DeviceProperties.convertToPx(100);
    private static float MOVE_TO_LIST = (int)(DeviceProperties.getScreenHeightWithoutPadding());
    private static float MOVE_TO_SCROLL = (MainActivity.mAddFragment.mScrollView.getHeight());
    private static float MOVE_ABOVE_SNACKBAR = -(int)DeviceProperties.convertToPx(56);

    private static final int ANIMATION_DURATION = 400;
    private static final int FAB_HIDE_ANIMATION_DURATION = 200;

    private static final String TRANSLATE_Y = "translationY", TRANSLATE_X = "translationX";

    public static ObjectAnimator setAddAnimation(boolean _up, View _view){
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(_view, TRANSLATE_Y, !_up ? MOVE_TO_ADD : 0, !_up ? 0 : MOVE_TO_ADD).setDuration(ANIMATION_DURATION);
        objectAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        return objectAnimator;
    }

    public static void setListAnimation(boolean _up, View _view){
        ObjectAnimator.ofFloat(_view, TRANSLATE_Y, !_up ? MOVE_TO_LIST : 0, !_up ? 0 : MOVE_TO_LIST).setDuration(ANIMATION_DURATION).start();
    }

    public static void animateFABIn(View _view){
        ObjectAnimator.ofFloat(_view, TRANSLATE_Y, MOVE_TO_ADD_HIDE, 0).setDuration(FAB_HIDE_ANIMATION_DURATION).start();
    }

    public static void animateFABOut(View _view){
        ObjectAnimator.ofFloat(_view, TRANSLATE_Y, 0, MOVE_TO_ADD_HIDE).setDuration(FAB_HIDE_ANIMATION_DURATION).start();
    }

    public static void animateScroll(View _view, boolean _shouldShow){
        ObjectAnimator.ofFloat(_view, TRANSLATE_Y, _shouldShow ? MOVE_TO_SCROLL : 0, _shouldShow ? 0 : MOVE_TO_SCROLL).setDuration(ANIMATION_DURATION).start();
    }

    public static void putScrollBack(View _view){
        ObjectAnimator.ofFloat(_view, TRANSLATE_Y, 0 ,0).setDuration(1).start();
    }

    public static void animateFABAboveSnackbar(boolean _up){
        ObjectAnimator.ofFloat(MainActivity.mFABFragment.mRootView, TRANSLATE_Y, _up ? 0 : MOVE_ABOVE_SNACKBAR, _up ? MOVE_ABOVE_SNACKBAR : 0).setDuration(ANIMATION_DURATION).start();
    }

    public static void animateFAB(boolean _shouldMove, boolean _up, final Drawable _newDrawable){
        if(_shouldMove){
            // Move it.
            ObjectAnimator objectAnimator = setAddAnimation(_up, MainActivity.mFABFragment.mRootView);
            objectAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                    animateBackground(true, _newDrawable);
                }

                @Override
                public void onAnimationEnd(Animator animator) {}

                @Override
                public void onAnimationCancel(Animator animator) {}
                @Override
                public void onAnimationRepeat(Animator animator) {}
            });
            objectAnimator.start();
        } else{
            animateBackground(true, _newDrawable);
        }
    }

    private static void animateBackground(final boolean _first, final Drawable _newDrawable){

        ValueAnimator colorAnimation = ValueAnimator.ofInt(_first ? 255 : 0, _first ? 0 : 255);
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                Drawable drawable = MainActivity.mFABFragment.mFAB.getDrawable();
                drawable.setAlpha((Integer)animator.getAnimatedValue());
                MainActivity.mFABFragment.mFAB.setImageDrawable(drawable);
            }
        });
        colorAnimation.setDuration(200).start();

        colorAnimation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {}

            @Override
            public void onAnimationEnd(Animator animator) {
                if(_first) {
                    MainActivity.mFABFragment.mFAB.setImageDrawable(_newDrawable);
                    animateBackground(false, null);
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {}
            @Override
            public void onAnimationRepeat(Animator animator) {}
        });
    }

}
