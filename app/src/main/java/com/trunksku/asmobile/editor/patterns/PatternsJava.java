package com.trunksku.asmobile.editor.patterns;

//import com.google.common.base.Joiner;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;

public class PatternsJava {
    public static final int colorImport = 0xffa0a0a0;
    public static final int colorKeywords = 0xff303F9F;
    public static final int colorAnnotations = 0xFF808000;
    public static final int colorComment = 0xffa0a0a0;
    public static final int colorStrings = 0xff43A047;

    public static final Pattern COMMENTS_SINGLE_LINE = Pattern.compile("(//)(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$).*");
    public static final Pattern COMMENTS_MULTI_LINE = Pattern.compile("(//)(?:/\\*)(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)(?:.|\\n)*?\\*/");
    public static final Pattern ANNOTATIONS = Pattern.compile("@ *(?:[a-zA-Z_0-9]+(?=\\s?))");
    public static final Pattern IMPORT_NOT_IMPORTANT = Pattern.compile("(?<=import ).*\\.(?=.*;)");
    public static final ArrayList<String> JAVA_KEYWORDS = new ArrayList<>(Arrays.asList("assert", "abstract", "boolean", "byte", "char", "class", "do",
            "double", "final", "float", "for", "if", "int", "long", "new",
            "public", "private", "protected", "package", "return", "static",
            "short", "super", "switch", "else", "volatile", "synchronized", "strictfp",
            "goto", "continue", "break", "transient", "void", "try", "catch",
            "finally", "while", "case", "default", "const", "enum", "extends",
            "implements", "import", "instanceof", "interface", "native",
            "this", "throw", "throws", "true", "false", "null"));

    private final Pattern patternKeywords = Pattern.compile("\\b(" + TextUtils.join("|", JAVA_KEYWORDS)/* + Joiner.on('|').join(JAVA_KEYWORDS) */ + ")\\b");
    private final Pattern patternNumbers = Pattern.compile("\\b(?:0x)?[0-9]+(?:.[0-9]+f?)?\\b");
    private final Pattern patternBooleans = Pattern.compile("\\b(true|false)\\b");
    private final Pattern patternStrings = Pattern.compile("\".*?\"");

    public Pattern getPatternKeywords() {
        return patternKeywords;
    }

    public Pattern getPatternImports() {
        return IMPORT_NOT_IMPORTANT;
    }

    public Pattern getPatternAnnotations() {
        return ANNOTATIONS;
    }

    public Pattern getPatternComments() {
        return COMMENTS_SINGLE_LINE;
    }

    public Pattern getPatternCommentsOther() {
        return COMMENTS_MULTI_LINE;
    }

    public Pattern getPatternNumbers() {
        return patternNumbers;
    }

    public Pattern getPatternBooleans() {
        return patternBooleans;
    }

    public Pattern getPatternStrings() {
        return patternStrings;
    }
}
