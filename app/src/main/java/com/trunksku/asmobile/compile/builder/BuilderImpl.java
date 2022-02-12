package com.trunksku.asmobile.compile.builder;

import android.content.Context;

import java.util.ArrayList;

public abstract class BuilderImpl implements IBuilder {
    protected Context mContext;
    protected boolean mVerbose;

    public BuilderImpl(Context context) {
        mContext = context;

        mVerbose = true;
    }

    protected boolean runTasks(ArrayList<Task> tasks) {
        for (Task task : tasks) {
            try {
                //  stdout("Run " + task.getTaskName() + " task");
                boolean result = task.doFullTaskAction();
                if (!result) {
                    // stdout(task.getTaskName() + " failed");
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
                //stdout(task.getTaskName() + " failed");
                return false;
            }
        }
        return true;
    }
}


