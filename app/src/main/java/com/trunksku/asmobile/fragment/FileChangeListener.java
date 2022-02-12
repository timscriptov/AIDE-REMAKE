package com.trunksku.asmobile.fragment;

import java.io.File;

public interface FileChangeListener {

    void onFileDeleted(File deleted);

    void onFileCreated(File newFile);

    void doOpenFile(File toEdit);

    void onLongClick(File file);
}

