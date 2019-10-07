package com.feng.textclock;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import java.util.Calendar;

/**
 * @author Feng Zhaohao
 * Created on 2019/9/21
 */
public class TextClock extends View {

    private Paint mPaint;
    // View 取出 padding 后的宽高
    private float mWidth;
    private float mHeight;
    // 小时、分钟、秒的半径
    private float mHourR;
    private float mMinuteR;
    private float mSecondR;
    // 各整体旋转角度
    private float mHourDeg;
    private float mMinuteDeg;
    private float mSecondDeg;
    // 动画
    private ValueAnimator mAnimator;

    public TextClock(Context context) {
        super(context);
        init();
    }

    public TextClock(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TextClock(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = initPaint(null, Color.WHITE);

        mAnimator = ValueAnimator.ofFloat(6f, 0f);
        // 由 6 到 0 的原因是负数表示逆时针旋转，加的数越小，代表逆时针旋转的角度越大
        // + 6 时说明还在上一时刻，+ 0 时表示已经到了下一时刻
        mAnimator.setDuration(500);
        mAnimator.setInterpolator(new LinearInterpolator());

        doInvalidate();
    }

    /**
     * 初始化 Paint
     */
    private Paint initPaint(String colorString, int color) {
        Paint paint = new Paint();
        if (colorString != null) {
            color = Color.parseColor(colorString);
        }
        paint.setColor(color);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);

        return paint;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        // 得到 View 去除 padding 后的宽高
        mWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        mHeight = getMeasuredHeight() - getPaddingLeft() - getPaddingRight();

        // 用 mWidth 的比例来表示
        mHourR = mWidth * 0.143f;
        mMinuteR = mWidth * 0.35f;
        mSecondR = mWidth * 0.35f;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (canvas == null) {
            return;
        }

        canvas.drawColor(Color.BLACK);  // 将画布的背景填充为黑色
        canvas.save();
        canvas.translate(mWidth/2, mHeight/2);  // 画布的原点移动到中心

        // 绘制时钟各组件
        drawCenterInfo(canvas);
        drawHour(canvas, mHourDeg);
        drawMinute(canvas, mMinuteDeg);
        drawSecond(canvas, mSecondDeg);

        canvas.restore();
    }

    /**
     * 获取绘制文字时在 x 轴上，贴紧 x 轴的上边缘的 y 坐标
     */
    private float getBottomedY() {
        return -mPaint.getFontMetrics().bottom;
    }

    /**
     * 获取绘制文字时在 x 轴上，贴紧 x 轴的下边缘的 y 坐标
     */
    private float getToppedY() {
        return -mPaint.getFontMetrics().ascent;
    }

    /**
     * 获取绘制文字时，在 x 轴上垂直居中的 y 坐标
     */
    private float getCenteredY() {
        return mPaint.getFontSpacing() / 2 - mPaint.getFontMetrics().bottom;
    }

    /**
     * 绘制圆中信息
     */
    private void drawCenterInfo(Canvas canvas) {
        Calendar calendar = Calendar.getInstance();
        // 得到当前时间
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        mPaint.setTextSize(mHourR * 0.4f);  // 字体大小根据时圈的半径来计算
        mPaint.setAlpha(255);
        mPaint.setTextAlign(Paint.Align.CENTER);
        // 绘制文字时间
        String time = minute < 10? hour + ":0" + minute : hour + ":" + minute;
        canvas.drawText(time, 0f, getBottomedY(), mPaint);

        // 得到月份、日期、星期
        int month = calendar.get(Calendar.MONTH) + 1;
        String monthStr = month < 10? "0"+month : String.valueOf(month);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;

        mPaint.setTextSize(mHourR * 0.16f);  // 字体大小根据时圈的半径来计算
        mPaint.setAlpha(255);
        mPaint.setTextAlign(Paint.Align.CENTER);
        // 绘制月份日期，星期
        String text = monthStr + "." + day + " 星期" + TextClockUtil.numToText(dayOfWeek);
        canvas.drawText(text, 0f, getToppedY(), mPaint);
    }

    /**
     * 绘制小时
     */
    private void drawHour(Canvas canvas, float degrees) {
        mPaint.setTextSize(mHourR * 0.16f);

        // 处理整体旋转
        canvas.save();
        canvas.rotate(degrees);

        for (int i = 0; i < 12; i++) {
            canvas.save();

            // 从 x 轴开始旋转，每 30°绘制一个时刻，12 次画完时圈
            float iDegrees = 360 / 12f * i;
            canvas.rotate(iDegrees);

            // 当前时间的透明度要和其他时间的透明度区别开
            // 由于 degrees 控制整体逆时针旋转，iDegrees 绘制时是顺时针旋转
            // 所以当两者和为 0 时，刚好在 x 正半轴上，表示当前时间
            if (degrees + iDegrees == 0f) {
                mPaint.setAlpha(255);
            } else {
                mPaint.setAlpha((int) (255 * 0.6f));
            }

            mPaint.setTextAlign(Paint.Align.LEFT);

            canvas.drawText(TextClockUtil.numToText(i+1), mHourR, getCenteredY(), mPaint);
            canvas.restore();
        }

        canvas.restore();
    }

    /**
     * 绘制分钟
     */
    private void drawMinute(Canvas canvas, float degrees) {
        mPaint.setTextSize(mHourR * 0.16f);

        // 处理整体旋转
        canvas.save();
        canvas.rotate(degrees);

        for (int i = 0; i < 60; i++) {
            canvas.save();

            // 从 x 轴开始旋转，每 6°绘制一个时刻，60 次画完分圈
            float iDegrees = 360 / 60f * i;
            canvas.rotate(iDegrees);

            if (degrees + iDegrees == 0f) {
                mPaint.setAlpha(255);
            } else {
                mPaint.setAlpha((int) (255 * 0.6f));
            }
            mPaint.setTextAlign(Paint.Align.RIGHT); // 分钟紧贴秒，位于秒的左边

            if (i < 59) {
                canvas.drawText(TextClockUtil.numToText(i+1) + "分", mMinuteR, getCenteredY(), mPaint);
            }
            canvas.restore();
        }

        canvas.restore();
    }

    /**
     * 绘制秒
     */
    private void drawSecond(Canvas canvas, float degrees) {
        mPaint.setTextSize(mHourR * 0.16f);

        // 处理整体旋转
        canvas.save();
        canvas.rotate(degrees);

        for (int i = 0; i < 60; i++) {
            canvas.save();

            float iDegrees = 360 / 60f * i;
            canvas.rotate(iDegrees);

            if (degrees + iDegrees == 0f) {
                mPaint.setAlpha(255);
            } else {
                mPaint.setAlpha((int) (255 * 0.6f));
            }
            mPaint.setTextAlign(Paint.Align.LEFT); // 秒紧贴分钟，位于分钟的右边

            if (i < 59) {
                canvas.drawText(TextClockUtil.numToText(i+1) + "秒", mSecondR, getCenteredY(), mPaint);
            }
            canvas.restore();
        }

        canvas.restore();
    }

    /**
     * 让时钟动起来
     */
    void doInvalidate() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR);
        final int minute = calendar.get(Calendar.MINUTE);
        final int second = calendar.get(Calendar.SECOND);

        // 当前几时几分几秒，就把相应圈逆时针旋转多少
        mHourDeg = -360 / 12f * (hour - 1);
        mMinuteDeg = -360 / 60f * (minute - 1);
        mSecondDeg = -360 / 60f * (second - 1);
        // 记录下一时刻
        final float hd = mHourDeg;
        final float md = mMinuteDeg;
        final float sd = mSecondDeg;

        // 动画部分
        // 先移除先前的监听
        mAnimator.removeAllUpdateListeners();
        // 监听动画进度
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // 当前进度
                float av = (float) animation.getAnimatedValue();
                // 时圈的旋转，是分秒的五倍
                if (minute == 0 && second == 0) {
                    mHourDeg = hd + av * 5;
                }
                // 分圈的旋转，和秒圈一样
                if (second == 0) {
                    mMinuteDeg = md + av;
                }
                // 秒圈的旋转
                mSecondDeg = sd + av;

                // 进行视图重绘
                invalidate();
            }
        });
        // 执行动画
        mAnimator.start();
    }
}
