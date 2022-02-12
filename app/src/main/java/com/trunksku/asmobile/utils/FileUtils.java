package com.trunksku.asmobile.utils;

import android.support.annotation.Nullable;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;

public class FileUtils {

    public static void writeFileUI(File file, String string) {
        try {
            FileUtils.refresh(file, false);
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            bw.write(string);
            bw.close();
        } catch (Exception ignored) {
        }
    }

    public static void writeFile(final File file, final String string) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                writeFileUI(file, string);
            }
        }).start();
        //Thread.start(() -> 
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static boolean refresh(@Nullable File file, boolean isDirectory) {
        try {
            if (file == null)
                return true;
            if (isDirectory)
                file.mkdirs();
            else {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static String readFile2String(String path) {
        try {
            InputStream open = new FileInputStream(path);
            ByteArrayOutputStream xf = new ByteArrayOutputStream();
            byte[] bArr = new byte[5242880];
            while (true) {
                int read = open.read(bArr);
                if (read == -1) {
                    break;
                }
                xf.write(bArr, 0, read);
            }
            return new String(xf.toByteArray());
        } catch (Exception e) {
            return null;
        }
    }


}
