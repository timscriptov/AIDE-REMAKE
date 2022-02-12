package com.trunksku.asmobile.compile.builder;

public abstract class Task {
    protected final IBuilder mBuilder;

    // protected final Context context;

    public Task(IBuilder builder) {
        this.mBuilder = builder;
        //this.mProject = builder.getProject();
        //this.context = builder.getContext();
    }

    public abstract String getTaskName();


    public abstract boolean doFullTaskAction() throws Exception;

    public IBuilder getBuilder() {
        return mBuilder;
    }
}

