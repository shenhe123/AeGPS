package com.linkface.baselibrary.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Paint.Style;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import com.linkface.baselibrary.utils.DensityUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 滚动View
 *
 * @author KartyJoin
 *         <p>
 *         TimePickerView.java - 2016-5-10
 */
public class TimePickerView extends View {

    public static final String TAG = "PickerView";
    /**
     * text之间间距和minTextSize之比
     */
    public static final float MARGIN_ALPHA = 3f;
    /**
     * 自动回滚到中间的速度
     */
    public static final float SPEED = 2;

    private List<String> mDataList;
    /**
     * 选中的位置，这个位置是mDataList的中心位置，一直不变
     */
    private int mCurrentSelected;
    private int mIndex;
    private Paint mPaint;

    private float mMaxTextSize = 80;
    private float mMinTextSize = 40;

    private float mMaxTextAlpha = 255;
    private float mMinTextAlpha = 120;

    private int mColorText = 0x0a233d;

    private int mViewHeight;
    private int mViewWidth;

    private float mLastDownY;
    /**
     * 滑动的距离
     */
    private float mMoveLen = 0;
    private boolean isInit = false;
    private onSelectListener mSelectListener;
    private Timer timer;
    private MyTimerTask mTask;
    private String currentText;

    private String tailStr;

    public String getTailStr() {
        return tailStr;
    }

    public void setTailStr(String tailStr) {
        this.tailStr = tailStr;
    }

    public String getCurrentText() {
        return currentText;
    }

    public int getCurrentIndex() {
        return mCurrentSelected;
    }

    public void setCurrentText(String currentText) {
        this.currentText = currentText;
    }

    Handler updateHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if (Math.abs(mMoveLen) < SPEED) {
                mMoveLen = 0;
                if (mTask != null) {
                    mTask.cancel();
                    mTask = null;
                    performSelect();
                    currentText = mDataList.get(mCurrentSelected);
                }
            } else
                // 这里mMoveLen / Math.abs(mMoveLen)是为了保有mMoveLen的正负号，以实现上滚或下滚
                mMoveLen = mMoveLen - mMoveLen / Math.abs(mMoveLen) * SPEED;
            invalidate();
        }

    };

    public TimePickerView(Context context) {
        super(context);
        init();
    }

    public TimePickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void setOnSelectListener(onSelectListener listener) {
        mSelectListener = listener;
    }

    private void performSelect() {
        if (mSelectListener != null)
            mSelectListener.onSelect(mDataList.get(mCurrentSelected), mIndex);
    }

    public void setData(List<String> datas) {
        mDataList = datas;
        // mCurrentSelected = datas.size() / 2;
        mCurrentSelected = 0;
        invalidate();
    }

    public void setData(List<String> datas, int mCurrentSelected) {
        mDataList = datas;
        // mCurrentSelected = datas.size() / 2;
        this.mCurrentSelected = mCurrentSelected;

        invalidate();
    }

    public void setSelected(int selected) {
        mCurrentSelected = selected;
    }

    private void moveHeadToTail() {
        String head = mDataList.get(0);
        mDataList.remove(0);
        mDataList.add(head);
    }

    private void moveTailToHead() {
        String tail = mDataList.get(mDataList.size() - 1);
        mDataList.remove(mDataList.size() - 1);
        mDataList.add(0, tail);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mViewHeight = getMeasuredHeight();
        mViewWidth = getMeasuredWidth();
        // 按照View的高度计算字体大小
        mMaxTextSize = sp2px(getContext(), 18f);
        mMinTextSize = sp2px(getContext(), 18f);
        isInit = true;
        invalidate();
    }

    private void init() {
        timer = new Timer();
        mDataList = new ArrayList<String>();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Style.FILL);
        mPaint.setTextAlign(Align.CENTER);
        mPaint.setColor(mColorText);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 根据index绘制view
        try {
            if (isInit)
                drawData(canvas);
        } catch (Exception e) {
            e.printStackTrace();
            // Toast.makeText(getContext(), "数据异常，请检查网络连接",
            // Toast.LENGTH_SHORT).show();
        }
    }

    private void drawData(Canvas canvas) {

        // canvas.drawLine(0, (int)(mViewHeight / 2.5 + mMoveLen), mViewWidth,
        // (int)(mViewHeight / 2.5 + mMoveLen), mPaint);

        // 先绘制选中的text再往上往下绘制其余的text
        float scale = parabola(mViewHeight / 4.0f, mMoveLen);
        float size = (mMaxTextSize - mMinTextSize) * scale + mMinTextSize;
        mPaint.setTextSize(size);

        // text居中绘制，注意baseline的计算才能达到居中，y值是text中心坐标
        float x = (float) (mViewWidth / 2.0);
        float y = (float) (mViewHeight / 2.0 + mMoveLen);
        FontMetricsInt fmi = mPaint.getFontMetricsInt();
        float baseline = (float) (y - (fmi.bottom / 2.0 + fmi.top / 2.0));
        // 中间加2条实线，圈中选择内容
        mPaint.setAlpha(15);
        canvas.drawLine(DensityUtil.dip2px(getContext(), 16), (int) (mViewHeight / 2.55), mViewWidth - DensityUtil.dip2px(getContext(), 16),
                (int) (mViewHeight / 2.55) + DensityUtil.dip2px(getContext(), 1), mPaint);
        mPaint.setAlpha((int) ((mMaxTextAlpha - mMinTextAlpha) * scale + mMinTextAlpha));
        canvas.drawText(mDataList.get(mCurrentSelected) + tailStr, x, baseline, mPaint);
        mPaint.setAlpha(15);
        canvas.drawLine(DensityUtil.dip2px(getContext(), 16), (int) (mViewHeight / 1.6), mViewWidth - DensityUtil.dip2px(getContext(), 16),
                (int) (mViewHeight / 1.6) + DensityUtil.dip2px(getContext(), 1), mPaint);

        // 绘制上方data
        /*
		 * for (int i = 1; (mCurrentSelected - i) >= 0; i++) {
		 * drawOtherText(canvas, i, -1); } // 绘制下方data for (int i =1;
		 * (mCurrentSelected + i) < mDataList.size(); i++) {
		 * drawOtherText(canvas, i, 1); }
		 */
        if (mDataList.size() < 4) {
            // 根据需求不再绘制上方的数字
            for (int i = 1; i < 2; i++) {
                drawOtherText(canvas, i, -1, mDataList.size() - i);
            }
            // 绘制下方data
            for (int i = 1; i < 2; i++) {
                drawOtherText(canvas, i, 1);
            }
        } else {
            // 根据需求不再绘制上方数据
            for (int i = 1; i < 3; i++) {
                drawOtherText(canvas, i, -1, mDataList.size() - i);
            }
            // 绘制下方data
            for (int i = 1; i < 3; i++) {
                drawOtherText(canvas, i, 1);
            }
        }

    }

    /**
     * @param canvas
     * @param position 距离mCurrentSelected的差值
     * @param type     1表示向下绘制，-1表示向上绘制
     */
    private void drawOtherText(Canvas canvas, int position, int type) {
        float d = MARGIN_ALPHA * mMinTextSize * position + type
                * mMoveLen;
        float scale = parabola(mViewHeight / 4.0f, d);
        float size = (mMaxTextSize - mMinTextSize) * scale + mMinTextSize;
        mPaint.setTextSize(size);
        // mPaint.setAlpha((int) ((mMaxTextAlpha - mMinTextAlpha) * scale +
        // mMinTextAlpha));
        mPaint.setAlpha(60);
        float y = (float) (mViewHeight / 2.0 + type * d);
        FontMetricsInt fmi = mPaint.getFontMetricsInt();
        float baseline = (float) (y - (fmi.bottom / 2.0 + fmi.top / 2.0));
        canvas.drawText(mDataList.get(mCurrentSelected + type * position),
                (float) (mViewWidth / 2.0), baseline, mPaint);
    }

    /**
     * @param canvas
     * @param position 距离mCurrentSelected的差值
     * @param type     1表示向下绘制，-1表示向上绘制
     */
    private void drawOtherText(Canvas canvas, int position, int type, int index) {
        float d = MARGIN_ALPHA * mMinTextSize * position + type
                * mMoveLen;
        float scale = parabola(mViewHeight / 4.0f, d);
        float size = (mMaxTextSize - mMinTextSize) * scale + mMinTextSize;
        mPaint.setTextSize(size);
        // mPaint.setAlpha((int) ((mMaxTextAlpha - mMinTextAlpha) * scale +
        // mMinTextAlpha));
        mPaint.setAlpha(60);
        float y = (float) (mViewHeight / 2.0 + type * d);
        FontMetricsInt fmi = mPaint.getFontMetricsInt();
        float baseline = (float) (y - (fmi.bottom / 2.0 + fmi.top / 2.0));
        canvas.drawText(mDataList.get(index), (float) (mViewWidth / 2.0),
                baseline, mPaint);
    }

    /**
     * 抛物线
     *
     * @param zero 零点坐标
     * @param x    偏移量
     * @return scale
     */
    private float parabola(float zero, float x) {
        float f = (float) (1 - Math.pow(x / zero, 2));
        return f < 0 ? 0 : f;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                doDown(event);
                break;
            case MotionEvent.ACTION_MOVE:
                doMove(event);
                break;
            case MotionEvent.ACTION_UP:
                doUp(event);
                break;
        }
        return true;
    }

    private void doDown(MotionEvent event) {
        if (mTask != null) {
            mTask.cancel();
            mTask = null;
        }
        mLastDownY = event.getY();

    }

    private void doMove(MotionEvent event) {

        mMoveLen += (event.getY() - mLastDownY);
        if (mDataList == null) {
            return;
        }
        if ("".equals(mDataList.get(mDataList.size() - 1))
                && mMoveLen > (MARGIN_ALPHA * mMinTextSize / 2 - 2)) {
            mMoveLen -= (event.getY() - mLastDownY);
            return;
        }
        if ("".equals(mDataList.get(mCurrentSelected + 1))
                && mMoveLen < (-MARGIN_ALPHA * mMinTextSize / 2 + 2)) {
            mMoveLen -= (event.getY() - mLastDownY);
            return;
        }

        if (mMoveLen > MARGIN_ALPHA * mMinTextSize / 2) {
            // 往下滑超过离开距离
            moveTailToHead();
            mMoveLen = mMoveLen - MARGIN_ALPHA * mMinTextSize;

            if (mIndex == 0) {
                mIndex = mDataList.size() - 1;
            } else {
                mIndex--;
            }
        } else if (mMoveLen < -MARGIN_ALPHA * mMinTextSize / 2) {

            // 往上滑超过离开距离
            moveHeadToTail();
            mMoveLen = mMoveLen + MARGIN_ALPHA * mMinTextSize;

            if (mIndex == mDataList.size() - 1) {
                mIndex = 0;
            } else {
                mIndex++;
            }
        }

        mLastDownY = event.getY();
        invalidate();
    }

    private void doUp(MotionEvent event) {
        // 抬起手后mCurrentSelected的位置由当前位置move到中间选中位置
        if (Math.abs(mMoveLen) < 0.0001) {
            mMoveLen = 0;
            return;
        }
        if (mTask != null) {
            mTask.cancel();
            mTask = null;
        }
        mTask = new MyTimerTask(updateHandler);
        timer.schedule(mTask, 0, 10);

    }

    class MyTimerTask extends TimerTask {
        Handler handler;

        public MyTimerTask(Handler handler) {
            this.handler = handler;
        }

        @Override
        public void run() {
            handler.sendMessage(handler.obtainMessage());
        }

    }

    public interface onSelectListener {
        void onSelect(String text, int index);
    }

    /**
     * sp转px
     *
     * @param context
     * @return
     */
    public int sp2px(Context context, float spVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                spVal, context.getResources().getDisplayMetrics());
    }
}