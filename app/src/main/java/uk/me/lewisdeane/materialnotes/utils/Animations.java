package uk.me.lewisdeane.materialnotes.utils;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;

import uk.me.lewisdeane.materialnotes.R;
import uk.me.lewisdeane.materialnotes.activities.MainActivity;

/**
 * Created by Lewis on 15/08/2014.
 */
public abstract class Animations {

    private static float MOVE_TO_ADD = -(int)(DeviceProperties.getHeight());
    private static float MOVE_TO_ADD_HIDE = (int)DeviceProperties.convertToPx(100);
    private static float MOVE_TO_LIST = (int)(DeviceProperties.getScreenHeightWithoutPadding());
    private static float MOVE_TO_SCROLL = (MainActivity.mAddFragment.mScrollView.getHeight());

    private static final int ANIMATION_DURATION = 250;

    private static final String TRANSLATE_Y = "translationY", TRANSLATE_X = "translationX";

    public static ObjectAnimator setAddAnimation(boolean _up, View _view){
        return ObjectAnimator.ofFloat(_view, TRANSLATE_Y, !_up ? MOVE_TO_ADD : 0, !_up ? 0 : MOVE_TO_ADD).setDuration(ANIMATION_DURATION);
    }

    public static void setListAnimation(boolean _up, View _view){
        ObjectAnimator.ofFloat(_view, TRANSLATE_Y, !_up ? MOVE_TO_LIST : 0, !_up ? 0 : MOVE_TO_LIST).setDuration(ANIMATION_DURATION).start();
    }

    public static void animateFABIn(View _view){
        ObjectAnimator.ofFloat(_view, TRANSLATE_Y, MOVE_TO_ADD_HIDE, 0).setDuration(ANIMATION_DURATION).start();
    }

    public static void animateFABOut(View _view){
        ObjectAnimator.ofFloat(_view, TRANSLATE_Y, 0, MOVE_TO_ADD_HIDE).setDuration(ANIMATION_DURATION).start();
    }

    public static void animateUndoFABIn(View _view){
        ObjectAnimator.ofFloat(_view, TRANSLATE_Y, 0, -MOVE_TO_ADD_HIDE).setDuration(ANIMATION_DURATION).start();
    }

    public static void animateUndoFABOut(View _view){
        ObjectAnimator.ofFloat(_view, TRANSLATE_Y, -MOVE_TO_ADD_HIDE, 0).setDuration(ANIMATION_DURATION).start();
    }

    public static void animateScroll(View _view, boolean _shouldShow){
        ObjectAnimator.ofFloat(_view, TRANSLATE_Y, _shouldShow ? MOVE_TO_SCROLL : 0, _shouldShow ? 0 : MOVE_TO_SCROLL).setDuration(ANIMATION_DURATION).start();
    }

    public static void putScrollBack(View _view){
        ObjectAnimator.ofFloat(_view, TRANSLATE_Y, 0 ,0).setDuration(1).start();
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
        Integer start  = _first ? Color.parseColor("#FFFFFF") : MainActivity.mContext.getResources().getColor(R.color.pink_primary);
        Integer finish = _first ? MainActivity.mContext.getResources().getColor(R.color.pink_primary) : Color.parseColor("#FFFFFF");

        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), start, finish);
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                MainActivity.mFABFragment.mFAB.setImageDrawable(Misc.getColouredDrawable(MainActivity.mFABFragment.mFAB.getDrawable(), (Integer) animator.getAnimatedValue()));
            }
        });
        colorAnimation.setDuration(125).start();

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
