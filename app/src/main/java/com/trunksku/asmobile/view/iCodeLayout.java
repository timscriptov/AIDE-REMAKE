package com.trunksku.asmobile.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;

public class iCodeLayout extends HorizontalScrollView {

    FrameLayout frameLayout;
    ScrollView scrollView;

    boolean configured = false;

    int width = -1;
    int height = -1;

    private HorizontalScrollView hscroll;

    private GestureDetector gestureDetector;

    private ScaleGestureDetector mScaleDetector;
    private float mScale = 1f;

    public iCodeLayout(Context context) {
        super(context, null);
        post(new Runnable() {
            @Override
            public void run() {
                configureView();
            }
        });
    }

    public iCodeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        post(new Runnable() {
            @Override
            public void run() {
                configureView();
            }
        });
    }

    private void configureView() {
        setFillViewport(true);
        configured = true;
        View child = getChildAt(0);
        removeViewAt(0);
        hscroll = this;
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        scrollView = new ScrollView(getContext());
        frameLayout = new FrameLayout(getContext());
        frameLayout.setLayoutParams(layoutParams);
        scrollView.addView(frameLayout);
        layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        scrollView.setLayoutParams(layoutParams);
        scrollView.setFillViewport(true);
        addView(scrollView);
        frameLayout.addView(child);
        gestureDetector = new GestureDetector(getContext(), new GestureListener());

        mScaleDetector = new ScaleGestureDetector(getContext(), new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                float scale = 1 - detector.getScaleFactor();


                float prevScale = mScale;
                mScale += scale;

                if (mScale < 0.1f) // Minimum scale condition:
                    mScale = 0.1f;

                if (mScale > 10f) // Maximum scale condition:
                    mScale = 10f;
                ScaleAnimation scaleAnimation = new ScaleAnimation(1f / prevScale, 1f / mScale, 1f / prevScale, 1f / mScale, detector.getFocusX(), detector.getFocusY());
                scaleAnimation.setDuration(0);
                scaleAnimation.setFillAfter(true);
                scrollView.startAnimation(scaleAnimation);
                //hscroll.startAnimation(scaleAnimation);
                //getRootView().findViewById(R.id.svSeats).startAnimation(scaleAnimation);
                return true;
            }
        });

    }

    // step 3: override dispatchTouchEvent()
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        super.dispatchTouchEvent(event);
        mScaleDetector.onTouchEvent(event);
        gestureDetector.onTouchEvent(event);
        return gestureDetector.onTouchEvent(event);
    }


    public ScrollView getScrollView() {
        return scrollView;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // scrollView.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        // event when double tap occurs
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            // double tap fired.
            return true;
        }
    }


}

