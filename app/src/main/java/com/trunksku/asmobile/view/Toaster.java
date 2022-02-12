package com.trunksku.asmobile.view;


import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.graphics.drawable.AnimatedVectorDrawableCompat;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.trunksku.asmobile.R;

public class Toaster {


    public static final int COLOR_SUCCESS = Color.parseColor("#4CAF50");
    public static final int COLOR_ERROR = Color.parseColor("#F44336");
    public static final int REVEAL_ANIM_DURATION = 500;
    public static final int SHORT = 2000;
    public static final int LONG = 3500;
    private Toast mToast;
    private Context mContext;
    private Gravity mGravity;
    private Type mType;
    private Drawable mDrawable;
    private AnimatedVectorDrawableCompat mIcon;
    private int mIconColor = Color.DKGRAY;
    private int mDuration = 0;
    private TextView mText;

    private ImageView mTIcon;

    private LinearWrapper mLWrapper;

    private View layout;

    public Toaster(Context context) {
        this.mContext = context;
        this.mType = Type.INFO;

        this.mDrawable = createBackgroundDrawable();

        createView();
    }

    protected void createView() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        layout = inflater.inflate(R.layout.layout_toast, null);
        mText = (TextView) layout.findViewById(R.id.toast_text);
        mTIcon = (ImageView) layout.findViewById(R.id.toast_image);
        mLWrapper = (LinearWrapper) layout.findViewById(R.id.toast_toastWrapper);
        mLWrapper.setToasterInstance(this);
    }

    protected Drawable createBackgroundDrawable() {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setColor(Color.BLACK/*ContextCompat.getColor(mContext, Color.GRAY)*/);
        drawable.setCornerRadius(10);
        return drawable;
    }

    protected AnimatedVectorDrawableCompat createToastIconAnimation(int tintColor) {
        AnimatedVectorDrawableCompat imageDrawable = AnimatedVectorDrawableCompat.create(mContext, mType == Type.SUCCESS ? R.drawable.ic_ok : R.drawable.ic_error);
        imageDrawable.setTint(tintColor);
        imageDrawable.setTintMode(PorterDuff.Mode.SRC_ATOP);
        mTIcon.setImageDrawable(imageDrawable);
        return imageDrawable;
    }

    public void show() {
        int dp16 = toPx(16);
        mIconColor = mType == Type.SUCCESS ? COLOR_SUCCESS :
                mType == Type.ERROR ? COLOR_ERROR :
                        Color.DKGRAY;
        int gravity = mGravity == Gravity.TOP_LEFT ? (android.view.Gravity.TOP | android.view.Gravity.START) :
                mGravity == Gravity.TOP_RIGHT ? (android.view.Gravity.TOP | android.view.Gravity.END) :
                        mGravity == Gravity.BOTTOM_RIGHT ? (android.view.Gravity.BOTTOM | android.view.Gravity.END) :
                                mGravity == Gravity.BOTTOM_LEFT ? (android.view.Gravity.BOTTOM | android.view.Gravity.START) :
                                        android.view.Gravity.TOP | android.view.Gravity.END;

        mIcon = createToastIconAnimation(mIconColor);

        mToast = Toast.makeText(mContext, mText.getText(), mDuration);
        mToast.setGravity(gravity, dp16, dp16);
        mToast.setDuration(mDuration);

        mToast.setView(layout);
        mToast.show();

        mTIcon.setImageDrawable(mIcon);
        mIcon.start();
    }

    public Toaster setText(String text) {
        if (mText == null)
            createView();

        mText.setText(text);
        return this;
    }

    public Toaster setText(int textResId) {
        if (mText == null)
            createView();

        mText.setText(textResId);
        return this;
    }

    public int getDuration() {
        return mDuration;
    }

    public Toaster setDuration(int mDuration) {
        this.mDuration = mDuration + REVEAL_ANIM_DURATION;
        return this;
    }

    public Type getType() {
        return mType;
    }

    public Toaster setType(Type type) {
        this.mType = type;
        return this;
    }

    private int toPx(int dp) {
        return (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, mContext.getResources().getDisplayMetrics()));
    }

    public Gravity getGravity() {
        return mGravity;
    }

    public Toaster setGravity(Gravity mGravity) {
        this.mGravity = mGravity;
        return this;
    }

    public AnimatedVectorDrawableCompat getIcon() {
        return mIcon;
    }

    public Drawable getBackroundDrawable() {
        return mDrawable;
    }

    public int getIconColor() {
        return mIconColor;
    }

    public enum Type {
        ERROR,
        SUCCESS,
        INFO
    }

    public enum Gravity {
        TOP_LEFT,
        TOP_RIGHT,
        BOTTOM_LEFT,
        BOTTOM_RIGHT
    }
}

