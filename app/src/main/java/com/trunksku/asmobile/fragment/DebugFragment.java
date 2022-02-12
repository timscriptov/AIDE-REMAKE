package com.trunksku.asmobile.fragment;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.trunksku.asmobile.utils.TextPrintStream;

import java.io.PrintStream;

public class DebugFragment extends Fragment {
    public static StringBuffer logs = new StringBuffer();


    public static TextView debug;

    public static void clear() {
        //debug.getEditableText().clear();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @SuppressLint("HandlerLeak")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // mView = inflater.inflate(R.layout.layout_debuger, container, false);
        //iScrollView iscroll = new iScrollView(getActivity());
        final ScrollView sv = new ScrollView(getActivity());
        debug = new TextView(getActivity());
        //iscroll.getScrollView().addView(debug);
        debug.setTextSize(11);
        debug.setPadding(10, 10, 10, 10);
        debug.setTextIsSelectable(true);
        //debug.setFocusable(false);
        // debug.setFocusableInTouchMode(false);
        sv.addView(debug);
        Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    debug.append(msg.obj.toString());
                    int offset = debug.getLineCount() * debug.getLineHeight()
                            - debug.getHeight();
                    if (offset > 0) {
                        // iscroll.scrollTo(0, iscroll.getBottom());
                        sv.scrollTo(0, sv.getBottom());
                        sv.fullScroll(ScrollView.FOCUS_DOWN);
                        //vsb_out.setVisibility(View.VISIBLE);
                    } else {
                        //vsb_out.setVisibility(View.GONE);
                    }

                }
            }
        };

        sv.post(new Runnable() {
            @Override
            public void run() {
                sv.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });

        PrintStream ps = new PrintStream(new TextPrintStream(handler));
        System.setOut(ps);
        //System.setErr(ps);

        return sv;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // t.destroy();
        //debug.getEditableText().clear();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}