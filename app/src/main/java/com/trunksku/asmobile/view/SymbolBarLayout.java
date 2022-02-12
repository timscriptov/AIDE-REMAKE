package com.trunksku.asmobile.view;


import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.trunksku.asmobile.R;


/**
 * @author Jecelyin Peng <jecelyin@gmail.com>
 */
public class SymbolBarLayout extends LinearLayout implements View.OnClickListener {
    public static final String VALUE_SYMBOL = TextUtils.join("\n", new String[]{"\\t", "{", "}", "<", ">"
            , ",", ";", "'", "\"", "(", ")", "/", "\\", "%", "[", "]", "|", "#", "=", "$", ":"
            , "&", "?", "!", "@", "^", "+", "*", "-", "_", "`", "\\n"});
    private OnSymbolCharClickListener onSymbolCharClickListener;
    private String[] charList;

    public SymbolBarLayout(Context context) {
        super(context);
        init();
    }

    public SymbolBarLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    public SymbolBarLayout(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);
        init();
    }

    private void init() {
        setOrientation(HORIZONTAL);

        String symbol;

        symbol = VALUE_SYMBOL;

        charList = TextUtils.split(symbol, "\n");

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        makeItemViews(getContext());
    }

    private void makeItemViews(Context context) {
        if (charList == null || charList.length == 0)
            return;

        LayoutInflater inflater = LayoutInflater.from(context);

        TextView tv;
        for (String str : charList) {
            tv = (TextView) inflater.inflate(R.layout.list_item_symbol, this, false);
            tv.setText(str);

            tv.setOnClickListener(this);
            addView(tv);
        }
    }

    @Override
    public void onClick(View v) {
        if (onSymbolCharClickListener == null)
            return;

        String str = ((TextView) v).getText().toString();
        if ("\\t".equals(str)) {
            str = "\t";
        } else if ("\\n".equals(str)) {
            str = "\n";
        }
        onSymbolCharClickListener.onClick(v, str);
    }

    public void setOnSymbolCharClickListener(OnSymbolCharClickListener listener) {
        this.onSymbolCharClickListener = listener;
    }

    public interface OnSymbolCharClickListener {
        void onClick(View v, String text);
    }
}

