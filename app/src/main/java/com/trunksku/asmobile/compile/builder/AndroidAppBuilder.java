package com.trunksku.asmobile.compile.builder;

import android.content.Context;

import com.trunksku.asmobile.compile.builder.model.BuildType;

import java.util.ArrayList;

public class AndroidAppBuilder extends BuilderImpl {


    public AndroidAppBuilder(Context context) {
        super(context);

    }


    @Override
    public boolean build(BuildType buildType) {
        if (mVerbose) {
            // mStdout.println("Starting build android project");
            // mStdout.println("Build type " + buildType);
        }
        ArrayList<Task> tasks = getTasks(buildType);
        return runTasks(tasks);
    }


    private ArrayList<Task> getTasks(BuildType buildType) {
        ArrayList<Task> tasks = new ArrayList<>();

        /*tasks.add(new CleanTask(this));

        tasks.add(new GenerateBuildConfigTask(this));

        if(isAapt2()) {
            tasks.add(new ProcessAndroidResourceTask2(this));
        } else {
            tasks.add(new ProcessAndroidResourceTask(this));
        }

        tasks.add(new CompileJavaTask(this));

        tasks.add(new DexTask(this));

        tasks.add(new PackageApkTask(this));

        tasks.add(new SignApkTask(this, buildType));

        tasks.add(new AlignApkTask(this));*/
        return tasks;
    }
}
