package com.trunksku.asmobile.dependency;

import android.os.Environment;

import com.trunksku.asmobile.Applications;
import com.trunksku.asmobile.utils.LoggerUtils;

import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DependencyResolver {

    private static final String TAG = DependencyResolver.class.getSimpleName();
    private static List<String> REPOS = new ArrayList<>();/*Arrays.asList(
       // "https://repo1.maven.org/maven2",
        "https://maven.google.com",
    "https://jitpack.io"
  //  "https://jcenter.bintray.com/"
    );*/
    private static List<String> DefREPOS = Arrays.asList(
            // "https://repo1.maven.org/maven2",
            "https://maven.google.com",
            "https://jitpack.io"
            //"https://jcenter.bintray.com/"
    );
    private static String repoPath;
    final List<Dependency> resolvedDependencies = new ArrayList<>();
    private final List<Dependency> library;
    private DependencyResolver.ResolveTask listener;

    public DependencyResolver(List<Dependency> library, final ResolveTask task) {
        this.listener = task;
        this.library = library;
        String[] repos = Applications.getDefSp().getString("mavenRepos", "").split("\n");
        for (String repo : repos) {
            REPOS.add(repo);
            System.out.println(repo);
            LoggerUtils.writeLog("DependencyResolver(..)" + repo);
        }
        if (REPOS.isEmpty()) {
            System.out.println("No Maven use Default");
            LoggerUtils.writeLog("No Maven use Default");
            REPOS.addAll(DefREPOS);
        }
        repoPath = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/AndroidStudio", ".maven").getAbsolutePath();
    }

    public static InputStream getJarStream(Dependency dep) throws IOException {
        //for (String url : REPOS) {
            URL downloadUrl = new URL(REPOS.get(0) + "/" + dep.getJarDownloadLink());
            URLConnection connection = downloadUrl.openConnection();

            return new BufferedInputStream(connection.getInputStream());
        //}
        //return null;
    }

    public static InputStream getAarStream(Dependency dep) throws IOException {
        //for (String url : REPOS) {
            URL downloadUrl = new URL(REPOS.get(0) + "/" + dep.getAarDownloadLink());
            URLConnection connection = downloadUrl.openConnection();

            return new BufferedInputStream(connection.getInputStream());
        //}
        //return null;
    }

    public static InputStream getInputStream(Dependency dependency) throws IOException {
        //for (String url : REPOS) {
            URL downloadUrl = new URL(REPOS.get(0) + "/" + dependency.getPomDownloadLink());
            URLConnection connection = downloadUrl.openConnection();

            return new BufferedInputStream(connection.getInputStream());
        //}
        //return null;
    }

    public static void writeFile(String path, String str) throws Exception {
        createNewFile(path);
        FileOutputStream dexpro = new FileOutputStream(path);
        dexpro.write(str.getBytes());
    }

    private static void createNewFile(String path) {
        int lastSep = path.lastIndexOf(File.separator);
        if (lastSep > 0) {
            String dirPath = path.substring(0, lastSep);
            File file = new File(dirPath);
            if (!file.exists()) {
                file.mkdirs();
            }
        }
    }

    public void resolve() throws Exception {

        for (Dependency lib : library) {
            resolveDependencymod(lib);
        }
        int size = resolvedDependencies.size();
        listener.onResult(resolvedDependencies);
    }

    private void resolveDependencymod(Dependency parent) throws Exception {
        if (resolvedDependencies.contains(parent)) {
            System.out.println("Skippings " + parent + " to avoid circular dependency");
            LoggerUtils.writeLog("Skippings " + parent + " to avoid circular dependency");
            //return Collections.emptySet();
            return;
        }

        InputStream is = null;
        File pomCache = new File(repoPath, parent.getPomDownloadLink());
        if (pomCache.exists()) {
            try {
                is = new FileInputStream(pomCache.getAbsolutePath());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            is = getInputStream(parent);
        }

        if (is == null) {
            return;
            //return Collections.emptySet();
        }
        String sb = "";
        try {
            sb = new String(readFully(is).toByteArray());
        } catch (IOException e) {
            //sb.equals(e.getMessage());
        } finally {

        }

        listener.onResolving(parent);
        System.out.println("Resolving dependency: " + parent);
        LoggerUtils.writeLog("Resolving dependency: " + parent);

        List<Dependency> dependencies = new ArrayList<>();

        POMParser parser = new POMParser();
        try {

            for (Dependency dep : parser.parse(sb)) {
                // skip test dependencies as its not important for now
                if (dep.getScope() != null && dep.getScope().equals("test")) {
                    continue;
                }
                if (!dependencies.contains(dep)) {
                    dependencies.add(dep);
                }

            }
            //dependencies.add(parent);
        } catch (IOException | XmlPullParserException e) {
            sb = e.getMessage();
            System.out.println("Failed to resolve " + parent + ", " + e.getMessage());
            LoggerUtils.writeLog("Failed to resolve " + parent + ", " + e.getMessage());
        }

        for (Dependency dep : dependencies) {
            if (dep.getScope() != null && dep.getScope().equals("test")) {
                continue;
            }
            resolveDependencymod(dep);
            // dependencies.addAll(resolveDependencymod(dep));
        }

        // dependencies
        //resolvedDependencies.add(parent);
        dependencies.add(parent);
        for (Dependency dp : dependencies) {
            if (!resolvedDependencies.contains(dp)) {
                resolvedDependencies.add(dp);
            }
        }

        //resolvedDependencies.addAll(dependencies);


        saveToCache(sb, parent);
        //is.close();


    }

    private void saveToCache(String contents, Dependency dependency) throws Exception {
        File pomCacheDir = new File(repoPath);

        File file = new File(pomCacheDir, dependency.getPomDownloadLink());
        if (!file.exists()) {
            writeFile(file.getAbsolutePath(), contents);
            /*if (!file.createNewFile()) {
             throw new IOException("Failed to create cache pom file " + file.getName());
             }*/
        }

        //FileUtils.writeStringToFile(file, contents, Charsets.UTF_8);
    }

    private ByteArrayOutputStream readFully(InputStream inputStream)
            throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length = 0;
        while ((length = inputStream.read(buffer)) != -1) {
            baos.write(buffer, 0, length);
        }
        return baos;
    }

    public interface ResolveTask {
        public void onResolving(Dependency dependency);

        public void onResult(List<Dependency> dependencie);
    }
}

