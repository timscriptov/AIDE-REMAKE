package com.trunksku.asmobile.dependency;

import android.util.Log;


import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import android.os.Environment;
import java.util.List;
import java.io.BufferedInputStream;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class DependencyDownloader {

    private String repoPath;

    public interface Listener {
        void onDownload(Dependency dependency);
        void onFinish(List<Dependency> downloaded);
    }

    private static final String TAG = DependencyDownloader.class.getSimpleName();

    // private File mOutputDir;
    //private Set<Dependency> mDownloadedLibraries;
    private Listener mListener;
    //private ILogger mLogger;

    public DependencyDownloader() {
        this.repoPath = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/AndroidStudio", ".maven").getAbsolutePath();
        // mOutputDir = file;
        // mDownloadedLibraries = downloaded;
    }

    public void setListener(Listener listener) {
        mListener = listener;
    }

    /*public void setLogger(ILogger logger) {
     mLogger = logger;
     }*/

    public void download(List<Dependency> libraries) throws Exception {
        List<Dependency> downloaded = new ArrayList<>();
        for (Dependency dependency : libraries) {
            if (downloadToCache(dependency)) {
                downloaded.add(dependency);
            }
        }
        if (mListener != null) {
            mListener.onFinish(downloaded);
        }

    }

    private boolean downloadToCache(Dependency library) throws Exception {
        if (mListener != null) {
            mListener.onDownload(library);
        }

        File cachedLibrary = getFromCache(library);
        if (cachedLibrary != null) {
            return true;
        }

        // if we got into here then we download the library
        // lets try with aar first
        boolean isAar = true;
        InputStream is = DependencyResolver.getAarStream(library);
        if (is == null) {
            // this library doesnt have an aar file, lets try with a jar file
            isAar = false;
            is = DependencyResolver.getJarStream(library);
        }

        if (is == null) {
            /* if (mLogger != null) {
             mLogger.error("Failed to find download link for library " + library);
             }*/
            return false;
        }

        saveToCache(library, is, isAar);



        return true;
    }

    /**
     * Checks if we have downloaded the library before
     * @param dependency library to download
     * @return null if it doesn't exist on the cache otherwise it returns the file
     */
    private File getFromCache(Dependency dependency) {
        File libraryCacheDir = new File(repoPath);
        if (!libraryCacheDir.exists()) {
            return null;
        }

        File libraryToCheck = new File(libraryCacheDir, dependency.getAarDownloadLink());
        if (!libraryToCheck.exists()) {
            libraryToCheck = new File(libraryCacheDir, dependency.getJarDownloadLink());
        }

        if (!libraryToCheck.exists()) {
            return null;
        }

        return libraryToCheck;
    }

    private void saveToCache(Dependency library, InputStream inputStream, boolean isAar) throws Exception {
        File libraryCacheDir = new File(repoPath);
        if (!libraryCacheDir.exists()) {
            if (!libraryCacheDir.mkdir()) {
                throw new IOException("Unable to create library cache directory");
            }
        }

        File libraryFile = new File(libraryCacheDir,   (isAar ? library.getAarDownloadLink() : library.getJarDownloadLink()));
        if (!libraryFile.exists()) {
            copy(inputStream, libraryFile);
        }
    }

    public static void copy(InputStream in, OutputStream out) throws Exception {
        byte[] buffer = new byte[1024];
        while (true) {
            int readCount = in.read(buffer);
            if (readCount < 0) {
                break;
            }
            out.write(buffer, 0, readCount);
        }
    }

    public static void copy(File file, OutputStream out) throws Exception {
        InputStream in = new BufferedInputStream(new FileInputStream(file));
        try {
            copy(in, out);
        } finally {
            in.close();
        }
    }

    public static void copy(InputStream in, File file) throws Exception {
        OutputStream out = new BufferedOutputStream(new FileOutputStream(file));
        try {
            copy(in, out);
        } finally {
            out.close();
        }
    }
}
