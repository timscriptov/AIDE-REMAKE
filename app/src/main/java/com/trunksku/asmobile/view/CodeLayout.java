package com.trunksku.asmobile.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;

public class CodeLayout extends ScrollView {
    
    /*private HashMap<Integer, ArrayList<Hint>> hints = new HashMap<Integer, ArrayList<Hint>>(){{
			put(JAVA_LANGUAGE, new ArrayList<>());
		}};*/

    //PushCallback<ScrollChange> scroll;
    public static final float DISTANCE_TO_ZOOM = 10;
    View popupView;
    RecyclerView popupRecycler;
    PopupWindow hintsPopup;
    int popupY = -1;
    int first_pointer_id = 0;
    int second_pointer_id = 1;
    double startDistance = -1;
    boolean canTouch = true;
    private boolean firstOnLayout = true;
    private LinearLayout container;
    private CodeEditor codeText;

    public CodeLayout(Context context) {
        super(context);
        init(context, null);
    }

    public CodeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CodeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public static double getDistanceBetween(float x1, float y1, float x2, float y2) {
        float distance_x = Math.abs(x1 - x2);
        float distance_y = Math.abs(y1 - y2);
        return Math.sqrt(distance_x * distance_x + distance_y * distance_y);
    }

    private void init(@NonNull Context context, @Nullable AttributeSet attrs) {
        //setupHintsList();
        //setupHintsPopup();
        setupTouchZoom();
    }

    @
            SuppressLint("DrawAllocation")
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (firstOnLayout) {
            firstOnLayout = false;
            if (getChildCount() > 0) {
                View child = getChildAt(0);
                if (child instanceof CodeEditor) {
                    setupWithCodeText((CodeEditor) child);
                } else {
                    throw new UnsupportedChildException();
                }
            } else {
                setupWithCodeText(new CodeEditor(getContext()));
            }
        }
    }

    private void setupWithCodeText(CodeEditor text) {
        codeText = text;
        codeText.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //if(popupRecycler != null)
                //popupRecycler.pushValue(TEXT_CHANGED, TextUtils.getCurrentWord(codeText).trim());
            }

            public void afterTextChanged(Editable editable) {
            }
        });
        // creating horizontal scroll
        HorizontalScrollView horizontalScrollView = new HorizontalScrollView(getContext()) {
            @Override
            protected void onScrollChanged(int l, int t, int oldl, int oldt) {
                super.onScrollChanged(l, t, oldl, oldt);
                // pushScroll(t, l, oldt, oldl);
            }
        };
        removeAllViews();
        addView(horizontalScrollView);
        // creating container
        container = new LinearLayout(getContext());
        container.setOrientation(LinearLayout.VERTICAL);
        horizontalScrollView.addView(container);
        container.addView(codeText);
        // adding space to scroll
        View space = new View(getContext());
        container.addView(space);
        space.setLayoutParams(new LinearLayout.LayoutParams(getContext().getResources().getDisplayMetrics().widthPixels, getContext().getResources().getDisplayMetrics().heightPixels));
        // code text setup
        codeText.setMinWidth(getContext().getResources().getDisplayMetrics().widthPixels);
    }

    private void setupTouchZoom() {
        setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                try {
                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_CANCEL:
                        case MotionEvent.ACTION_UP:
                            startDistance = -1;
                            canTouch = true;
                            break;
                        case MotionEvent.ACTION_MOVE:
                            if (motionEvent.getPointerCount() == 2) {
                                int first_pointer_index = motionEvent.findPointerIndex(first_pointer_id);
                                int second_pointer_index = motionEvent.findPointerIndex(second_pointer_id);

                                float x1 = motionEvent.getX(first_pointer_index);
                                float y1 = motionEvent.getY(first_pointer_index);
                                float x2 = motionEvent.getX(second_pointer_index);
                                float y2 = motionEvent.getY(second_pointer_index);

                                double distance = getDistanceBetween(x1, y1, x2, y2);
                                startDistance = startDistance == -1 ? distance : startDistance;

                                double difference = distance - startDistance;

                                if (Math.abs(difference) >= DISTANCE_TO_ZOOM) {
                                    float k = (float) difference / DISTANCE_TO_ZOOM;
                                    startDistance = distance;
                                    int linesAbove = getScrollY() / codeText.getLineHeight();
                                    int textHeightBefore = codeText.getLineHeight() * linesAbove;
                                    //codeText.upTextSize(k);
                                    int textHeightAfter = codeText.getLineHeight() * linesAbove;
                                    scrollBy(0, textHeightAfter - textHeightBefore);
                                }
                                canTouch = false;
                            }
                            break;
                    }
                    performClick();
                    return !canTouch;
                } catch (Exception e) {
                    // Toast.show(ThrowableUtils.toString(e));
                    return true;
                }

            }
        });

    }

    private static class UnsupportedChildException extends RuntimeException {
        UnsupportedChildException() {
            super("CodeLayout can have only CodeText as child");
        }
    }
}
