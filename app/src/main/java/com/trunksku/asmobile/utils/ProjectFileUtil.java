package com.trunksku.asmobile.utils;


import android.support.annotation.NonNull;

import com.trunksku.asmobile.Applications;

import java.io.File;

/**
 * Created by duy on 18/07/2017.
 */

public class ProjectFileUtil {

    public static File getProjectHome() {
        return new File(android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/AndroidStudio");
    }

    public static String getCurrentProject() {
        return Applications.getDefSp().getString("CurrentProject", "");

    }

    public static void setCurrentProject(String path) {
        Applications.getDefSp().edit().putString("CurrentProject", path).commit();
    }

    public static boolean inSrcDir(File projectF, File current) {
        try {
            String path = projectF.getPath();
            path += "/src/main/java";
            return current.getPath().startsWith(path);
        } catch (Exception e) {
            return false;
        }
    }


    public static boolean isRoot(File root, File current) {
        try {
            return root.getPath().equals(current.getPath());
        } catch (Exception e) {
            return false;
        }
    }


    @NonNull
    public static String findPackage(File javaDir, File currentFolder) {
        try {
            String path = currentFolder.getPath();
            if (path.startsWith(javaDir.getPath())) {
                String pkg = path.substring(javaDir.getPath().length() + 1);
                pkg = pkg.replace(File.separator, ".");
                return pkg;
            } else {
                return "";
            }
        } catch (Exception e) {
            return "";
        }
    }


}

