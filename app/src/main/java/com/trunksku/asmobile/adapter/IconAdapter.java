/*package com.trunksku.asmobile.adapter;
import android.widget.BaseAdapter;
import java.util.List;
import java.util.ArrayList;
import android.widget.TextView;
import android.content.Context;
import android.text.TextUtils;
import java.util.Locale;
import android.view.View;
import android.widget.LinearLayout;
import android.view.ViewGroup;
import android.text.SpannableStringBuilder;
import android.text.SpannableString;

public class IconAdapter extends BaseAdapter {
    private List<Icon> backup_list = new ArrayList();
    private Context context;
    private List<Icon> list = new ArrayList();
    private boolean searching;

    public class ViewHolder {
        public SVGImageView icon;
        private final IconAdapter this$0;
        public TextView title;

        public ViewHolder(IconAdapter iconAdapter, View view) {
            this.this$0 = iconAdapter;
            this.icon = (SVGImageView) view.findViewById(16908294);
            this.title = (TextView) view.findViewById(16908310);
        }

        static IconAdapter access$0(ViewHolder viewHolder) {
            return viewHolder.this$0;
        }
    }

    public void search(String str) {
        if (TextUtils.isEmpty(str)) {
            this.list = this.backup_list;
            this.searching = false;
            notifyDataSetChanged();
            return;
        }
        ArrayList arrayList = new ArrayList();
        String toLowerCase = str.toLowerCase(Locale.ENGLISH);
        for (Icon icon : this.backup_list) {
            int indexOf = icon.getFileNameNoExtension().toLowerCase(Locale.ENGLISH).indexOf(toLowerCase);
            if (indexOf >= 0) {
                icon.hig.title.start = indexOf;
                icon.hig.title.end = indexOf + toLowerCase.length();
                arrayList.add(icon);
            }
        }
        this.list = arrayList;
        this.searching = true;
        if (arrayList.size() == 0) {
            notifyDataSetInvalidated();
        } else {
            notifyDataSetChanged();
        }
    }

    private View getItemView() {
        LinearLayout linearLayout = new LinearLayout(this.context);
        int dp2px = Utils.dp2px((float) 16);
        linearLayout.setPadding(dp2px, 0, dp2px, 0);
        linearLayout.setGravity(17);
        linearLayout.setOrientation(1);
        SVGImageView sVGImageView = new SVGImageView(this.context);
        sVGImageView.setId(16908294);
        TextView textView = new TextView(this.context);
        textView.setId(16908310);
        textView.setGravity(17);
        textView.setPadding(0, Utils.dp2px((float) 10), 0, 0);
        textView.setMaxLines(2);
        //textView.setEllipsize(TruncateAt.END);
        textView.setTextSize((float) 13);
        linearLayout.addView(sVGImageView, Utils.dp2px((float) 32), Utils.dp2px((float) 32));
        linearLayout.addView(textView);
        LinearLayout linearLayout2 = new LinearLayout(this.context);
        linearLayout2.addView(linearLayout, -1, Utils.dp2px((float) 110));
        return linearLayout2;
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
        Icon item = getItem(i);
        viewHolder.title.setText(getHighlightText(this.context, item.getFileNameNoExtension(), item.hig.title));
        try {
            viewHolder.icon.setSVG(item.getSvg());
            viewHolder.icon.setColorFilter(-1);
        } catch (Exception e) {
        }
        return view;
    }

    public SpannableStringBuilder getHighlightText(Context context, String str, Highlight highlight) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        SpannableString spannableString = new SpannableString(str);
        if (this.searching && highlight.start >= 0) {
            BackgroundColorSpan backgroundColorSpan = new BackgroundColorSpan(Utils.getColorAccent(context));
            ForegroundColorSpan backgroundColorSpan2 = new BackgroundColorSpan(-1);
            spannableString.setSpan(backgroundColorSpan, highlight.start, highlight.end, 33);
            spannableString.setSpan(backgroundColorSpan2, highlight.start, highlight.end, 33);
        }
        spannableStringBuilder.append(spannableString);
        return spannableStringBuilder;
    }

    public IconAdapter(Context context, List<Icon> list) {
        this.context = context;
        this.list = list;
        this.backup_list = list;
    }

    @Override
    public int getCount() {
        return this.list.size();
    }

    public Icon getItem(int i) {
        return (Icon) this.list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return (long) i;
    }

    public 
    Icon getItem(int i) {
        return getItem(i);
    }
}*/
