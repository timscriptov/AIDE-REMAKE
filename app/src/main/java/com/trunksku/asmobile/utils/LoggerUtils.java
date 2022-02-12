package com.trunksku.asmobile.utils;

import android.os.Environment;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class LoggerUtils {
    public static void writeLog(String info) {
        BufferedWriter bw = null;
        FileWriter fw = null;
        try {
            fw = new FileWriter(Environment.getExternalStorageDirectory() + "/AndroidStudio/Logger.txt", true);
            bw = new BufferedWriter(fw);
            bw.write(info);
            bw.write("\n");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bw != null)
                    bw.close();
                if (fw != null)
                    fw.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}