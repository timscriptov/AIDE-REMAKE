package com.trunksku.asmobile.utils;


import android.os.Handler;
import android.os.Message;

import java.io.OutputStream;

public class TextPrintStream extends OutputStream {
    Handler handler;
    Message msg;

    public TextPrintStream(Handler h) {
        handler = h;
    }

    @Override
    public void write(int p1) {
    }

    @Override
    public void write(byte[] b) {
        write(b, 0, b.length);
    }

    @Override
    public void write(byte[] b, int off, int len) {
        msg = new Message();
        msg.obj = new String(b, off, len);
        msg.what = 1;
        handler.sendMessage(msg);
    }

}



