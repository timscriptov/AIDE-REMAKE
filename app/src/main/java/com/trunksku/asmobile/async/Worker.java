package com.trunksku.asmobile.async;

import android.os.AsyncTask;

public class Worker extends AsyncTask<Void, Integer, String> {
    private WorkerAction workerAction;

    public Worker(WorkerAction work) {
        workerAction = work;
    }

    @Override
    protected void onPreExecute() {
        workerAction.runFirst();

        super.onPreExecute();

    }

    @Override
    protected String doInBackground(Void[] p1) {
        workerAction.run();
        return "love";
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        workerAction.runLast();
    }

    public interface WorkerAction {
        void run();

        void runFirst();

        void runLast();
    }

}

