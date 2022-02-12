package com.trunksku.asmobile.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;

public class iScrollView extends HorizontalScrollView {

    FrameLayout frameLayout;
    ScrollView scrollView;

    boolean configured = false;

    int width = -1;
    int height = -1;

    private HorizontalScrollView hscroll;
    private float mx, my, curX, curY;
    private boolean started = false;

    public iScrollView(Context context) {
        super(context, null);

        post(new Runnable() {
            @Override
            public void run() {
                configureView();
            }
        });
    }

    public iScrollView(Context context, AttributeSet attrs) {
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

    }

    public ScrollView getScrollView() {
        return scrollView;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // scrollView.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    public void setChildSize(int width, int height) {
        this.width = width;
        this.height = height;
        if (configured) {
            frameLayout.getLayoutParams().width = width;
            frameLayout.getLayoutParams().height = height;
            frameLayout.getChildAt(0).getLayoutParams().width = width;
            frameLayout.getChildAt(0).getLayoutParams().height = height;
        }

    }


}

