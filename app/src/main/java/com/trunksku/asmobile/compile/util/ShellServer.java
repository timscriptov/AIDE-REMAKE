package com.trunksku.asmobile.compile.util;


import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ShellServer extends Thread {
    private final List<Callback> callbacks = Collections.synchronizedList(new ArrayList());
    private BufferedReader output;
    private Process process;

    public ShellServer(Callback callback, Context c, String str, String str2) {
        addCallback(callback);
        ProcessBuilder processBuilder = new ProcessBuilder(new String[]{str});
        processBuilder.directory(new File(str2));
        processBuilder.redirectErrorStream(true);
        try {
            this.process = processBuilder.start();
            this.output = new BufferedReader(new InputStreamReader(this.process.getInputStream()));
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("export HOME=$PWD&&cd\n");
            stringBuilder.append("export TMPDIR=$HOME/tmp\n");
            stringBuilder.append("export JAVA_HOME=$HOME/openjdk-11.0.1\n");
            stringBuilder.append("export ANDROID_HOME=$HOME/android-sdk\n");
            stringBuilder.append("export PATH=$PATH:$HOME/bin\n");
            stringBuilder.append("export PATH=$PATH:$HOME/openjdk-11.0.1/bin\n");
            stringBuilder.append("export PATH=$PATH:$HOME/gradle-6.6.1/bin\n");
            stringBuilder.append("export PATH=$PATH:$HOME/android-sdk/cmdline-tools/latest/bin\n");
            stringBuilder.append("export PATH=$PATH:$HOME/android-sdk/cmake/bin\n");
            stringBuilder.append(String.format("export AIDE_DATA=%s\n", new Object[]{c.getFilesDir().getParentFile().getAbsolutePath()}));
            stringBuilder.append(String.format("export AIDE_FILES=%s\n", new Object[]{c.getFilesDir().getAbsolutePath()}));
            stringBuilder.append(String.format("export AIDE_EDATA=%s\n", new Object[]{c.getExternalFilesDir("").getParentFile().getAbsolutePath()}));
            stringBuilder.append(String.format("export AIDE_EFILES=%s\n", new Object[]{c.getExternalFilesDir("").getAbsolutePath()}));
            append(stringBuilder.toString(), false);
        } catch (Throwable th) {
            // LogUtils.eTag("ShellServer", new Object[]{th});
            if (callback != null) {
                // callback.output(ThrowableUtils.getFullStackTrace(th).concat("\n"));
            }
        }
    }

    public ShellServer addCallback(Callback callback) {
        if (callback != null) {
            this.callbacks.add(callback);
        }
        return this;
    }

    public ShellServer removeCallback(Callback callback) {
        if (callback != null) {
            this.callbacks.remove(callback);
        }
        return this;
    }

    public void append(String... strArr) {
        for (String append : strArr) {
            append(append);
        }
    }

    public void append(String str) {
        append(str, true);
    }

    public void append(String str, boolean z) {
        try {

            if (z) {
                if (str.endsWith("\n")) {
                    output(str);
                } else {
                    output(str.concat("\n"));
                }

            }
        } catch (Throwable th) {
            output(th.toString().concat("\n"));
            return;
        }
        try {
            this.process.getOutputStream().write(str.concat("\n").getBytes());
            this.process.getOutputStream().flush();
        } catch (IOException e) {
        }

    }

    public void exit() {
        append("exit");
    }

    @Override
    public void run() {
        while (true) {
            try {
                String readLine = this.output.readLine();
                if (readLine == null) {
                    break;
                }
                output(readLine.concat("\n"));
            } catch (Throwable th) {
                output(th.toString().concat("\n"));
                // LogUtils.eTag("Run error", new Object[]{th});
            }
        }
        //CloseUtils.closeIOQuietly(new Closeable[]{this.output, this.process.getInputStream(), this.process.getErrorStream(), this.process.getOutputStream()});
        this.process.destroy();
    }

    public void output(String str) {
        int i = 0;
        if (this.callbacks != null && this.callbacks.size() != 0) {
            Callback[] callbackArr = (Callback[]) this.callbacks.toArray(new Callback[0]);
            while (i < callbackArr.length) {
                callbackArr[i].output(str);
                i++;
            }
        }
    }

    public interface Callback {
        void output(CharSequence charSequence);
    }
}

