package com.trunksku.asmobile.view;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.widget.TextView;

import java.util.Locale;

public class ColorBackgroundTextView extends TextView {
    private int rectangleSize = 10;

    public ColorBackgroundTextView(Context context) {
        super(context);
        init();
    }

    public ColorBackgroundTextView(Context context, int i) {
        super(context);
        init();
        setColor(i);
    }

    public static String convertToARGB(int i) {
        String toHexString = Integer.toHexString(Color.alpha(i));
        String toHexString2 = Integer.toHexString(Color.red(i));
        String toHexString3 = Integer.toHexString(Color.green(i));
        String toHexString4 = Integer.toHexString(Color.blue(i));
        if (toHexString.length() == 1) {
            toHexString = new StringBuffer().append("0").append(toHexString).toString();
        }
        if (toHexString2.length() == 1) {
            toHexString2 = new StringBuffer().append("0").append(toHexString2).toString();
        }
        if (toHexString3.length() == 1) {
            toHexString3 = new StringBuffer().append("0").append(toHexString3).toString();
        }
        if (toHexString4.length() == 1) {
            toHexString4 = new StringBuffer().append("0").append(toHexString4).toString();
        }
        return new StringBuffer().append(new StringBuffer().append(new StringBuffer().append(new StringBuffer().append("#").append(toHexString).toString()).append(toHexString2).toString()).append(toHexString3).toString()).append(toHexString4).toString().toUpperCase(Locale.ENGLISH);
    }

    public static int dp2px(float f) {
        return (int) ((Resources.getSystem().getDisplayMetrics().density * f) + 0.5f);
    }

    private void init() {
        setPadding(dp2px((float) 5), 0, dp2px((float) 5), 0);
    }

    @Override
    public void setPadding(int i, int i2, int i3, int i4) {
        super.setPadding(i, i2, i3, i4);
    }

    public void setRectangleSize(int i) {
        this.rectangleSize = i;
    }

    public void setColor(int i) {
        setText(convertToARGB(i));
        setBackgroundDrawable(new AlphaPatternDrawable(this, this.rectangleSize, i));
        setTextColor(-1);
    }

    public class AlphaPatternDrawable extends Drawable {
        private final ColorBackgroundTextView this$0;
        private int backgroundColor = 0;
        private Bitmap mBitmap;
        private Paint mPaint = new Paint();
        private Paint mPaintGray = new Paint();
        private Paint mPaintWhite = new Paint();
        private int mRectangleSize = 10;
        private int numRectanglesHorizontal;
        private int numRectanglesVertical;

       /* static ColorBackgroundTextView access$0(AlphaPatternDrawable alphaPatternDrawable) {
            return alphaPatternDrawable.this$0;
        }*/

        public AlphaPatternDrawable(ColorBackgroundTextView colorBackgroundTextView, int i, int i2) {
            this.this$0 = colorBackgroundTextView;
            block$6877();
            this.mRectangleSize = i;
            this.backgroundColor = i2;
        }

        @Override
        public void setAlpha(int i) {
        }

        @Override
        public void setColorFilter(ColorFilter colorFilter) {
        }

        private void block$6877() {
            this.mPaintWhite.setColor(-1);
            this.mPaintGray.setColor(-3421237);
        }

        @Override
        public void draw(Canvas canvas) {
            canvas.drawBitmap(this.mBitmap, (Rect) null, getBounds(), this.mPaint);
            canvas.drawColor(this.backgroundColor);
        }

        @Override
        public int getOpacity() {
            return 0;
        }

        @Override
        protected void onBoundsChange(Rect rect) {
            super.onBoundsChange(rect);
            int height = rect.height();
            this.numRectanglesHorizontal = (int) Math.ceil((double) (rect.width() / this.mRectangleSize));
            this.numRectanglesVertical = (int) Math.ceil((double) (height / this.mRectangleSize));
            generatePatternBitmap();
        }

        private void generatePatternBitmap() {
            if (getBounds().width() > 0 && getBounds().height() > 0) {
                this.mBitmap = Bitmap.createBitmap(getBounds().width(), getBounds().height(), Config.ARGB_8888);
                Canvas canvas = new Canvas(this.mBitmap);
                Rect rect = new Rect();
                Object obj = 1;
                for (int i = 0; i <= this.numRectanglesVertical; i++) {
                    Object obj2 = obj;
                    for (int i2 = 0; i2 <= this.numRectanglesHorizontal; i2++) {
                        rect.top = this.mRectangleSize * i;
                        rect.left = this.mRectangleSize * i2;
                        rect.bottom = rect.top + this.mRectangleSize;
                        rect.right = rect.left + this.mRectangleSize;
                        canvas.drawRect(rect, obj2 != null ? this.mPaintWhite : this.mPaintGray);
                        if (obj2 != null) {
                            obj2 = null;
                        } else {
                            int i3 = 1;
                        }
                    }
                    if (obj != null) {
                        obj = null;
                    } else {
                        int i4 = 1;
                    }
                }
            }
        }
    }
}

