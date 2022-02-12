package com.trunksku.asmobile.compile.file;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GradleFile {

    public static String parseGradle(String gradle) {

        Pattern p = Pattern.compile("^\\s*(versionName)\\s*([A-Z0-9.'/-]+)");
        Matcher m = p.matcher(gradle);

        if (m.find()) {
            return m.group(2);
            //System.out.println(m.group(2));
        }
        return "error";
    }

}
