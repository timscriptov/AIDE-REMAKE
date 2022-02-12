package com.trunksku.asmobile.compile.helper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.widget.Toast;

import com.android.sdklib.build.ApkBuilder;
import com.mcal.apksigner.ApkSigner;
import com.trunksku.asmobile.Applications;
import com.trunksku.asmobile.compile.util.BinaryExecutor;
import com.trunksku.asmobile.dependency.Dependency;
import com.trunksku.asmobile.dependency.DependencyDownloader;
import com.trunksku.asmobile.dependency.DependencyResolver;
import com.trunksku.asmobile.utils.FileUtil;
import com.trunksku.asmobile.utils.LoggerUtils;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.FileHeader;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.CompressionLevel;
import net.lingala.zip4j.model.enums.CompressionMethod;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ApkMaker extends AsyncTask<String, String, String> {

    static List<Dependency> dependencies = new ArrayList<>();
    private final boolean isDebug = true;
    private final List<File> allLibs = new ArrayList<>();
    private final List<File> allLibsJar = new ArrayList<>();
    private final SharedPreferences sp;
    private BuildCallback callback;
    private String packageName, versionName, versionCode, minSdkVersion, targetSdkVersion = null;
    private boolean isMinify = false;
    private boolean isJava8 = false;
    private File project;
    private Context context;
    private File bin;
    private File libs;
    private File sdk;
    private File keys;
    private File jars;
    private File buildBin;
    private File gen;
    private File classes;
    private File dexes;
    private File temp;
    private File build;
    private File aidlLibs;

    private int buildMode = 0;

    private File jniLibs;

    private File jarDex;


    public ApkMaker(Context c, int mode) {
        this.context = c;
        this.buildMode = mode;
        this.sp = Applications.getDefSp();
    }

    public void setProjectDir(String str) {
        this.project = new File(str);
    }

    public void setBuildListener(BuildCallback callback) {
        this.callback = callback;
    }

    public void build() {
        bin = new File(context.getFilesDir(), "bin");
        libs = new File(context.getFilesDir(), "libs");
        sdk = new File(context.getFilesDir(), "sdk");
        keys = new File(context.getFilesDir(), "key");
        jars = new File(context.getFilesDir(), "jars");

        aidlLibs = new File(project, "app/src/main/aidl");
        build = new File(project, "app/build");
        buildBin = new File(project, "app/build/bin");
        jniLibs = new File(project, "app/src/main/JniLibs");
        gen = new File(project, "app/build/gen");
        classes = new File(project, "app/build/bin/classes");
        jarDex = new File(project, "app/build/bin/jardex");
        dexes = new File(project, "app/build/bin/dexes");
        temp = new File(project, "app/build/bin/temp");
        this.execute();
    }

    @Override
    protected void onPreExecute() {
        callback.onStart();
        super.onPreExecute();
        try {
            //FileUtil.deleteFile(buildBin.getAbsolutePath());
            if (!bin.exists()) bin.mkdirs();
            if (!libs.exists()) libs.mkdirs();
            if (!sdk.exists()) sdk.mkdirs();
            if (!keys.exists()) keys.mkdirs();
            if (!jars.exists()) jars.mkdirs();

            if (!buildBin.exists()) buildBin.mkdirs();
            if (!gen.exists()) gen.mkdirs();
            if (!classes.exists()) classes.mkdirs();
            if (!dexes.exists()) dexes.mkdirs();
            if (!jarDex.exists()) jarDex.mkdirs();
            if (!temp.exists()) temp.mkdirs();
        } catch (Exception e) {
            e.printStackTrace();
        }
        new SdkInstaller(context).install();
    }

    @Override
    protected String doInBackground(String[] p1) {
        publishProgress("Analyzing Gradle...");
        analyzeGradle();

        publishProgress("Analyzing Dependency...");
        analyzeDependency();

        publishProgress("Resolving Dependency...");
        dependencyResolver();

        publishProgress("Downloading Dependency...");
        dependencyDownloader();

        publishProgress("Indexing...");
        indexing();

        try {
            if (buildMode == 0) {
                publishProgress("Manifest is merging...");
                mergeManifest();

                publishProgress("Generate Firebase Config...");
                generateFirebase();

                publishProgress("Running Aapt...");
                if (sp.getBoolean("useAapt2", false)) {
                    runAapt2();
                } else {
                    runAapt();
                }

                publishProgress("Generate Build Config...");
                generateBuildConfig();

                //publishProgress("Running Aidl...");
                //runAidl();

                publishProgress("Java is compiling...");
                runEcj();

                if (!isMinify) {
                    publishProgress("Dexing...");
                    runDex();
                }

                publishProgress("Merging classes...");
                if (isMinify) {
                    mergeDexR8();
                } else {
                    mergeDex();
                }

                if (!isMinify) {
                    extractFileFromJars();
                }

                publishProgress("Building Apk...");
                buildApk();

                publishProgress("Sign Apk...");
                new ApkSigner().signApk(context, new File(buildBin.getAbsolutePath() + "/app.apk"), new File(buildBin.getAbsolutePath() + "/app-signed.apk"));
                if (sp.getBoolean("useConversion", false)) {
                    publishProgress("Generating AAB...");
                    buildAab();
                }

                publishProgress("Install Apk...");
                install(new File(buildBin, "app-signed.apk"));
            } else if (buildMode == 1) {
                publishProgress("Manifest is merging...");
                mergeManifest();

                if (new File(project + File.separator + "app/google-services.json").exists()) {
                    String content = FileUtil.readFile(project + File.separator + "app/google-services.json");
                    JSONObject jsonObject = new JSONObject(content);
                    JSONObject projectInfo = jsonObject.getJSONObject("project_info");

                    JSONArray clientArray = jsonObject.getJSONArray("client");
                    for (int i = 0; i < clientArray.length(); i++) {
                        JSONObject object = clientArray.getJSONObject(i);
                        JSONObject clientInfo = object.getJSONObject("client_info");
                        JSONObject androidClientInfo = clientInfo.getJSONObject("android_client_info");

                        String clientPackageName = androidClientInfo.getString("package_name");
                        if (packageName.equals(clientPackageName)) {
                            parseConfig(projectInfo, object, clientInfo, new File(project, "app/src/main/res/values/secret.xml"));
                        } else {
                            System.out.println("Invalid google-services.json!");
                        }
                    }
                } else {
                    System.out.println("No google-services.json found.");
                }


                publishProgress("Running Aapt...");
                if (sp.getBoolean("useAapt2", false)) {
                    runAapt2();
                } else {
                    runAapt();
                }
                String buildConfig = "/**\r\n * Automatically generated file. DO NOT MODIFY\r\n */\r\npackage %pack%;\r\n\r\npublic final class BuildConfig {\r\n  public static final boolean DEBUG = Boolean.parseBoolean(\"%debug%\");\r\n}\r\n".replace("%pack%", packageName).replace("%debug%", String.valueOf(isDebug));
                FileUtil.writeFile(gen.getAbsolutePath() + "/" + packageName.replace(".", "/") + "/BuildConfig.java", buildConfig);

                //publishProgress("Running Aidl...");
                //runAidl();

                publishProgress("Java is compiling...");
                runEcj();
            }

        } catch (Exception e) {
            return e.getMessage();
        }

        return "";
    }

    private void generateBuildConfig() {
        String buildConfig = ("/**\n" +
                " * Automatically generated file. DO NOT MODIFY\n" +
                " */\n" +
                "package %pack%;\n" +
                "\n" +
                "public final class BuildConfig {\n" +
                "  public static final boolean DEBUG = Boolean.parseBoolean(\"%debug%\");\n" +
                "  public static final String APPLICATION_ID = \"%pack%\";\n" +
                //"  public static final String BUILD_TYPE = \"debug\";\n" +
                //"  public static final int VERSION_CODE = 1;\n" +
                //"  public static final String VERSION_NAME = \"1.5\";\n" +
                "}\n").replace("%pack%", packageName).replace("%debug%", String.valueOf(isDebug));
        FileUtil.writeFile(gen.getAbsolutePath() + "/" + packageName.replace(".", "/") + "/BuildConfig.java", buildConfig);
    }

    private void generateFirebase() {
        try {
            if (new File(project + File.separator + "app/google-services.json").exists()) {
                String content = FileUtil.readFile(project + File.separator + "app/google-services.json");
                JSONObject jsonObject = new JSONObject(content);
                JSONObject projectInfo = jsonObject.getJSONObject("project_info");

                JSONArray clientArray = jsonObject.getJSONArray("client");
                for (int i = 0; i < clientArray.length(); i++) {
                    JSONObject object = clientArray.getJSONObject(i);
                    JSONObject clientInfo = object.getJSONObject("client_info");
                    JSONObject androidClientInfo = clientInfo.getJSONObject("android_client_info");

                    String clientPackageName = androidClientInfo.getString("package_name");
                    if (packageName.equals(clientPackageName)) {
                        parseConfig(projectInfo, object, clientInfo, new File(project, "app/src/main/res/values/secret.xml"));
                    } else {
                        System.out.println("Invalid google-services.json!");
                    }
                }
            } else {
                System.out.println("No google-services.json found.");
            }
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }

    private void indexing() {
        try {
            for (Dependency dep : dependencies) {
                File pomCacheDir = new File(android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/AndroidStudio", ".maven");
                if (new File(pomCacheDir, dep.getAarDownloadLink()).exists()) {
                    File f = new File(pomCacheDir, dep.getAarDownloadLink());
                    File aarout = new File(f.getAbsolutePath().replace(".aar", ".exploded.aar"));
                    if (!aarout.exists()) {
                        ZipFile zipFile = new ZipFile(f.getAbsolutePath());
                        zipFile.extractAll(aarout.getAbsolutePath());
                        allLibs.add(aarout);
                    } else {
                        allLibs.add(aarout);
                    }
                } else if (new File(pomCacheDir, dep.getJarDownloadLink()).exists()) {
                    File f = new File(pomCacheDir, dep.getJarDownloadLink());
                    if (!allLibsJar.contains(f.getName())) {
                        allLibsJar.add(f);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            File plibs = new File(project, "app/libs");
            if (plibs.exists()) {
                for (File f : plibs.listFiles()) {
                    if (f.getName().endsWith(".jar")) {
                        if (!allLibsJar.contains(f.getName())) {
                            allLibsJar.add(f);
                        }
                    } else if (f.getName().endsWith(".aar")) {
                        File aarout = new File(build.getAbsolutePath() + "/libs", f.getName());
                        if (!aarout.exists()) {
                            ZipFile zipFile = new ZipFile(f.getAbsolutePath());
                            zipFile.extractAll(aarout.getAbsolutePath());
                        }
                    }
                }
            }

            File aar = new File(build, "libs");
            if (aar.exists()) {
                for (File f : aar.listFiles()) {
                    if (!allLibs.contains(f.getName())) {
                        allLibs.add(f);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void analyzeGradle() {
        String gradleFile = FileUtil.readFile(new File(project, "app/build.gradle").getAbsolutePath());
        packageName = findFromLines(gradleFile, "applicationId").replace("\"", "").replace("\n", "");
        versionName = findFromLines(gradleFile, "versionName").replace("\"", "").replace("\n", "");
        versionCode = findFromLines(gradleFile, "versionCode").replace("\n", "");
        minSdkVersion = findFromLines(gradleFile, "minSdkVersion").replace("\n", "");
        targetSdkVersion = findFromLines(gradleFile, "targetSdkVersion").replace("\n", "");

        //isDebug = Boolean.parseBoolean(json.getString("debug"));
        //isMinify = Boolean.parseBoolean(findFromLines(gradleFile, "minifyEnabled"));
        isJava8 = sp.getBoolean("useJava8", false);
        isMinify = sp.getBoolean("useProguard", false);
    }

    private void analyzeDependency() {
        dependencies.clear();
        String content = FileUtil.readFile(new File(project, "app/build.gradle").getAbsolutePath());
        for (String apis : findFromLines(content, "implementation").split("\n")) {
            String api = apis.replace("'", "");
            System.out.println(apis + " " + api.split(":")[0]);

            try {
                Dependency dependency = Dependency.from(api);
                dependency.setUserDefined(true);
                dependencies.add(dependency);
            } catch (Exception e) {
                e.printStackTrace();
            }
            //String[] api = apis.split(":");
            //System.out.println(api[0]+"."+api[1]+"."+api[2]);
        }
        for (Dependency dep : dependencies) {
            System.out.println("implementation " + dep.getFileName());
        }
    }

    private void dependencyResolver() {
        try {
            DependencyResolver resolver = new DependencyResolver(dependencies, new DependencyResolver.ResolveTask() {
                @Override
                public void onResolving(Dependency dependency) {
                    publishProgress("Resolving " + dependency.getFileName());
                }

                @Override
                public void onResult(List<Dependency> result) {
                    //result.addAll(dependencies);
                    dependencies = result;
                    List<String> deps = new ArrayList<>();/* result.stream().map(new Function<Dependency, String>() {
                     @Override
                     public String apply(Dependency dep) {
                     return dep.toString();
                     }
                     }).collect(Collectors.toList());*/
                    for (Dependency res : result) {
                        deps.add(res.getPomDownloadLink());
                    }
                }
            });
            resolver.resolve();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void dependencyDownloader() {
        try {
            DependencyDownloader dd = new DependencyDownloader();
            dd.setListener(new DependencyDownloader.Listener() {

                @Override
                public void onFinish(List<Dependency> downloaded) {
                    dependencies.clear();
                    dependencies.addAll(downloaded);
                    System.out.println("Downloaded " + downloaded.size());
                    LoggerUtils.writeLog("Downloaded " + downloaded.size());
                }

                @Override
                public void onDownload(Dependency dependency) {
                    publishProgress("Downloading " + dependency.getFileName());
                    //System.out.println("Downloading " + dependency.getFileName());
                    LoggerUtils.writeLog("Downloading " + dependency.getFileName());
                }
            });
            dd.download(dependencies);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void parseConfig(JSONObject projectInfo,
                             JSONObject client,
                             JSONObject clientInfo,
                             File secretsFile) throws JSONException, IOException {
        Iterator<String> keys = projectInfo.keys();
        Map<String, String> map = new HashMap<>();
        while (keys.hasNext()) {
            String s = keys.next();
            try {
                map.put(s, projectInfo.getString(s));
            } catch (JSONException ignore) {

            }
        }

        String mobileSdkAppId = clientInfo.getString("mobilesdk_app_id");
        map.put("google_app_id", mobileSdkAppId);

        try {
            String oathClientId = client.getJSONObject("oauth_client")
                    .getString("client_id");
            map.put("default_web_client_id", oathClientId);
        } catch (JSONException ignore) {

        }

        try {
            String apiKey = client.getJSONArray("api_key")
                    .getJSONObject(0)
                    .getString("current_key");
            map.put("google_api_key", apiKey);
            map.put("google_crash_reporting_api_key", apiKey);
        } catch (JSONException ignore) {

        }
        generateXML(map, secretsFile);
    }

    private void generateXML(Map<String, String> config, File secretsFile) throws IOException {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(secretsFile));
            writer.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
            writer.write("<resources>\n");
            writer.write("\t<integer name=\"google_play_services_version\">12451000</integer>\n");
            for (Map.Entry<String, String> entry : config.entrySet()) {
                writer.write("\t<string name=\"");
                writer.write(entry.getKey());
                writer.write("\" translatable=\"false\">");
                writer.write(entry.getValue());
                writer.write("</string>\n");
            }
            writer.write("</resources>");
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void install(File file) {

        if (file.exists()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Uri apkUri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", file);
                Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
                intent.setData(apkUri);
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                context.startActivity(intent);
            } else {
                Uri apkUri = Uri.fromFile(file);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        } else {
            Toast.makeText(context, "ّFile not found!", Toast.LENGTH_SHORT).show();
        }

    }

    private String findFromLines(String lines, String text) {
        int lineNumber = 0;
        String m_str = "";
        try {
            for (String line : lines.split("\\r?\\n")) {
                lineNumber++;
                if (line.contains(text)) {
                    String[] items = line.replace(" ", "").replace(text, text + " ").split(" ");
                    m_str += items[1] + "\n";
                }
            }
            return m_str;
        } catch (Exception e) {
            return null;
        }
    }

    private void buildAab() {
        try {
            File aabOut = new File(buildBin.getAbsolutePath(), "app-unsigned.aab");
            new ApkToAaabConverter(context, new File(buildBin.getAbsolutePath(), "app-signed.apk"), aabOut).start();
            new ApkSigner().signApk(context, aabOut, new File(buildBin.getAbsolutePath() + "/app-signed.aab"));
            aabOut.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addFilesInApk() throws Exception {
        List<File> allLib = new ArrayList<>();
        for (File f : allLibs) {
            File lib = new File(f, "lib");
            if (lib.exists()) {
                allLib.add(lib);
            }
        }
        try {
            ZipFile zip = new ZipFile(buildBin.getAbsolutePath() + "/resources.ap_");
            ZipParameters zipParameters = new ZipParameters();
            zipParameters.setCompressionMethod(CompressionMethod.DEFLATE);
            zipParameters.setCompressionLevel(CompressionLevel.NORMAL);
            for (File f : dexes.listFiles()) {
                if (f.isDirectory()) {
                    zip.addFolder(f, zipParameters);
                } else {
                    zip.addFile(f, zipParameters);
                }
            }
            /*if (lib.exists()) {
                if (lib.listFiles().length != 0) {
                    zip.addFolder(lib, zipParameters);
                }
            }*/
            for (File f : allLib) {
                zip.addFolder(f, zipParameters);
            }
        } catch (Exception e) {
            throw e;
        }
    }

    private void buildApk() {
        int dexCount = 1;
        try {
            ApkBuilder builder = new ApkBuilder(
                    buildBin.getAbsolutePath() + "/app.apk",
                    buildBin.getAbsolutePath() + "/resources.ap_",
                    //dexes.getAbsolutePath() + "/classes.dex",
                    null,
                    null,
                    null);

            for (File extraDex : dexes.listFiles()) {
                if (extraDex.getName().endsWith(".dex")) {
                    dexCount++;
                    builder.addFile(extraDex, Uri.parse(extraDex.getAbsolutePath()).getLastPathSegment());
                }
            }

            if (jniLibs.exists()) {

                builder.addNativeLibraries(jniLibs);
            }
            if (sp.getInt("buildVariant", 0) == 0) {
                builder.setDebugMode(true);
            }
            builder.sealApk();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void extractFileFromJars() {
        List<String> allJar = new ArrayList<>();
        for (File f : allLibs) {
            File jar = new File(f, "classes.jar");
            if (jar.exists()) {
                allJar.add(jar.getAbsolutePath());
            }
        }
        try {
            for (String s : allJar) {
                ZipFile zip = new ZipFile(s);
                for (FileHeader each : zip.getFileHeaders()) {
                    String fileName = each.getFileName();
                    if (fileName.endsWith(".class")) {
                        continue;
                    }
                    if (each.isDirectory()) {
                        continue;
                    }
                    zip.extractFile(fileName, dexes.getAbsolutePath());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void mergeDexR8() {
        List<String> allJar = new ArrayList<>();
        for (File f : allLibs) {
            File jar = new File(f, "classes.jar");
            if (jar.exists()) {
                allJar.add(jar.getAbsolutePath());
            }
        }
        List<String> allPro = new ArrayList<>();
        for (File f : allLibs) {
            File pro = new File(f, "proguard.txt");
            if (pro.exists()) {
                allPro.add(pro.getAbsolutePath());
            }
        }

        try {
            StringBuilder cmd = new StringBuilder();
            cmd.append("dalvikvm -Xcompiler-option --compiler-filter=speed -Xmx512m -cp " + jars.getAbsolutePath() + "/d8.jar" + " com.android.tools.r8.R8");
            cmd.append(" --release");
            cmd.append(" --lib " + sdk.getAbsolutePath() + "/android.jar");
            if (isJava8) {
                cmd.append(" --classpath " + sdk.getAbsolutePath() + "/rt.jar");
            } else {
                cmd.append(" --no-desugaring");
            }
            cmd.append(" --min-api " + minSdkVersion);
            //cmd.append(" --no-tree-shaking");
            //cmd.append(" --no-minification");
            cmd.append(" --pg-map-output " + buildBin.getAbsolutePath() + "/mapping.txt");
            cmd.append(" --output " + dexes.getAbsolutePath());

            File android_rules = new File(context.getFilesDir() + "/sdk/proguard-android.txt");
            if (android_rules.exists()) {
                cmd.append(" --pg-conf " + android_rules.getAbsolutePath());
            } else {
                System.out.println("Proguard Rules not exist");
            }

            File proguard_rules = new File(project, "app/proguard-rules.pro");
            if (proguard_rules.exists()) {
                cmd.append(" --pg-conf " + proguard_rules.getAbsolutePath());
            } else {
                System.out.println("Proguard Rules not exist");
            }

            File aapt_rules = new File(project, "app/build/bin/aapt-rules.txt");
            if (aapt_rules.exists()) {
                cmd.append(" --pg-conf " + aapt_rules.getAbsolutePath());
            } else {
                System.out.println("Proguard Rules not exist");
            }

            for (String s : allPro) {
                cmd.append(" --pg-conf " + s);
            }
            getAllFilesOfDir(".class", classes.getAbsolutePath(), cmd);
            for (String s : allJar) {
                cmd.append(" ");
                cmd.append(s);
            }
            for (File s : allLibsJar) {
                cmd.append(" ");
                cmd.append(s.getAbsolutePath());
            }
            Process dexProcess = Runtime.getRuntime().exec(cmd.toString());
            String error = readInputStreem(dexProcess.getErrorStream());
            if (!error.isEmpty()) {
                if (error.contains("Error")) {
                    throw new Exception(error);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void mergeDex() {
        List<String> allJar = new ArrayList<>();
        for (File f : allLibs) {
            File jar = new File(f, "classes.jar");
            if (jar.exists()) {
                allJar.add(jar.getAbsolutePath());
            }
        }
        List<String> allDex = new ArrayList<>();
        for (File f : allLibs) {
            File dex = new File(f, "classes.dex");
            if (dex.exists()) {
                allDex.add(dex.getAbsolutePath());
            }
        }

        try {
            StringBuilder cmd = new StringBuilder();
            cmd.append("dalvikvm -Xcompiler-option --compiler-filter=speed -Xmx256m -cp " + jars.getAbsolutePath() + "/d8.jar" + " com.android.tools.r8.D8");
            cmd.append(" --release");
            cmd.append(" --lib " + sdk.getAbsolutePath() + "/android.jar");
            if (isJava8) {
                cmd.append(" --classpath " + sdk.getAbsolutePath() + "/rt.jar");
            } else {
                cmd.append(" --no-desugaring");
            }
            cmd.append(" --min-api " + minSdkVersion);
            cmd.append(" --output " + dexes.getAbsolutePath());
            cmd.append(" --intermediate");
            getAllFilesOfDir(".dex", classes.getAbsolutePath(), cmd);
            for (String s : allDex) {
                cmd.append(" ");
                cmd.append(s);
            }
            for (String s : allJar) {
                cmd.append(" ");
                cmd.append(s);
            }
            for (File s : allLibsJar) {
                cmd.append(" ");
                cmd.append(s.getAbsolutePath());
            }
            Process dexProcess = Runtime.getRuntime().exec(cmd.toString());
            String error = readInputStreem(dexProcess.getErrorStream());
            if (!error.isEmpty()) {
                if (error.contains("Error")) {
                    callback.javaERR(error);
                    throw new Exception(error);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void runDex() {
        try {
            StringBuilder cmd = new StringBuilder();
            cmd.append("dalvikvm -Xcompiler-option --compiler-filter=speed -Xmx256m -cp " + jars.getAbsolutePath() + "/d8.jar" + " com.android.tools.r8.D8");
            cmd.append(" --release");
            cmd.append(" --lib " + sdk.getAbsolutePath() + "/android.jar");
            cmd.append(" --min-api " + minSdkVersion);
            if (isJava8) {
                cmd.append(" --classpath " + sdk.getAbsolutePath() + "/rt.jar");
            } else {
                cmd.append(" --no-desugaring");
            }
            cmd.append(" --output " + classes.getAbsolutePath());
            cmd.append(" --intermediate");
            cmd.append(" --file-per-class");
            getAllFilesOfDir(".class", classes.getAbsolutePath(), cmd);
            Process dexProcess = Runtime.getRuntime().exec(cmd.toString());
            String error = readInputStreem(dexProcess.getErrorStream());
            if (!error.isEmpty()) {
                if (error.contains("Error")) {
                    throw new Exception(error);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void runEcj() {
        StringBuilder allJars = new StringBuilder();
        for (File f : allLibs) {
            File jar = new File(f, "classes.jar");
            if (jar.exists()) {
                if (allJars.length() == 0) {
                    allJars.append(jar.getAbsolutePath());
                } else {
                    allJars.append(":");
                    allJars.append(jar.getAbsolutePath());
                }
            }
        }
        StringBuilder allJava = new StringBuilder();
        for (File f : allLibs) {
            File java = new File(f, "java");
            if (java.exists()) {
                if (allJava.length() == 0) {
                    allJava.append(java.getAbsolutePath());
                } else {
                    allJava.append(":");
                    allJava.append(java.getAbsolutePath());
                }
            }
        }
        List<String> allsource = new ArrayList<>();
        for (File f : allLibs) {
            File s = new File(f, "java");
            if (s.exists()) {
                allsource.add(s.getAbsolutePath());
            }
        }

        StringWriter errResult = new StringWriter();
        PrintWriter errWriter = new PrintWriter(errResult);

        try {
            List<String> cmd = new ArrayList<>();
            cmd.add("-proc:none");
            cmd.add("-nowarn");
            if (isJava8) {
                cmd.add("-8");
            } else {
                cmd.add("-7");
            }
            cmd.add("-deprecation");
            cmd.add("-d");
            cmd.add(classes.getAbsolutePath());
            cmd.add("-bootclasspath");
            cmd.add(sdk.getAbsolutePath() + "/android.jar");
            cmd.add("-cp");
            cmd.add(allJars.toString());

            for (File f : allLibsJar) {
                cmd.add("-cp");
                cmd.add(f.getAbsolutePath());
            }

            if (isJava8) {
                cmd.add("-cp");
                cmd.add(sdk.getAbsolutePath() + "/rt.jar");
            }

            cmd.add("-sourcepath");
            cmd.add(gen.getAbsolutePath() + ":" + new File(project, "app/src/main/java").getAbsolutePath() + ":" + allJava.toString());
            cmd.add(gen.getAbsolutePath());
            cmd.add(new File(project, "app/src/main/java").getAbsolutePath());
            for (String s : allsource) {
                cmd.add(s);
            }
            Compiler cm = new Compiler();
            cm.main(context, cmd.toArray(new String[0]), errWriter);
            String error = errResult.toString();
            errWriter.close();
            if (!cm.isErrors()) {
                callback.javaERR(error);
                throw new Exception(error);
            } else {
                callback.javaOK(error);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void runAapt2() {
        StringBuilder allPackages = new StringBuilder();
        for (File f : allLibs) {
            File pack = new File(f, "package.txt");
            if (pack.exists()) {
                if (allPackages.length() == 0) {
                    allPackages.append(FileUtil.readFile(pack.getAbsolutePath()));
                } else {
                    allPackages.append(":");
                    allPackages.append(FileUtil.readFile(pack.getAbsolutePath()));
                }
            }
            File manifest = new File(f, "AndroidManifest.xml");
            if (manifest.exists()) {
                if (allPackages.length() == 0) {
                    allPackages.append(getPackageName(manifest.getAbsolutePath()));
                } else {
                    allPackages.append(":");
                    allPackages.append(getPackageName(manifest.getAbsolutePath()));
                }
            }
        }

        try {
            List<String> args = new ArrayList<>();
            BinaryExecutor executor = new BinaryExecutor();
            args.add(getAapt2File().getAbsolutePath());
            args.add("compile");
            args.add("--dir");
            args.add(new File(project, "app/src/main/res").getAbsolutePath());
            args.add("-o");

            File resPath = new File(build, "res");
            resPath.mkdir();

            File outputPath = createNewFile(resPath, "project.zip");

            args.add(outputPath.getAbsolutePath());

            executor.setCommands(args);
            if (!executor.execute().isEmpty()) {
                callback.aaptERR(executor.getLog());
                throw new Exception(executor.getLog());
            }

            System.out.println("AAPT2 > Compiling libraries");
            compileLibraries();

            args.clear();

            args.add(getAapt2File().getAbsolutePath());
            args.add("link");
            //TODO: custom android.jar
            args.add("-I");
            args.add(sdk.getAbsolutePath() + "/android.jar");

            args.add("--allow-reserved-package-id");
            args.add("--no-version-vectors");
            args.add("--no-version-transitions");
            args.add("--auto-add-overlay");
            args.add("--min-sdk-version");
            args.add(minSdkVersion);
            args.add("--target-sdk-version");
            args.add(targetSdkVersion);
            args.add("--version-code");
            args.add(versionCode);
            args.add("--version-name");
            args.add(versionName);

            if (new File(project, "app/src/main/assets").exists()) {
                args.add("-A");
                args.add(new File(project, "app/src/main/assets").getAbsolutePath());
            }

            File[] resources = resPath.listFiles();
            if (resources != null) {
                for (File resource : resources) {
                    if (resource.isDirectory()) {
                        continue;
                    }
                    if (!resource.getName().endsWith(".zip")) {
                        continue;
                    }
                    args.add("-R");
                    args.add(resource.getAbsolutePath());
                }
            }

            args.add("--java");
            File gen = new File(build, "gen");
            if (!gen.exists()) {
                if (!gen.mkdirs()) {
                    throw new Exception("Failed to create gen folder");
                }
            }
            args.add(gen.getAbsolutePath());


            args.add("--manifest");
            args.add(buildBin.getAbsolutePath() + "/AndroidManifest.xml");


            if (!allPackages.toString().isEmpty()) {
                args.add("--extra-packages");
                args.add(allPackages.toString());
            }

            args.add("-o");
            args.add(new File(buildBin, "resources.ap_").getAbsolutePath());

            executor.setCommands(args);
            if (!executor.execute().isEmpty()) {
                callback.aaptERR(executor.getLog());
                throw new Exception(executor.getLog());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void compileLibraries() throws Exception {
        BinaryExecutor executor = new BinaryExecutor();

        ArrayList<String> args = new ArrayList<>();

        for (File f : allLibs) {
            if (!f.exists()) {
                System.out.println("AAPT2 > Resource folder doesn't exist at path " + f.getAbsolutePath());
                continue;
            }
            if (!new File(f, "res").exists()) {
                System.out.println("AAPT2 > Resource folder doesn't exist at path " + f.getAbsolutePath());
                continue;
            }
            System.out.println("AAPT2 > Compiling library: " + f.getName());
            args.clear();
            args.add(getAapt2File().getAbsolutePath());
            args.add("compile");
            args.add("--dir");
            args.add(new File(f, "res").getAbsolutePath());
            args.add("-o");
            File output = createNewFile(new File(build, "res"), f.getName() + ".zip");
            args.add(output.getAbsolutePath());

            executor.setCommands(args);
            if (!executor.execute().isEmpty()) {
                callback.aaptERR(executor.getLog());
                throw new Exception("AAPT2: " + executor.getLog());
            }
        }
    }

    private void runAapt() {
        List<String> allRes = new ArrayList<>();
        for (File f : allLibs) {
            File res = new File(f, "res");
            if (res.exists()) {
                allRes.add(res.getAbsolutePath());
            }
        }
        List<String> allAssets = new ArrayList<>();
        for (File f : allLibs) {
            File assets = new File(f, "assets");
            if (assets.exists()) {
                allAssets.add(assets.getAbsolutePath());
            }
        }
        List<String> allManifests = new ArrayList<>();
        for (File f : allLibs) {
            File manifest = new File(f, "AndroidManifest.xml");
            if (manifest.exists()) {
                allManifests.add(manifest.getAbsolutePath());
            }
        }
        StringBuilder allPackages = new StringBuilder();
        for (File f : allLibs) {
            File pack = new File(f, "package.txt");
            if (pack.exists()) {
                if (allPackages.length() == 0) {
                    allPackages.append(FileUtil.readFile(pack.getAbsolutePath()));
                } else {
                    allPackages.append(":");
                    allPackages.append(FileUtil.readFile(pack.getAbsolutePath()));
                }
            }
            File manifest = new File(f, "AndroidManifest.xml");
            if (manifest.exists()) {
                if (allPackages.length() == 0) {
                    allPackages.append(getPackageName(manifest.getAbsolutePath()));
                } else {
                    allPackages.append(":");
                    allPackages.append(getPackageName(manifest.getAbsolutePath()));
                }
            }
        }

        try {
            List<String> cmd = new ArrayList<>();
            if (allLibs.size() != 0) {
                cmd.add(getAaptFile().getAbsolutePath());
                cmd.add("package");
                cmd.add("-f");
                cmd.add("--auto-add-overlay");
                for (File library : allLibs) {
                    File manifest = new File(library, "AndroidManifest.xml");
                    if (manifest.exists()) {
                        cmd.add("-M");
                        cmd.add(manifest.getAbsolutePath());
                    }
                }
                cmd.add("-I");
                cmd.add(sdk.getAbsolutePath() + "/android.jar");

                cmd.add("-m");
                cmd.add("-J");
                cmd.add(gen.getAbsolutePath());
                for (File library : allLibs) {
                    File res = new File(library, "res");
                    if (res.exists()) {
                        cmd.add("-S");
                        cmd.add(res.getAbsolutePath());
                    }
                }

                String error2 = new BinaryExecutor().setCommands(cmd).execute();
                if (!error2.isEmpty()) {
                    System.out.println("aapt error");
                    callback.aaptERR(error2);
                    throw new Exception(error2);
                } else {
                    callback.aaptOK(error2);
                }
            }
            cmd.clear();
            cmd.add(getAaptFile().getAbsolutePath());
            cmd.add("p");
            cmd.add("-f");
            cmd.add("--auto-add-overlay");
            //cmd.add("--debug-mode");
            cmd.add("-M");
            cmd.add(buildBin.getAbsolutePath() + "/AndroidManifest.xml");
            cmd.add("-F");
            cmd.add(buildBin.getAbsolutePath() + "/resources.ap_");
            cmd.add("-I");
            cmd.add(sdk.getAbsolutePath() + "/android.jar");

            cmd.add("-m");
            cmd.add("-J");
            cmd.add(gen.getAbsolutePath());

            if (new File(project, "app/src/main/assets").exists()) {
                cmd.add("-A");
                cmd.add(new File(project, "app/src/main/assets").getAbsolutePath());
            }
            if (new File(project, "app/src/main/res").exists()) {
                cmd.add("-S");
                cmd.add(new File(project, "app/src/main/res").getAbsolutePath());
            }


            for (File library : allLibs) {
                File res = new File(library, "res");
                if (res.exists()) {
                    cmd.add("-S");
                    cmd.add(res.getAbsolutePath());
                }
            }
            for (String s : allAssets) {
                cmd.add("-A");
                cmd.add(s);
            }


            cmd.add("--extra-packages");
            cmd.add(allPackages.toString());

            cmd.add("--min-sdk-version");
            cmd.add(minSdkVersion);
            cmd.add("--target-sdk-version");
            cmd.add(targetSdkVersion);
            cmd.add("--version-code");
            cmd.add(versionCode);
            cmd.add("--version-name");
            cmd.add(versionName);
            cmd.add("--no-version-vectors");

            cmd.add("-G");
            cmd.add(buildBin.getAbsolutePath() + "/aapt-rules.txt");

            Process aaptProcess = Runtime.getRuntime().exec(cmd.toArray(new String[0]));
            String error = readInputStreem(aaptProcess.getErrorStream());

            if (!error.isEmpty()) {
                //System.out.println("aapt error");
                callback.aaptERR(error);
                throw new Exception(error);
            } else {
                //System.out.println("aapt ok");
                callback.aaptOK(error);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getPackageName(String manifest) {
        Pattern mPackagePattern = Pattern.compile("(package\\=\".*\")");

        Matcher matcher = mPackagePattern.matcher(FileUtil.readFile(manifest));

        if (matcher.find()) {
            return matcher.group(1).substring(9, matcher.group(1).length() - 1);
        }
        return "";
    }

    private File createNewFile(File parent, String name) throws IOException {
        File createdFile = new File(parent, name);
        parent.mkdirs();
        createdFile.createNewFile();
        return createdFile;
    }

    private File getAapt2File() throws Exception {
        File nativeLibrary = new File(context.getApplicationInfo().nativeLibraryDir + "/libaapt2.so");

        if (!nativeLibrary.exists()) {
            throw new Exception("AAPT2 binary not found");
        }

        return nativeLibrary;
    }

    private File getAaptFile() throws Exception {
        File nativeLibrary = new File(context.getApplicationInfo().nativeLibraryDir + "/libaapt.so");

        if (!nativeLibrary.exists()) {
            throw new Exception("AAPT2 binary not found");
        }

        return nativeLibrary;
    }

    private File getZipAlignFile() throws Exception {
        File nativeLibrary = new File(context.getApplicationInfo().nativeLibraryDir + "/libzipalign.so");

        if (!nativeLibrary.exists()) {
            throw new Exception("ZipAlign binary not found");
        }

        return nativeLibrary;
    }

    private File getAidlFile() throws Exception {
        File nativeLibrary = new File(context.getApplicationInfo().nativeLibraryDir + "/libaidl.so");

        if (!nativeLibrary.exists()) {
            throw new Exception("Aidl binary not found");
        }

        return nativeLibrary;
    }

    private void runZipalign() throws Exception {
        List<String> ch = new ArrayList<>();
        /*ch.add("chmod");
         ch.add("744");
         ch.add(bin.getAbsolutePath() + "/zipalign");
         Runtime.getRuntime().exec(ch.toArray(new String[0]));*/

        List<String> cmd = new ArrayList<>();
        cmd.add(getZipAlignFile().getAbsolutePath());
        cmd.add("-p");
        cmd.add("-f");
        cmd.add("-v");
        cmd.add("4");
        cmd.add(buildBin.getAbsolutePath() + "/app.apk");
        cmd.add(buildBin.getAbsolutePath() + "/app-aligned.apk");
        Process zipalignProcess = Runtime.getRuntime().exec(cmd.toArray(new String[0]));
        String error = readInputStreem(zipalignProcess.getErrorStream());
        if (!error.isEmpty()) {
            throw new Exception("Aligner:\n\n" + error);
        }
        new File(buildBin.getAbsolutePath() + "/app.apk").delete();
    }

    private void runAidl() throws Exception {
        List<String> allAidl = new ArrayList<>();
        for (File f : allLibs) {
            File aidl = new File(f, ".aidl");
            if (aidl.exists()) {
                allAidl.add(aidl.getAbsolutePath());
            }
        }
        File aidls = new File(project.getAbsolutePath() + "/app/src/main/aidl");
        for (File f : aidls.listFiles()) {
            File ai = new File(f, ".aidl");
            if (ai.exists()) {
                allAidl.add(ai.getAbsolutePath());
            }
        }

        // TODO: NOT FINISHED
        List<String> cmd = new ArrayList<>();
        cmd.add(getAidlFile().getAbsolutePath());
        cmd.add("-p");
        cmd.add(sdk.getAbsolutePath() + "/framework.aidl");
        cmd.add("-o");
        cmd.add(gen.getAbsolutePath());
        cmd.add("-I");
        cmd.add(aidls.getAbsolutePath());
        getAllFilesOfDir(".aidl", aidls.getAbsolutePath(), cmd);
        // cmd.add(aidls.getAbsolutePath() + "/com/test/AidlTest.aidl");
        cmd.add(sdk.getAbsolutePath() + "/framework.aidl");
        // getAllFilesOfDir(".aidl", aidls.getAbsolutePath(), cmd);

        //cmd.add(aidlLibs.getAbsolutePath());
       /* for (String aidl : allAidl) {
            cmd.add(aidl);
        }*/
        System.out.println(cmd.toString());
        //cmd.add(aidls.getAbsolutePath() + "/com/academy/android/aidlserviceexample/IEmpireServiceResponseListener.aidl");
        //cmd.add(mAidlDir.getAbsolutePath() + "/com/test/AidlTest.aidl");

        /* args.add("-p");
         args.add(frameworkFile.getAbsolutePath());
         args.add("-o");
         args.add(mGenDir.getAbsolutePath());
         args.add("-I");
         args.add(mAidlDir.getAbsolutePath());
         args.add(mAidlDir.getAbsolutePath() + "/com/test/AidlTest.aidl");
         */
        Process aidlProcess = Runtime.getRuntime().exec(cmd.toArray(new String[0]));
        String error = readInputStreem(aidlProcess.getErrorStream());
        if (!error.isEmpty()) {
            throw new Exception(error);
        }
    }

    private void mergeManifest() {
        String main = project.getAbsolutePath() + "/app/src/main/AndroidManifest.xml";
        String output = buildBin.getAbsolutePath() + "/AndroidManifest.xml";
        int i = 0;
        for (File f : allLibs) {
            File f2 = new File(f, "AndroidManifest.xml");
            if (f2.exists()) {
                i++;
                FileUtil.writeFile(temp.getAbsolutePath() + "/" + i + ".xml", FileUtil.readFile(f2.getAbsolutePath()).replace("${applicationId}", packageName));
            }
        }
        List<String> manifests = new ArrayList<>();
        for (File f : temp.listFiles()) {
            manifests.add(f.getAbsolutePath());
        }

        String error = com.mcal.manifestmerger.Merger.merge(main, manifests.toArray(new String[0]), output);
        //String error = Merger.merge(c, main, manifests.toArray(new String[0]), output);

        //String error = Merger.merge(c, output, main, new String[0], manifests.toArray(new String[0]));
        //if (error != null) {
        //    throw new Exception(error);
        //}
    }

    private String readInputStreem(InputStream in) {
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append('\n');
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString().trim();
    }

    private List<String> readFile(String str) {
        List<String> list = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(new File(str)));
            while (true) {
                String line = br.readLine();
                if (line == null) {
                    break;
                }
                list.add(line);
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    private void getAllFilesOfDir(String s, String path, StringBuilder sb) {
        File file = new File(path);
        for (File x : file.listFiles()) {
            if (x.isDirectory()) {
                getAllFilesOfDir(s, x.getAbsolutePath(), sb);
            } else if (x.isFile()) {
                if (x.getName().endsWith(s)) {
                    sb.append(" ");
                    sb.append(x.getAbsolutePath());
                }
            }
        }
    }

    private void getAllFilesOfDir(String s, String path, List<String> sb) {
        File file = new File(path);
        for (File x : file.listFiles()) {
            if (x.isDirectory()) {
                getAllFilesOfDir(s, x.getAbsolutePath(), sb);
            } else if (x.isFile()) {
                if (x.getName().endsWith(s)) {
                    sb.add("-I");
                    sb.add(x.getAbsolutePath());
                }
            }
        }
    }

    @Override
    protected void onProgressUpdate(String[] values) {
        callback.onProgress(values[0]);
        super.onProgressUpdate(values);
    }

    @Override
    protected void onCancelled(String result) {
        if (result != null) {
            callback.onFailure(result);
        }
        super.onCancelled(result);
    }

    @Override
    protected void onPostExecute(String result) {

        super.onPostExecute(result);
        if (result.isEmpty()) {
            callback.onSuccess(null);
        } else {
            callback.onFailure(result);
        }
    }
}