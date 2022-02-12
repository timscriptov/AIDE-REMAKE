package com.trunksku.asmobile.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipUtils {

    public static void unzip(InputStream zipFile, File targetDirectory) throws Exception {
        ZipInputStream in = new ZipInputStream(new BufferedInputStream(zipFile));
        try {
            ZipEntry ze;
            int count;
            byte[] buffer = new byte[8192];
            while ((ze = in.getNextEntry()) != null) {
                File file = new File(targetDirectory, ze.getName());
                File dir = ze.isDirectory() ? file : file.getParentFile();
                if (!dir.isDirectory() && !dir.mkdirs()) {
                    throw new Exception("Failed to ensure directory: " + dir.getAbsolutePath());
                }
                if (ze.isDirectory()) {
                    continue;
                }
                BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file));
                try {
                    while ((count = in.read(buffer)) != -1)
                        out.write(buffer, 0, count);
                } finally {
                    out.close();
                }
                long time = ze.getTime();
                if (time > 0) {
                    file.setLastModified(time);
                }
            }
        } finally {
            in.close();
        }
    }

    public static boolean unzipFile(String zipFile, String outputFile) {
        byte[] buffer = new byte[2048];
        try {
            //create output directory is not exists
            float prev = -1; // to check if the percent changed and its worth updating the UI
            int finalSize = 0;
            float current = 0;

            /* File folder = new File(xpath + "/gen/assets");
             if (!folder.exists()) {
             folder.mkdir();
             }*/

            //get the zip file content
            finalSize = (int) new File(zipFile).length();
            ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
            //get the zipped file list entry
            ZipEntry ze = zis.getNextEntry();
            while (ze != null) {
                current += ze.getCompressedSize();
                FileOutputStream fos = null;
                String fileName = ze.getName();
                /*if (fileName.contains("lua.mph")) {
                 addLog("APK is already protected!");
                 listener.onProtected();
                 return false;
                 } else */
                if (!fileName.contains("META-INF")) {
                    File newFile = new File(outputFile + File.separator + fileName);
                    new File(newFile.getParent()).mkdirs();
                    fos = new FileOutputStream(newFile);
                    //addLog("Recompiling - " + fileName);
                    do {
                        int numread = zis.read(buffer);
                        if (numread <= 0) {
                            break;
                        }
                        fos.write(buffer, 0, numread);
                        if (prev != current / finalSize * 100) {
                            prev = current / finalSize * 100;
                            //System.out.println("Extract ->" + ((int) prev) + "%");

                            // publishProgress(mi.getAppName(), "Compiling... " + ((int) prev) + "%", "40");
                        }
                    } while (true);
                    fos.close();
                }
                ze = zis.getNextEntry();
            }
            //new File(zipFile).delete();
            //Utils.deleteFolder(new File(xpath + File.separator + "gen" + File.separator + "META-INF"));
            // addLog("Extracting apk complete");
            return true;
        } catch (Exception ex) {
            // addLog(ex.getMessage());
            return false;
        }
    }

    public static String getFiles() throws Exception {
        ZipInputStream zis = new ZipInputStream(new FileInputStream(""));
        ZipEntry temp = null;
        while ((temp = zis.getNextEntry()) != null) {
           /* if(!temp.isDirectory()){
                Path currentPath = Paths.get(temp.getName());                    
                if(pathToBrowse.equals(currentPath.getParent())){
                    // Here is the file name
                    System.out.println(currentPath.getFileName());
                }
            }*/
        }
        return "";
    }

}
