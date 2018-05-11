package com.example.gaope.qqredcrile;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by gaope on 2018/5/10.
 */

public class RedCrileView extends View {

    private static final String TAG = "RedCrile";

    private Paint paint;

    /**
     * 小圆半径
     */
    private float smallRadius;

    /**
     * 大圆半径
     */
    private float bigRadius;

    private PointF center;

    /**
     * 小圆中心点
     */
    private PointF smallCrileCenter;

    /**
     * 大圆中心点
     */
    private PointF bigCrileCenter;

    /**
     *贝塞尔曲线的控制点
     */
    private PointF control;

    /**
     *小圆上面的两个数据点，第一个点为圆上面的数据点，第二个为圆下面的数据点
     */
    private float[] smallData;

    /**
     *大圆上面的两个数据点，第一个点为圆上面的数据点，第二个为圆下面的数据点
     */
    private float[] bigData;

    /**
     *两个圆心之间的dx
     */
    private float dx = 0;

    /**
     *两个圆心之间的dy
     */
    private float dy = 0;


    public RedCrileView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setColor(Color.parseColor("#fffb5d5c"));
        paint.setStyle(Paint.Style.FILL);

        smallRadius = 20;
        bigRadius = 40;

        smallCrileCenter = new PointF(500,500);
        bigCrileCenter = new PointF(500,500);

        control = new PointF(0,0);

        smallData = new float[4];
        bigData = new float[4];
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        bigCrileCenter.x = event.getX();
        bigCrileCenter.y = event.getY();
        dx =  (bigCrileCenter.x - smallCrileCenter.x);
        dy =  (bigCrileCenter.y - smallCrileCenter.y);
        control.x = (smallCrileCenter.x + bigCrileCenter.x)/2;
        control.y = (smallCrileCenter.y + bigCrileCenter.y)/2;
        Log.d(TAG,"dx:"+dx);
        invalidate();
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(smallCrileCenter.x,smallCrileCenter.y,smallRadius,paint);
        canvas.drawCircle(bigCrileCenter.x,bigCrileCenter.y,bigRadius,paint);
        obtainData();

        Path path = new Path();
        path.reset();
        path.moveTo(smallData[0],smallData[1]);
        path.quadTo(control.x,control.y,bigData[0],bigData[1]);
        path.lineTo(bigData[2],bigData[3]);
        path.quadTo(control.x,control.y,smallData[2],smallData[3]);
        path.lineTo(smallData[0],smallData[1]);
        canvas.drawPath(path,paint);



    }

    private void obtainData() {

        float aa = (float) Math.atan( dy/dx );

        /**
         * 大圆在小圆的右下左上，大圆在小圆的右上左下
         */
        if ( (dx > 0 && dy > 0) || (dx < 0 && dy < 0) || (dx > 0 && dy < 0) || (dx < 0 && dy > 0)){

            /**
             * 大圆和小圆底部的控制点
             */
            smallData[0] = (float) (smallCrileCenter.x - smallRadius * Math.sin(aa));
            smallData[1] = (float) (smallCrileCenter.y + smallRadius * Math.cos(aa));
            bigData[0] = (float) (bigCrileCenter.x - bigRadius * Math.sin(aa));
            bigData[1] = (float) (bigCrileCenter.y + bigRadius * Math.cos(aa));

            /**
             * 大圆和小圆上部的控制点
             */
            smallData[2] = (float) (smallCrileCenter.x + smallRadius * Math.sin(aa));
            smallData[3] = (float) (smallCrileCenter.y - smallRadius * Math.cos(aa));
            bigData[2] = (float) (bigCrileCenter.x + bigRadius * Math.sin(aa));
            bigData[3] = (float) (bigCrileCenter.y - bigRadius * Math.cos(aa));

            Log.d(TAG,"am[0]:"+smallData[0]);
        }

//        /**
//         * 大圆在小圆的右上左下
//         */
//        if ( (dx > 0 && dy < 0) || (dx < 0 && dy > 0) ){
//
//        }

        /**
         * 大圆在小圆的上面和小面，dx为0，dy不为0
         */
        if ( dx == 0 && dy !=0 ){

            smallData[0] = smallCrileCenter.x - smallRadius;
            smallData[1] = smallCrileCenter.y;
            smallData[2] = smallCrileCenter.x + smallRadius;
            smallData[3] = smallCrileCenter.y;

            bigData[0] = bigCrileCenter.x - bigRadius;
            bigData[1] = bigCrileCenter.y;
            bigData[2] = bigCrileCenter.x + bigRadius;
            bigData[3] = bigCrileCenter.y;

        }

        /**
         * 大圆在小圆的左边和右边，dy为0，dx不为0
         */
        if ( dx != 0 && dy == 0 ){
            smallData[0] = smallCrileCenter.x;
            smallData[1] = smallCrileCenter.y - smallRadius;
            smallData[2] = smallCrileCenter.x;
            smallData[3] = smallCrileCenter.y + smallRadius;

            bigData[0] = bigCrileCenter.x;
            bigData[1] = bigCrileCenter.y - bigRadius;
            bigData[2] = bigCrileCenter.x;
            bigData[3] = bigCrileCenter.y + bigRadius;
        }
    }


}
