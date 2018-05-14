package com.example.gaope.qqredcrile;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.AnimationDrawable;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Scroller;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by gaope on 2018/5/10.
 */

public class RedCrileView extends LinearLayout {

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

    /**
     * 显示的Text文本
     */
    private TextView textView;

    /**
     *拉动的最大距离
     */
    private float maxLength;

    /**
     * 在minLength范围内释放就会回到起点
     */
    private float minLength;

    /**
     *拉动的距离
     */
    private float length;

    /**
     * 判断是否是拉动状态且小圆的半径不为0
     */
    private boolean touch;

    /**
     * 判断是否超过最大拉动范围且又回到可以回弹的范围内
     */
    private boolean mtext;

    /**
     * mImage判断是否到最后
     */
    private boolean mImage;

    /**
     *Scroller滑动
     */
    private Scroller scroller;

    private ImageView imageView;

    /**
     * 照片的集合
     */
    private int[] photo = new int[]{
            R.drawable.photo1,R.drawable.photo2,R.drawable.photo3,

};

    public RedCrileView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setColor(Color.parseColor("#fffb5d5c"));
        paint.setStyle(Paint.Style.FILL);

        scroller = new Scroller(getContext());

        touch = true;
        mtext = true;
        mImage = true;

        smallRadius = 15;
        bigRadius = 15;
        maxLength = 200;
        minLength = 150;

        smallCrileCenter = new PointF(500,500);
        bigCrileCenter = new PointF(500,500);

        control = new PointF(0,0);

        smallData = new float[4];
        bigData = new float[4];

        textView = new TextView(getContext());
        LayoutParams lp = new LayoutParams(100,80);
        textView.setLayoutParams(lp);
        textView.setPadding(5,5,5,5);
        textView.setBackgroundResource(R.drawable.red_text);
        textView.setText("99+");
        textView.setTextColor(Color.WHITE);
        addView(textView);

        imageView = new ImageView(getContext());
        LayoutParams layoutParams = new LayoutParams(80,80);
        imageView.setLayoutParams(layoutParams);
        imageView.setVisibility(View.INVISIBLE);
        imageView.setImageResource(R.drawable.photo1);
        addView(imageView);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        textView.layout((int) (bigCrileCenter.x - textView.getHeight()/2),
                (int) (bigCrileCenter.y - textView.getHeight()/2),
                (int) (bigCrileCenter.x + textView.getHeight()/2),
                (int) (bigCrileCenter.y + textView.getHeight()/2));
    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {

        bigCrileCenter.x = event.getX();
        bigCrileCenter.y = event.getY();
        dx =  (bigCrileCenter.x - smallCrileCenter.x);
        dy =  (bigCrileCenter.y - smallCrileCenter.y);
        length = (float) Math.sqrt(Math.pow(dx,2) + Math.pow(dy,2));

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if(bigCrileCenter.x > textView.getLeft() && bigCrileCenter.x < textView.getRight()
                        && bigCrileCenter.y > textView.getTop() && bigCrileCenter.y < textView.getBottom()) {
                    invalidate();
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (!mImage){
                    imageView.setVisibility(View.GONE);
                }
                //length小于minLength，回到起点，有回弹效果
                if (touch ){
                    mtext = true;
                    kickBackCenter();
                    return true;
                }
                if (mtext && !touch && mImage){
                    //在范围内就回到起点
                    Log.d(TAG,"touch:"+touch);
                    if(length <= minLength){
                        Log.d(TAG,"aaaaaaaaa");
                        textView.setX(smallCrileCenter.x - textView.getWidth()/2);
                        textView.setY(smallCrileCenter.y - textView.getHeight()/2);
                        touch = true;
                        return true;
                    }else {
                        //不在范围内就进行图片爆炸动画
                        textView.setVisibility(View.GONE);
                        imageView.setVisibility(View.VISIBLE);
                        imageView.setX(bigCrileCenter.x);
                        imageView.setY(bigCrileCenter.y);
                        photoBlast();
                        mImage = false;
                  //      imageView.setVisibility(View.GONE);
                    }


                }
                return true;
        }

        Log.d(TAG,"dx:"+dx);
        invalidate();
        return true;
    }

    private void photoBlast() {
        Log.d(TAG,"bbbbb");
        ValueAnimator value = ValueAnimator.ofInt(0,photo.length-1);
        value.setInterpolator(new LinearInterpolator());
        value.setDuration(500);
        value.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Log.d(TAG,"acab:"+(int)animation.getAnimatedValue());
                imageView.setImageResource(photo[(int)animation.getAnimatedValue()]);
                invalidate();
            }
        });
        value.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                imageView.setVisibility(View.GONE);
            }
        });
        value.start();
    }

    private void kickBackCenter() {
        ValueAnimator valu = ValueAnimator.ofObject(new KickBackEvaluator(bigCrileCenter),bigCrileCenter,smallCrileCenter);
        valu.setDuration(1000);
        valu.setInterpolator(new BounceInterpolator());
        valu.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                bigCrileCenter = (PointF) animation.getAnimatedValue();
                invalidate();
            }
        });
        valu.start();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {

        canvas.saveLayer(new RectF(0,0,getWidth(),getHeight()),paint,Canvas.ALL_SAVE_FLAG);
        judgeRadius();

        if (touch){
            canvas.drawCircle(smallCrileCenter.x,smallCrileCenter.y,smallRadius,paint);
            textView.setX(bigCrileCenter.x  - textView.getWidth()/2);
            textView.setY(bigCrileCenter.y - textView.getHeight()/2);
            obtainData();
            Path path = new Path();
            path.reset();
            path.moveTo(smallData[0],smallData[1]);
            path.quadTo(control.x,control.y,bigData[0],bigData[1]);
            path.lineTo(bigData[2],bigData[3]);
            path.quadTo(control.x,control.y,smallData[2],smallData[3]);
            path.lineTo(smallData[0],smallData[1]);
            canvas.drawPath(path,paint);
        }else {
            textView.setX(bigCrileCenter.x  - textView.getWidth()/2);
            textView.setY(bigCrileCenter.y - textView.getHeight()/2);
        }


        canvas.restore();

        //绘制自身然后绘制子控件，子控件覆盖在父控件上面
        super.dispatchDraw(canvas);
    }

    private void obtainData() {

        float aa = (float) Math.atan( dy/dx );
        control.x = (smallCrileCenter.x + bigCrileCenter.x)/2;
        control.y = (smallCrileCenter.y + bigCrileCenter.y)/2;

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
    void judgeRadius(){

        float a = length / maxLength;
        Log.d(TAG,"aaa:"+a);
        Log.d(TAG,"length:"+length);
        if (a >= 0 && a < 1/4.0){
            smallRadius = 15;
            
        }else if (a >= 1/4.0 && a< 1/2.0){
            smallRadius = 13;
        }else if (a >= 1/2.0 && a< 3/4.0){
            smallRadius = 8;
        }else if (a >= 3/4.0 && a<= 1){
            smallRadius = 0;
            touch = false;
        }
    }


}
