package com.trunksku.asmobile.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TemplateView extends LinearLayout {
    public final ViewHolder holder = new ViewHolder(this);
    private Context context;

    public TemplateView(Context context) {
        super(context);
        this.context = context;
        ImageView imageView = new ImageView(context);
        TextView textView = new TextView(context);
        textView.setTextAppearance(context, 16973890);
        textView.setTextSize((float) 16);
        TextView textView2 = new TextView(context);
        textView2.setTextAppearance(context, 16973894);
        textView2.setTextSize((float) 14);
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setPadding(0, 0, dp2px((float) 10), 0);
        linearLayout.addView(imageView, dp2px((float) 36), dp2px((float) 36));
        LinearLayout linearLayout2 = new LinearLayout(context);
        linearLayout2.setOrientation(1);
        linearLayout2.addView(textView);
        linearLayout2.addView(textView2);
        this.holder.ll = linearLayout;
        this.holder.lll = linearLayout2;
        this.holder.icon = imageView;
        this.holder.title = textView;
        this.holder.subtitle = textView2;
        addView(linearLayout);
        addView(linearLayout2);
        setPadding(dp2px((float) 32), dp2px((float) 16), dp2px((float) 16), dp2px((float) 16));
        setGravity(16);
    }

    public static int dp2px(float f) {
        return (int) ((Resources.getSystem().getDisplayMetrics().density * f) + 0.5f);
    }

    public class ViewHolder {
        private final TemplateView this$0;
        public ImageView icon;
        public View ll;
        public View lll;
        public TextView subtitle;
        public TextView title;


        public ViewHolder(TemplateView templateView) {
            this.this$0 = templateView;
        }

        public void setIcon(Bitmap bitmap, boolean z) {
            this.icon.setImageBitmap(bitmap);
            if (z) {
                checkEmpty(this.icon, bitmap);
            }
        }

        public void setIcon(int bitmap, boolean z) {
            this.icon.setImageResource(bitmap);
            if (z) {
                checkEmpty(this.icon, bitmap);
            }
        }

        public void setTitle(CharSequence charSequence) {
            this.title.setText(charSequence);
            checkEmpty(this.title, charSequence);
        }

        public void setSubtitle(CharSequence charSequence) {
            this.subtitle.setText(charSequence);
            checkEmpty(this.subtitle, charSequence);
        }

        private void checkEmpty(View view, Object obj) {
            if (obj == null) {
                view.setVisibility(8);
            } else {
                view.setVisibility(0);
            }
        }
    }
}

