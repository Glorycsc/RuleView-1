package com.zkk.activity;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Scroller;

import com.zkk.rulerview.R;

/**
 * Create by glorizz on 2019/2/19
 * Describe: 划出所有的刻度线
 * 如何实现滑动的,根据手势不停的绘制出新的界面
 */
public class VerticalRulerView extends View {
    private static final String TAG = "TapeView";

    private int bgColor = Color.parseColor("#FBE40C");

    private int calibrationColor = Color.WHITE;

    private int textColor = Color.WHITE;

    private int triangleColor = Color.WHITE;

    private float textSize = 14.0f; //sp

    private float textY;

    //刻度线的宽度
    private float calibrationWidth = 1.0f; //dp

    //短的刻度线的高度
    private float calibrationShort = 20; //dp

    //长的刻度线的高度
    private float calibrationLong = 35; //dp

    private float triangleHeight = 18.0f; //dp

    //当前View的高度
    private int height;

    //当前VIew的宽度
    private int width;

    //宽度的中间值
    private int middle;

    //刻度尺最小值
    private float minValue = 0;

    //最大值
    private float maxValue = 100;

    //刻度尺当前值
    private float value = 0;

    //每一格代表的值
    private float per = 1;

    //两条长的刻度线之间的 per 数量
    private int perCount = 10;

    //当前刻度与最小值的距离 (minValue-value)/per*gapWidth
    private float offset;

    //当前刻度与最新值的最大距离 (minValue-maxValue)/per*gapWidth   所有的偏移量,最大到最小之间的距离
    private float maxOffset;

    //两个刻度之间的距离
    private float gapWidth = 10.0f; //dp

    //总的刻度数量
    private int totalCalibration;

    private float lastY;

    //被认为是快速滑动的最小速度
    private float minFlingVelocity;

    private Scroller scroller; // 通过Scroller可以实现果冻弹性效果,(不用也能实现滑动)

    private float dx;

    private Paint paint;

    //速度追踪器
    private VelocityTracker velocityTracker; //主要用跟踪触摸屏事件（flinging事件和其他gestures手势事件）的速率。

    private OnValueChangeListener onValueChangeListener;

    private Context mContext;

    /**
     * 回调接口
     */
    public interface OnValueChangeListener {
        void onChange(float value);
    }

    public void setOnValueChangeListener(OnValueChangeListener onValueChangeListener) {
        this.onValueChangeListener = onValueChangeListener;
    }


    public VerticalRulerView(Context context) {
        this(context, null);
    }

    public VerticalRulerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VerticalRulerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initAttrs(context, attrs);
        init(context);
        calculateAttr();
    }


    private void init(Context context) {
        minFlingVelocity = ViewConfiguration.get(getContext()).getScaledMinimumFlingVelocity();
        scroller = new Scroller(context);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(textSize);
    }

    private void calculateAttr() {
        verifyValues(maxValue, minValue, value);
        textY = calibrationLong + DisplayUtil.dp2px(20, mContext);
        offset = (value - minValue) * 10.0f / per * gapWidth;
        maxOffset = (maxValue - minValue) * 10.0f / per * gapWidth;
        totalCalibration = (int) ((maxValue - minValue) * 10.0f / per + 1);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.VerticalRulerView);
        bgColor = ta.getColor(R.styleable.VerticalRulerView_bgColor, bgColor);
        calibrationColor = ta.getColor(R.styleable.VerticalRulerView_calibrationColor, calibrationColor);
        calibrationWidth = ta.getDimension(R.styleable.VerticalRulerView_calibrationWidth, DisplayUtil.dp2px(calibrationWidth, context));
        calibrationLong = ta.getDimension(R.styleable.VerticalRulerView_calibrationLong, DisplayUtil.dp2px(calibrationLong, context));
        calibrationShort = ta.getDimension(R.styleable.VerticalRulerView_calibrationShort, DisplayUtil.dp2px(calibrationShort, context));
        triangleColor = ta.getColor(R.styleable.VerticalRulerView_triangleColor, triangleColor);
        triangleHeight = ta.getDimension(R.styleable.VerticalRulerView_triangleHeight, DisplayUtil.dp2px(triangleHeight, context));
        textColor = ta.getColor(R.styleable.VerticalRulerView_textColor, textColor);
        textSize = ta.getDimension(R.styleable.VerticalRulerView_textSize, DisplayUtil.sp2px(textSize, context));
        per = ta.getFloat(R.styleable.VerticalRulerView_per, per);
        per *= 10.0f;
        perCount = ta.getInt(R.styleable.VerticalRulerView_perCount, perCount);
        gapWidth = ta.getDimension(R.styleable.VerticalRulerView_gapWidth, DisplayUtil.dp2px(gapWidth, context));
        minValue = ta.getFloat(R.styleable.VerticalRulerView_minValue, minValue);
        maxValue = ta.getFloat(R.styleable.VerticalRulerView_maxValue, maxValue);
        value = ta.getFloat(R.styleable.VerticalRulerView_value, value);
        ta.recycle();
    }

    private boolean verifyValues(float maxValue, float minValue, float defaultValue) {
        if (maxValue > minValue && maxValue >= defaultValue && minValue <= defaultValue) {
            return true;

        }
        return false;

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        height = MeasureSpec.getSize(heightMeasureSpec);
        middle = height / 2;
        width = MeasureSpec.getSize(widthMeasureSpec);
        int mode = MeasureSpec.getMode(heightMeasureSpec);
        //当在布局文件设置高度为wrap_content时，默认为80dp(如果不处理效果和math_parent效果一样)，宽度就不处理了
        if (mode == MeasureSpec.AT_MOST) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec((int) DisplayUtil.dp2px(80, mContext), MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(bgColor);// 绘制背景颜色
        drawCalibration(canvas);//绘制刻度线
        drawTriangle(canvas);//绘制三角形游标

    }

    /**
     * 绘制刻度线
     *
     * @param canvas
     */
    private void drawCalibration(Canvas canvas) {
        //当前画的刻度的位置
        float currentCalibration;
        float height;//要绘制的刻度线的高度
        String value;//显示的值
        //首先计算,左侧显示的第一根刻度,是多少 (超出屏幕的不用绘制)
        int distance = (int) (middle - offset);//超出屏幕外的长度
        int left = 0;//超出屏幕外的刻度数
        if (distance < 0) { //说明左侧一定超了屏幕外边了
            left = (int) (-distance / gapWidth);
        }
        //当前刻度显示在屏幕上的哪个位置
        currentCalibration = middle - offset + left * gapWidth;

        while (currentCalibration < this.height * 10 && left < totalCalibration) {//todo 为什么要*10
            if (currentCalibration == 0) {
                left++;
                currentCalibration = middle - offset + left * gapWidth;
                continue;
            }
            if (left % perCount == 0) {
                paint.setStrokeWidth(calibrationWidth * 2);
                height = calibrationLong;
                //获取文字的高度
                Rect rect = new Rect();//文字所在区域的矩形
                value = String.valueOf(minValue + left / perCount);
                paint.getTextBounds(value, 0, value.length(), rect);
                int textHeight = rect.height();

                if (value.endsWith(".0")) {
                    value = value.substring(0, value.length() - 2);
                }
                paint.setColor(textColor);
                canvas.drawText(value, textY, currentCalibration + textHeight / 2, paint);

            } else {
                paint.setStrokeWidth(calibrationWidth);
                height = calibrationShort;
            }

            paint.setColor(calibrationColor);
            canvas.drawLine(0, currentCalibration, height, currentCalibration, paint);

            left++; //每画一个,左侧需要被绘制的就少了一个 左后left最大值是所有的刻度的个数
            currentCalibration = middle - offset + left * gapWidth;
        }
    }

    private void drawTriangle(Canvas canvas) {
//        Path path = new Path();
//        path.moveTo(width, middle - triangleHeight / 2);
//        path.lineTo(width - triangleHeight / 2, middle);
//        path.lineTo(width, middle + triangleHeight / 2);
//        path.close(); // 记得要关闭
//        canvas.drawPath(path, paint);
        //再绘制一条游标的横线
        paint.setStrokeWidth(calibrationWidth * 1.5f);
        canvas.drawLine(0, middle, width, middle, paint);
    }

    //加入手势的操作

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (velocityTracker == null) {
            velocityTracker = VelocityTracker.obtain();
        }
        velocityTracker.addMovement(event);//追踪该事件

        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                scroller.forceFinished(true);
                lastY = y;
                dx = 0; //dx置为0
                break;
            case MotionEvent.ACTION_MOVE:
                dx = lastY - y;// 移动的距离
                validateValue();//刷新value的值
                break;
            case MotionEvent.ACTION_UP:
                smoothMoveToCalibration();
                calculateVelocity();
                return false;
            default:
                return false;
        }
        lastY = y;
        return true;//返回true 表示我已经消费了,系统不用再干预
    }


    //更新value
    private void validateValue() {
        offset += dx;
        if (offset < 0) {
            offset = 0;
            dx = 0;
            scroller.forceFinished(true);
        } else if (offset > maxOffset) {
            offset = maxOffset;
            dx = 0;
            scroller.forceFinished(true);
        }
        value = minValue + Math.round(Math.abs(offset) / gapWidth) * per / 10.0f;
        if (onValueChangeListener != null) {
            onValueChangeListener.onChange(value);
        }
        postInvalidate();
    }

    private void smoothMoveToCalibration() {
        offset += dx;
        if (offset < 0) {
            offset = 0;
        } else if (offset > maxOffset) {
            offset = maxOffset;
        }
        lastY = 0;
        dx = 0;
        value = minValue + Math.round(Math.abs(offset) / gapWidth) * per / 10.0f;
        offset = (value - minValue) * 10.0f / per * gapWidth;
        if (onValueChangeListener != null) {
            onValueChangeListener.onChange(value);
        }
        postInvalidate();
    }

    //计算加速度
    private void calculateVelocity() {
        velocityTracker.computeCurrentVelocity(1000);
        float yVelocity = velocityTracker.getYVelocity();//计算水平方向的速度(单位秒)

        //大于这个值才会被认为是fling
        if (Math.abs(yVelocity) > minFlingVelocity) {
            scroller.fling(0, 0, 0, (int) yVelocity, 0, 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
            invalidate();// 调用刷新会执行onDraw()方法
        }
    }

    /**
     * 重写view的该方法是为了和Scroller配合完成滑动
     */
    @Override
    public void computeScroll() {
        super.computeScroll();
        //返回true表示滑动还没有结束
        if (scroller.computeScrollOffset()) {
            if (scroller.getCurrY() == scroller.getFinalY()) {
                smoothMoveToCalibration(); //相当于ACTION_UP
            } else {
                int y = scroller.getCurrY();//相当于ACTION_MOVE 一直在移动
                dx = lastY - y;
                validateValue();
                lastY = y;
            }

        }
    }
}
