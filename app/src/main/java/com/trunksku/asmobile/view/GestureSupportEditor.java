package com.trunksku.asmobile.view;


import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.Scroller;


public class GestureSupportEditor extends CodeEditor
        implements GestureDetector.OnGestureListener {

    protected final Handler mHandler = new Handler();
    private final Rect mVisibleRect = new Rect();
    private OnEditorSizeChangedListener mOnEditorSizeChangedListener;
    @Nullable
    private ScaleGestureDetector mScaleDetector;
    @Nullable
    private GestureDetector mGestureDetector;
    @Nullable
    private Scroller mScroller;
    /**
     * When user zoom in/zoom out, disable long click event
     */
    private boolean mIsTextScaling = false;

    public GestureSupportEditor(Context context) {
        this(context, null);
    }

    public GestureSupportEditor(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    //public GestureSupportEditor(Context context, AttributeSet attrs, int defStyleAttr) {
    //   super(context, attrs, defStyleAttr);
    // init(context);
    //}

    private void init(Context context) {
        mScaleDetector = new ScaleGestureDetector(getContext(), new ScaleListener());
        mGestureDetector = new GestureDetector(getContext(), this);
        mScroller = new Scroller(getContext());
    }

   /* @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mOnEditorSizeChangedListener != null) {
            mOnEditorSizeChangedListener.onEditorSizeChanged(w, h, oldw, oldh);
        }
    }*/

    public void setOnEditorSizeChangedListener(OnEditorSizeChangedListener onEditorSizeChangedListener) {
        this.mOnEditorSizeChangedListener = onEditorSizeChangedListener;
    }

  

   /* @Override
    public int getLineForOffset(int offset) {
        if (getLayout() == null) {
            return -1;
        }
        return getLayout().getLineForOffset(offset);
    }*/

    @Override
    public boolean performLongClick() {
        if (mIsTextScaling) {
            return true;
        }
        return super.performLongClick();
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        try {
            if (!hasSelection() && mScaleDetector != null) {
                mScaleDetector.onTouchEvent(ev);
            }

            if (mGestureDetector != null) {
                mGestureDetector.onTouchEvent(ev);
            }
        } catch (Exception e) {
        }
        return super.onTouchEvent(ev);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        if (mScroller != null) {
            mScroller.forceFinished(true);
        }
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public void computeScroll() {
        if (mIsTextScaling) {
            return;
        }
        if (mScroller != null) {
            if (mScroller.computeScrollOffset()) {
                if (hasFocus()) {
                    clearFocus();
                }
                scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            }
        } else {
            super.computeScroll();
        }
    }

    /**
     * @see GestureDetector.OnGestureListener#onFling(MotionEvent,
     * MotionEvent, float, float)
     */
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                           float velocityY) {

        if (mIsTextScaling) {
            return true;
        }
        if (hasFocus()) {
            clearFocus();
        }
        Layout layout = getLayout();
        if (mScroller != null && layout != null) {
            int maxX = layout.getWidth() - mVisibleRect.width() - getPaddingRight() - getPaddingLeft();
            int maxY = layout.getHeight() - mVisibleRect.height() - getPaddingBottom() - getPaddingTop();
            mScroller.fling(getScrollX(), getScrollY(), -(int) velocityX, -(int) velocityY,
                    0, maxX, 0, maxY);
        }
        return true;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mVisibleRect.set(left, top, right, bottom);
    }

    public interface OnEditorSizeChangedListener {
        void onEditorSizeChanged(int w, int h, int oldw, int oldh);
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        private static final float ONE_FPS = 1000f / 60f;

        private final float minSize;
        private final float maxSize;
        private long mLastTime = 0;
        private int mExpectedTextSizePx = 0;

        ScaleListener() {
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            minSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 14, metrics);
            maxSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 14, metrics);
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            mIsTextScaling = true;
            mExpectedTextSizePx = (int) getTextSize();
            return super.onScaleBegin(detector);
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float size = (mExpectedTextSizePx * detector.getScaleFactor());
            mExpectedTextSizePx = (int) Math.max(minSize, Math.min(size, maxSize * 2));
            postChangeTextSize();
            return true;
        }

        private void postChangeTextSize() {
            if (System.currentTimeMillis() - mLastTime >= ONE_FPS) {
                setTextSize(TypedValue.COMPLEX_UNIT_PX, mExpectedTextSizePx);
                mLastTime = System.currentTimeMillis();
            }
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            super.onScaleEnd(detector);
            mIsTextScaling = false;
        }
    }
}

