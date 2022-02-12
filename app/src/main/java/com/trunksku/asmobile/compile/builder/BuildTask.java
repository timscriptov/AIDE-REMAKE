package com.trunksku.asmobile.compile.builder;

import android.os.AsyncTask;

import com.trunksku.asmobile.compile.builder.model.BuildType;

public class BuildTask extends AsyncTask<String, Object, Boolean> {
    private final IBuilder builder;
    private final BuildTask.CompileListener listener;
    private Exception exception;

    public BuildTask(IBuilder builder, BuildTask.CompileListener listener) {
        this.builder = builder;
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        listener.onStart();
    }

    @Override
    protected Boolean doInBackground(String... params) {
        try {
            return builder.build(BuildType.DEBUG);
        } catch (Exception e) {
            exception = e;
            return false;
        }
    }

    @Override
    protected void onPostExecute(final Boolean result) {
        super.onPostExecute(result);
        if (!result) {
            listener.onError(exception);
        } else {
            listener.onComplete();
        }
    }

    public interface CompileListener {
        void onStart();

        void onError(Exception project);

        void onComplete();
    }
}

