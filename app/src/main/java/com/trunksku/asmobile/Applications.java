package com.trunksku.asmobile;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.multidex.MultiDexApplication;

import com.trunksku.asmobile.utils.Environment;
import com.trunksku.asmobile.utils.NightMode;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

public class Applications extends MultiDexApplication {
    private static SharedPreferences sharedPref;
    private static Context context;
    private Thread.UncaughtExceptionHandler uncaughtExceptionHandler;

    public static SharedPreferences getDefSp() {
        return sharedPref;
    }

    public static Context getContext() {
        return context;
    }

    @Override
    public void onCreate() {
        this.uncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable ex) {
                Intent intent = new Intent(getApplicationContext(), DebugActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("error", getStackTrace(ex));
                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 11111, intent, PendingIntent.FLAG_ONE_SHOT);
                AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                am.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, 1000, pendingIntent);
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(2);
                uncaughtExceptionHandler.uncaughtException(thread, ex);
            }
        });
        super.onCreate();
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        context = getApplicationContext();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
            Environment.install(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        NightMode.setMode(true ? NightMode.Mode.NIGHT : NightMode.Mode.DAY);
    }

    private String getStackTrace(Throwable th) {
        final Writer result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(result);
        Throwable cause = th;
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        final String stacktraceAsString = result.toString();
        printWriter.close();
        return stacktraceAsString;
    }
}
