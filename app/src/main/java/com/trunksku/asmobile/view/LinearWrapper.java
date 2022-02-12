package com.trunksku.asmobile.view;


import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.support.graphics.drawable.ArgbEvaluator;
import android.util.AttributeSet;
import android.view.ViewAnimationUtils;
import android.widget.LinearLayout;

public class LinearWrapper extends LinearLayout {
    private Toaster mToaster;

    public LinearWrapper(Context context) {
        super(context);
    }

    public LinearWrapper(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LinearWrapper(Context context, AttributeSet attrs, int style) {
        super(context, attrs, style);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        startRevealAnimation();
        startIconColorChangeAnimation();
    }

    protected void startIconColorChangeAnimation() {
        ValueAnimator anim = ValueAnimator.ofObject(new ArgbEvaluator(), Color.WHITE, mToaster.getIconColor());
        anim.setDuration(Toaster.REVEAL_ANIM_DURATION + 300);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator p1) {
                mToaster.getIcon().setTint((int) p1.getAnimatedValue());
            }
        });
        anim.start();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        setBackgroundColor(Color.TRANSPARENT);
    }

    public void setToasterInstance(Toaster toaster) {
        this.mToaster = toaster;
    }

    protected void startRevealAnimation() {
        setBackgroundDrawable(mToaster.getBackroundDrawable());
        int x = (getLeft());// + getRight()) / 2;
        int y = (getTop() + getBottom()) / 2;
        int endRadius = Math.max(getHeight(), getWidth());
        Animator anim = ViewAnimationUtils.createCircularReveal(this, x, y, 0, endRadius);
        anim.setDuration(Toaster.REVEAL_ANIM_DURATION);
        anim.start();
    }
}

