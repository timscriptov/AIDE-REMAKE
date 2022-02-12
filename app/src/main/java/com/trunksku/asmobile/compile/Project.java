package com.trunksku.asmobile.compile;


import java.io.File;

/**
 * Structure of app files & directories
 *
 * @author Paul Brussee
 */
public class Project {

    //public static final File dirRoot = android.os.Environment.getExternalStorageDirectory();

    public static File dirProj = null;
    /* PROJECT */
    public static File dirSrc = new File(dirProj, "src");
    public static File dirGen = new File(dirProj, "gen");
    public static File dirRes = new File(dirProj, "res");
    public static File dirLibs = new File(dirProj, "libs");
    public static File dirAssets = new File(dirProj, "assets");
    public static File dirBin = new File(dirProj, "bin");
    public static File xmlMan = new File(dirProj, "AndroidManifest.xml");

    /* BIN */
    public static File dirClasses = new File(dirBin, "classesdebug");
    public static File dirDexedLibs = new File(dirBin, "dexedLibs");
    // File dirBinRes = new File(dirBin, "res");
    // File dirCrunch = new File(dirBinRes, "crunch");

    public static File ap_Resources = new File(dirBin, "resources.ap_");
    public static File dexClasses = new File(dirBin, "classes.dex");
    public static File dexLibs = new File(dirBin, "libs.dex");

    public static File apkUnsigned = new File(dirBin, "app.unsigned.apk");
    public static File apkUnaligned = new File(dirBin, "app.unaligned.apk");

    public static File dirJava;
    public static File apkRedistributable = null;

    public static void loadProj(File dirProject) {
        dirProj = new File(dirProject.getAbsolutePath());
        for (File f : dirProject.listFiles()) {
            if (f.getAbsolutePath().endsWith("src")) {
                dirSrc = f;
            } else if (f.getAbsolutePath().endsWith("java")) {
                dirJava = f;
            } else if (f.getAbsolutePath().endsWith("res")) {
                dirRes = f;
            } else if (f.getAbsolutePath().endsWith("bin")) {
                dirBin = f;
            } else if (f.getAbsolutePath().endsWith("gen")) {
                dirGen = f;
            } else if (f.getAbsolutePath().endsWith("AndroidManifest.xml")) {
                xmlMan = f;
            } else if (f.getAbsolutePath().endsWith("libs")) {

            } else if (f.getAbsolutePath().endsWith("")) {

            } else if (f.getAbsolutePath().endsWith("")) {

            } else if (f.getAbsolutePath().endsWith("")) {

            } else if (f.getAbsolutePath().endsWith("")) {

            }
        }
        // dirSrc = new File(dirProj, "src");
        //dirJava = new File(dirProject, "app/src/main/java");
        // dirGen = new File(dirProj, "build/gen");
        // dirRes = new File(dirProj, "src/main/res");
        dirLibs = new File(dirProj, "libs");
        dirAssets = new File(dirProj, "src/main/assets");
        //dirBin = new File(dirProj, "build/bin");
        // xmlMan = new File(dirProj, "src/main/AndroidManifest.xml");
        dirClasses = new File(dirBin, "classesdebug");
        dirDexedLibs = new File(dirBin, "dexedLibs");
        ap_Resources = new File(dirBin, "resources.ap_");
        dexClasses = new File(dirBin, "classes.dex");
        dexLibs = new File(dirBin, "libs.dex");
        apkUnsigned = new File(dirBin, "app.unsigned.apk");
        apkUnaligned = new File(dirBin, "app.apk");

        //mkDirs();
    }

    // public static final File dirDist = new File(dirProj, "dist");


    /* RES */
   /* public static final File dirDrawLdpi = new File(dirRes, "drawable-ldpi");
    public static final File dirDrawMdpi = new File(dirRes, "drawable-mdpi");
    public static final File dirDrawHdpi = new File(dirRes, "drawable-hdpi");
    public static final File dirDrawXhdpi = new File(dirRes, "drawable-xhdpi");
    public static final File dirDrawXxhdpi = new File(dirRes, "drawable-xxhdpi");
    public static final File dirLayout = new File(dirRes, "layout");
    public static final File dirValues = new File(dirRes, "values");
    public static final File dirValues11 = new File(dirRes, "values-v11");
    public static final File dirValues14 = new File(dirRes, "values-v14");
*/
    // File pngAppIconLdpi = new File(dirDrawLdpi, "app_icon.png");
   /* public static final File pngAppIconMdpi = new File(dirDrawMdpi, "app_icon.png");
    public static final File pngAppIconHdpi = new File(dirDrawHdpi, "app_icon.png");
    public static final File pngAppIconXhdpi = new File(dirDrawXhdpi, "app_icon.png");
    public static final File pngAppIconXxhdpi = new File(dirDrawXxhdpi, "app_icon.png");

    public static final File xmlActivitySelfCompile = new File(dirLayout, "activity_self_compile.xml");
    public static final File xmlActivityPickApp = new File(dirLayout, "activity_pick_app.xml");
    public static final File xmlItemAppInfo = new File(dirLayout, "item_app_info.xml");

    public static final File xmlStrings = new File(dirValues, "strings.xml");
    public static final File xmlStyles = new File(dirValues, "styles.xml");
    public static final File xmlStyles11 = new File(dirValues11, "styles.xml");
    public static final File xmlStyles14 = new File(dirValues14, "styles.xml");
*/
    // File xmlBinMan = new File(dirBin, "AndroidManifest.xml");


    /* DIST */

    public static void mkDirs() {
        // dirRoot.mkdirs();
        //dirProj.mkdirs();
        //dirSrc.mkdirs();
        dirGen.mkdirs();
        //dirRes.mkdirs();
        // dirLibs.mkdirs();
        // dirAssets.mkdirs();
        dirClasses.mkdirs();
        dirBin.mkdirs();
        dirDexedLibs.mkdirs();
        // dirDist.mkdirs();
    }

    public static String getJavaClassPath() {
        String strClassPath = "";
        for (File jarLib : dirLibs.listFiles()) {
            if (jarLib.isFile() && jarLib.getName().endsWith(".jar")) {
                strClassPath += File.pathSeparator + jarLib.getPath();
            }
        }
        return strClassPath;
    }

    /*public static String getJavaBootClassPath() {
        return jarAndroid.getPath();
    }*/

    /*  @Override
     public String toString() {
     return MoreObjects.toStringHelper(this)
     .toString();
     }*/

    public File getDirs(File file, String find) {
        File[] listFiles = file.listFiles(/*new FileFilter() {
		 @Override
		 public boolean accept(File file) {
		 if (file.isDirectory()) {
		 return true;
		 }
		 /*for (String matches : projectInfo.getReplaces()) {
		 if (Pattern.matches(matches, file.getAbsolutePath())) {
		 return true;
		 }
		 }
		 return false;
		 }


		 }*/);
        if (listFiles != null) {
            for (File file2 : listFiles) {
                if (file2.getName().contains(find)) {
                    return file2;
                } else {
                    if (file2.isDirectory()) {
                        getDirs(file2, find);

                    }
                }

            }
        }
        return null;
    }

}


