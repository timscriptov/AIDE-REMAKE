package com.trunksku.asmobile.compile.helper;

import java.io.File;

public interface BuildCallback {

    public void onStart();

    public void onFailure(String message);

    public void onProgress(String progress);

    public void onSuccess(File apk);

    void aaptOK(String output);

    void aaptERR(String output);

    void javaOK(String output_err);

    void javaERR(String output_err);

}


