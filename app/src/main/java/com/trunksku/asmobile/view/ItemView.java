package com.trunksku.asmobile.view;

import android.content.Context;
import android.content.res.Resources;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ItemView extends LinearLayout {
    final CheckBox cb;
    final Context ctx;
    final TextView text;

    public ItemView(Context context) {
        super(context);
        this.ctx = context;
        int dp2px = dp2px((float) 10);
        setPadding(dp2px, dp2px, dp2px, dp2px);
        this.cb = new CheckBox(context);
        this.text = new TextView(context);
        setGravity(16);
        this.cb.setClickable(false);
        this.cb.setFocusable(false);
        this.text.setPadding(dp2px((float) 10), 0, 0, 0);
        this.text.setTextAppearance(context, 16973890);
        this.text.setTextSize((float) 15);
        addView(this.cb);
        addView(this.text);
    }

    public static int dp2px(float f) {
        return (int) ((Resources.getSystem().getDisplayMetrics().density * f) + 0.5f);
    }

    public void setChecked(boolean z) {
        this.cb.setChecked(z);
    }

    public void setText(CharSequence charSequence) {
        this.text.setText(charSequence);
    }


}
