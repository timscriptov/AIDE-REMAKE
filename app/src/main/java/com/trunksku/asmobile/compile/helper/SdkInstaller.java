package com.trunksku.asmobile.compile.helper;

import android.content.Context;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SdkInstaller {

    private Context context;

    public SdkInstaller(Context context) {
        this.context = context;
    }

    public void install() {
        installSdk();
        installJars();
        installKeys();
    }

    private void installSdk() {
        try {
            String[] list = context.getAssets().list("sdk");
            for (String s : list) {
                InputStream input = context.getAssets().open("sdk/" + s);
                OutputStream output = new FileOutputStream(new File(new File(context.getFilesDir(), "sdk"), s));
                IOUtils.copy(input, output);
                input.close();
                output.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void installJars() {
        try {
            String[] list = context.getAssets().list("jars");
            for (String s : list) {
                InputStream input = context.getAssets().open("jars/" + s);
                OutputStream output = new FileOutputStream(new File(new File(context.getFilesDir(), "jars"), s));
                IOUtils.copy(input, output);
                input.close();
                output.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void installKeys() {
        try {
            String[] list = context.getAssets().list("key");
            for (String s : list) {
                InputStream input = context.getAssets().open("key/" + s);
                OutputStream output = new FileOutputStream(new File(new File(context.getFilesDir(), "key"), s));
                IOUtils.copy(input, output);
                input.close();
                output.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
