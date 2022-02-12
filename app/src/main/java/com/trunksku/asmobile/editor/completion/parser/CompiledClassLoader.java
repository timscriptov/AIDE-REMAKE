package com.trunksku.asmobile.editor.completion.parser;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import dalvik.system.DexClassLoader;

/**
 * Load classes from jar file or dex file
 */
public class CompiledClassLoader {
    private static final String TAG = "CompiledClassLoader";
    private static final String DOT_DEX = ".dex";
    private File mBootClasspath;
    private File mTempDir;

    public CompiledClassLoader(File bootClassPath, File tempDir) {
        mBootClasspath = bootClassPath;
        mTempDir = tempDir;
    }


    public ArrayList<Class> getCompiledClassesFromProject(File file) {
        ArrayList<Class> classes = new ArrayList<>();

        //load all class from bootclasspath
        if (mBootClasspath != null) {
            classes.addAll(getAllClassesFromJar(true, mBootClasspath));
        }

       /* if (project.getDirBuildDexedLibs().listFiles() != null
            && project.getDirBuildDexedLibs().listFiles().length > 0) {
            File[] files = project.getDirBuildDexedLibs().listFiles(new FileFilter() {
                    @Override
                    public boolean accept(File pathname) {
                        return pathname.isFile() && pathname.getName().endsWith(DOT_DEX);
                    }
                });
            for (File lib : files) {
                classes.addAll(getAllClassesFromDex(true, lib.getPath()));
            }
        }*/
        return classes;
    }

    public ArrayList<Class> getAllClassesFromDex(boolean android, String path) {
        Log.d(TAG, "getAllClassesFromDex() called with: android = [" + android + "], path = [" + path + "]");
        DexClassLoader dexClassLoader = new DexClassLoader(path,
                mTempDir.getAbsolutePath(),
                null, ClassLoader.getSystemClassLoader());
        ArrayList<Class> classes = new ArrayList<>();
        try {
            dalvik.system.DexFile dexFile = new dalvik.system.DexFile(path);
            for (Enumeration<String> iter = dexFile.entries(); iter.hasMoreElements(); ) {
                String className = iter.nextElement();
                try {
                    if (android) {
                        Class c = dexClassLoader.loadClass(className);
                        classes.add(c);
                    } else if (!className.startsWith("android")) {
                        Class c = dexClassLoader.loadClass(className);
                        classes.add(c);
                    }
                } catch (ClassNotFoundException e1) {
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return classes;
    }

    public ArrayList<Class> getAllClassesFromJar(boolean android, File path) {
        DexClassLoader dexClassLoader = new DexClassLoader(path.getPath(),
                mTempDir.getAbsolutePath(),
                null, ClassLoader.getSystemClassLoader());
        ArrayList<Class> classes = new ArrayList<>();
        try {
            JarFile jarFile = new JarFile(path);
            Enumeration<JarEntry> e = jarFile.entries();

            while (e.hasMoreElements()) {
                JarEntry je = e.nextElement();
                if (je.isDirectory() || !je.getName().endsWith(".class")) {
                    continue;
                }
                String className = je.getName().substring(0, je.getName().length() - 6);
                className = className.replace('/', '.');
                try {
                    if (android) {
                        Class c = dexClassLoader.loadClass(className);
                        classes.add(c);
                    } else if (!className.startsWith("android")) {
                        Class c = dexClassLoader.loadClass(className);
                        classes.add(c);
                    }
                } catch (ClassNotFoundException e1) {
                    e1.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return classes;
    }
}