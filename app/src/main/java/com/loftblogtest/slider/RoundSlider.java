package com.loftblogtest.slider;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.loftblogtest.R;
import com.loftblogtest.utils.ViewUtil;

import static com.loftblogtest.utils.Const.END_ANGLE;
import static com.loftblogtest.utils.Const.START_ANGLE;

public class RoundSlider extends View {

    protected float customRadius;
    protected int roundRadius;
    protected float minRadius = getResources().getDimension(R.dimen.slider_round_min_radius);

    protected float maxValue;
    protected float minValue;
    protected float currentValue;

    protected boolean isTouch;

    protected Point customCenter = new Point();
    protected Point center = new Point();
    protected RectF frameOval = new RectF();
    protected Paint paint;

    public RoundSlider(Context context) {
        super(context);
        initPaint();
    }

    public RoundSlider(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(attrs);
        initPaint();
    }

    public RoundSlider(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(attrs);
        initPaint();
    }

    public RoundSlider(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initAttrs(attrs);
        initPaint();
    }

    private void initAttrs(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.RoundSlider);
        maxValue = typedArray.getInt(R.styleable.RoundSlider_max_value, 100);
        minValue = typedArray.getInt(R.styleable.RoundSlider_min_value, 0);
        currentValue = typedArray.getInt(R.styleable.RoundSlider_current_value, 50);
        customRadius = typedArray.getInt(R.styleable.RoundSlider_touch_radius,
                (int) getResources().getDimension(R.dimen.slider_custom_radius));
        typedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int desiredWidth = getResources().getDimensionPixelSize(R.dimen.slider_default_size);
        int desiredHeight = getResources().getDimensionPixelSize(R.dimen.slider_default_size);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            width = Math.min(desiredWidth, widthSize);
        } else {
            width = desiredWidth;
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            height = Math.min(desiredHeight, heightSize);
        } else {
            height = desiredHeight;
        }

        setMeasuredDimension(width, height);
    }

    private void initPaint() {
        paint = new Paint();
        paint.setColor(Color.parseColor("#75FFFFFF"));
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(6);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        initSizes(canvas);
        drawCircleFrame(canvas);
        drawTouchSlider(canvas);
    }

    private void initSizes(Canvas canvas) {
        center.x = canvas.getWidth() / 2;
        center.y = canvas.getHeight() / 2;
        roundRadius = (int) (canvas.getWidth() / 2 - customRadius);
        frameOval.set(
                center.x - roundRadius,
                center.y - roundRadius,
                center.x + roundRadius,
                center.y + roundRadius);
    }

    private void drawCircleFrame(Canvas canvas) {
        paint.setColor(Color.parseColor("#FFFFFFFF"));
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawArc(frameOval, START_ANGLE, END_ANGLE - START_ANGLE, false, paint);

        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(
                (float) Math.cos(Math.toRadians(END_ANGLE)) * roundRadius + center.x,
                (float) Math.sin(Math.toRadians(END_ANGLE)) * roundRadius + center.y,
                minRadius, paint);

        canvas.drawCircle(
                (float) Math.cos(Math.toRadians(START_ANGLE)) * roundRadius + center.x,
                (float) Math.sin(Math.toRadians(START_ANGLE)) * roundRadius + center.y,
                minRadius, paint);
    }

    private void drawTouchSlider(Canvas canvas) {

        int angle = (int) ((currentValue - minValue) * (END_ANGLE - START_ANGLE) /
                (maxValue - minValue) + START_ANGLE);

        customCenter.x = (int)
                (Math.cos(Math.toRadians(angle)) * roundRadius + center.x);
        customCenter.y = (int)
                (Math.sin(Math.toRadians(angle)) * roundRadius + center.y);
        paint.setColor(Color.parseColor("#BFFFFFFF"));
        canvas.drawCircle(customCenter.x, customCenter.y, customRadius, paint);
        paint.setColor(Color.parseColor("#FFFFFFFF"));

        paint.setTextSize(getResources().getDimensionPixelSize(R.dimen.slider_value_size));

        canvas.drawText(String.valueOf(Math.round(currentValue)),
                center.x,
                center.y + customRadius * 3 / 2,
                paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int touchX = Math.round(event.getX());
        int touchY = Math.round(event.getY());
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                return false;
            case MotionEvent.ACTION_DOWN:
                isTouch = ViewUtil.isNearTouch(touchX, touchY, 20, customCenter,
                        (int) customRadius);
            case MotionEvent.ACTION_MOVE:
                if (isTouch) {
                    int val;
                    int angle = ViewUtil.pointToAngle(touchX, touchY, center) - START_ANGLE;
                    if (angle < 0) {
                        angle = 360 + angle;
                    }
                    val = (int) (minValue + angle * (maxValue - minValue) / (END_ANGLE - START_ANGLE));

                    if ((val != currentValue) && (val <= maxValue) && (val >= minValue) &&
                            Math.abs(currentValue - val) < (maxValue - minValue) / 10 + 1) {

                        setCurrentValue(val);
                    }
                }
                break;
        }
        return true;
    }

    public float getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(float maxValue) {
        this.maxValue = maxValue;
    }

    public float getMinValue() {
        return minValue;
    }

    public void setMinValue(float minValue) {
        this.minValue = minValue;
    }

    public float getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(float currentValue) {
        if (currentValue < minValue) {
            currentValue = minValue;
        }

        if (currentValue > maxValue) {
            currentValue = maxValue;
        }

        this.currentValue = currentValue;
        invalidate();
    }
}
