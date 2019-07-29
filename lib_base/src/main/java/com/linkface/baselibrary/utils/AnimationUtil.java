package com.linkface.baselibrary.utils;

import android.view.animation.Interpolator;
import android.view.animation.RotateAnimation;

/**
 * Created by yuemq on 2018/9/25.
 */

public class AnimationUtil {
    public static RotateAnimation getRotateAnimation(float fromDegrees, float toDegrees, int pivotXType, float pivotXValue,
                                                     int pivotYType, float pivotYValue, long durationMillis, int repeatMode, int repeatCount, Interpolator interpolator) {

        RotateAnimation rotateAnimation = new RotateAnimation(fromDegrees, toDegrees, pivotXType, pivotXValue,
                pivotYType, pivotYValue);
        rotateAnimation.setDuration(durationMillis);
        rotateAnimation.setRepeatMode(repeatMode);
        rotateAnimation.setRepeatCount(repeatCount);
        if (interpolator != null)
            rotateAnimation.setInterpolator(interpolator);
        return rotateAnimation;
    }
}
