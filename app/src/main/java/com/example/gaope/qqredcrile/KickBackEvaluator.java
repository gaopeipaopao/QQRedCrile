package com.example.gaope.qqredcrile;

import android.animation.TypeEvaluator;
import android.graphics.PointF;

/**
 * Created by gaope on 2018/5/13.
 */

public class KickBackEvaluator implements TypeEvaluator {

    PointF pointF;

    KickBackEvaluator(PointF pointF){
        this.pointF = pointF;
    }

    @Override
    public Object evaluate(float fraction, Object startValue, Object endValue) {
        PointF start = (PointF)startValue;
        PointF end = (PointF)endValue;
        pointF.x = start.x + fraction * (end.x - pointF.x);
        pointF.y = start.x + fraction * (end.y - pointF.y);
        return pointF;
    }
}
