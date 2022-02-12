package com.trunksku.asmobile.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.trunksku.asmobile.model.Highlight;
import com.trunksku.asmobile.view.ColorBackgroundTextView;

import java.util.ArrayList;
import java.util.List;

public class HighlightAdapter extends BaseAdapter {
    private Context context;
    private boolean isLight;
    private List<Highlight> list = new ArrayList();

    public HighlightAdapter(Context context, List<Highlight> list) {
        this.context = context;
        this.list = list;
    }

    public /* bridge */ Object m0getItem(int i) {
        return getItem(i);
    }

    public List<Highlight> getList() {
        return this.list;
    }

    public void setList(List<Highlight> list) {
        this.list = list;
    }

    public void setIsLight(boolean z) {
        this.isLight = z;
    }

    public boolean isLight() {
        return this.isLight;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null) {
            view = getItemView();
            viewHolder = new ViewHolder(this, view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        Highlight item = getItem(i);
        viewHolder.title.setText(item.getTitle());
        try {
            viewHolder.subtitle.setColor(Color.parseColor(item.getColor(this.isLight)));
        } catch (Throwable th) {
            Toast.makeText(context, th.getMessage(), 1).show();

        }
        return view;
    }

    public View getItemView() {
        LinearLayout linearLayout = new LinearLayout(this.context);
        linearLayout.setOrientation(1);
        linearLayout.setGravity(16);
        linearLayout.setPadding(dp2px((float) 16), dp2px((float) 14), dp2px((float) 16), dp2px((float) 14));
        TextView textView = new TextView(this.context);
        textView.setId(16908308);
        textView.setTextAppearance(this.context, 16973890);
        textView.setTextSize((float) 16);
        textView.setPadding(0, 0, 0, dp2px((float) 5));
        ColorBackgroundTextView colorBackgroundTextView = new ColorBackgroundTextView(this.context);
        colorBackgroundTextView.setId(16908309);
        colorBackgroundTextView.setTextSize((float) 14);
        linearLayout.addView(textView);
        linearLayout.addView(colorBackgroundTextView, -1, -2);
        return linearLayout;
    }

    @Override
    public int getCount() {
        return this.list.size();
    }

    @Override
    public Highlight getItem(int i) {
        return (Highlight) this.list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return (long) i;
    }

    public int dp2px(float f) {
        return (int) ((context.getResources().getDisplayMetrics().density * f) + 0.5f);
    }

    private class ViewHolder {
        private final HighlightAdapter this$0;
        public View rootView;
        public ColorBackgroundTextView subtitle;
        public TextView title;


        public ViewHolder(HighlightAdapter highlightAdapter, View view) {
            this.this$0 = highlightAdapter;
            this.rootView = view;
            this.title = (TextView) view.findViewById(16908308);
            this.subtitle = (ColorBackgroundTextView) view.findViewById(16908309);
        }
    }
}

