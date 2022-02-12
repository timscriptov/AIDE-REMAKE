package com.trunksku.asmobile.compile.helper;


import android.content.Context;

import com.trunksku.asmobile.compile.util.BinaryExecutor;

import org.apache.commons.io.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ApkToAaabConverter {
    private static final int BUFFER_SIZE = 2048;
    private final File AAPT2Binary;
    private final File mBaseZip;
    private final File mProtoOutput;
    private final File mTempDir;

    private File input;

    private File output;

    private Context context;

    public ApkToAaabConverter(Context c, File input, File output) {
        //super(builder)
        this.context = c;
        this.input = input;
        this.output = output;
        AAPT2Binary = new File(c.getApplicationInfo().nativeLibraryDir, "libaapt2.so");
        File file = new File(c.getExternalFilesDir(null), "temp");
        this.mTempDir = file;
        file.mkdirs();
        this.mProtoOutput = new File(file, "proto.zip");
        this.mBaseZip = new File(file, "base.zip");
    }

    private static void $closeResource(Throwable th, AutoCloseable autoCloseable) {
        if (th != null) {
            try {
                autoCloseable.close();
                return;
            } catch (Throwable th2) {
                th.addSuppressed(th2);
                return;
            }
        }
        //autoCloseable.close();
    }

    private void createBaseZip() throws IOException {
        addLog("Creating base.zip");

        try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(mProtoOutput))) {
            try (ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(mBaseZip))) {
                ZipEntry entry;

                while ((entry = zipInputStream.getNextEntry()) != null) {
                    if (entry.getName().endsWith(".dex") && entry.getName().startsWith("classes")) {
                        zipOutputStream.putNextEntry(
                                new ZipEntry("dex" + File.separator + entry.getName()));
                    } else if (entry.getName().equals("AndroidManifest.xml")) {
                        zipOutputStream.putNextEntry(
                                new ZipEntry("manifest" + File.separator + entry.getName()));
                    } else if (entry.getName().startsWith("res" + File.separator)) {
                        zipOutputStream.putNextEntry(new ZipEntry(entry));
                    } else if (entry.getName().startsWith("lib" + File.separator)) {
                        zipOutputStream.putNextEntry(new ZipEntry(entry));
                    } else if (entry.getName().equals("resources.pb")) {
                        zipOutputStream.putNextEntry(new ZipEntry(entry));
                    } else if (entry.getName().startsWith("assets" + File.separator)) {
                        zipOutputStream.putNextEntry(new ZipEntry(entry));

                        // the META-INF folder may contain non-signature-related resources
                        // as well, so we check if the entry doesn't point to a signature
                        // file before adding it
                    } else if (!entry.getName().endsWith(".RSA")
                            && !entry.getName().endsWith(".SF")
                            && !entry.getName().endsWith(".MF")) {
                        zipOutputStream.putNextEntry(new ZipEntry("root" + File.separator + entry.getName()));
                    } else {
                        continue;
                    }
                    //addLog("Adding " + entry.getName() + " to base.zip");

                    byte[] buffer = new byte[BUFFER_SIZE];
                    int len;
                    while ((len = zipInputStream.read(buffer)) != -1) {
                        zipOutputStream.write(buffer, 0, len);
                    }
                }
            }
        }
    }

    private void buildAab() throws IOException {
        addLog("Creating aab");
        PrintStream oldErrorStream = System.err;
        PrintStream oldOutputStream = System.out;
        ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setErr(new PrintStream(errorStream));
        System.setOut(new PrintStream(outputStream));
        try {
            getOutputFile().createNewFile();
            ArrayList<String> args = new ArrayList<>();
            args.add("dalvikvm");
            args.add("-Xcompiler-option");
            args.add("--compiler-filter=speed");
            args.add("-Xmx256m");
            args.add("-Djava.io.tmpdir=" + context.getCacheDir().getAbsolutePath());
            args.add("-cp");
            args.add(context.getFilesDir() + "/bundletool.jar");
            args.add("com.android.tools.build.bundletool.BundleToolMain");
            args.add("build-bundle");
            args.add("--modules=" + mBaseZip.getAbsolutePath());
            args.add("--overwrite");
            args.add("--output=" + getOutputFile().getAbsolutePath());
            //BundleToolMain.main(args.toArray(new String[0]));
            BinaryExecutor executor = new BinaryExecutor();
            executor.setCommands(args);
            if (!executor.execute().isEmpty()) {
                throw new Exception(executor.getLog());
            }
            /*if (!errorStream.toString().isEmpty()) {
             addLog(errorStream.toString());
             throw new RuntimeException(errorStream.toString());
             }*/
            addLog("Successfully converted Apk to AAB");
        } catch (Exception e) {
            addLog(e.getMessage());
        } finally {
            System.out.flush();
            System.err.flush();
            System.setErr(oldErrorStream);
            System.setOut(oldOutputStream);
        }
    }

    private void addLog(String str) {
        // DebugLog.i("kk", str);
        //Toast.makeText(context, str, 1).show();

    }

    private void budletool() throws IOException {
        addLog("Preparing Bundletool");
        File bundletool = new File(context.getFilesDir(), "bundletool.jar");
        if (!bundletool.exists()) {
            InputStream input = context
                    .getAssets().open("jars/bundletool.jar");
            OutputStream output = new FileOutputStream(
                    new File(context.getFilesDir(), "bundletool.jar"));
            IOUtils.copy(input, output);
            addLog("Prepare complete");
        }
    }

    public File getOutputFile() {
        return output;
    }

    public File getInputFile() {
        return input;
    }

    private void createProtoFormatZip() throws Exception {
        mProtoOutput.delete();
        mProtoOutput.createNewFile();
        addLog("Creating proto format zip");
        ProcessBuilder processBuilder = new ProcessBuilder();
        StringWriter stringWriter = new StringWriter();
        List<String> args = new ArrayList<>();
        args.add(AAPT2Binary.getAbsolutePath());
        args.add("convert");
        args.add(getInputFile().getAbsolutePath());
        args.add("-o");
        args.add(mProtoOutput.getAbsolutePath());
        args.add("--output-format");
        args.add("proto");
        processBuilder.command(args);
        Process process = processBuilder.start();
        Scanner scanner = new Scanner(process.getErrorStream());
        boolean hasError = false;
        while (scanner.hasNextLine()) {
            hasError = true;
            String log = scanner.nextLine();
            addLog(log);
            stringWriter.append(log);
            stringWriter.append(System.lineSeparator());
        }
        process.waitFor();
        if (hasError) throw new Exception(stringWriter.toString());
    }

    public void start() throws Exception {
        // DebugLog.clear();
        budletool();
        createProtoFormatZip();
        createBaseZip();
        buildAab();
    }
}


