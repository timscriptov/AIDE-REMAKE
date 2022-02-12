package com.trunksku.asmobile.utils;

import android.text.Editable;
import android.widget.EditText;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class TextUtils {
    public static final String WORD_SPLITTER = "\\W";
    public static final String IMPORTS_REGEX = "(?:package(.|\\n)*?(?=import)|.+\\{(?:.|\\s)*|import.*\\.|;)";

    public static String replaceLast(String text, String regex, String replacement) {
        return text.replaceFirst("(?s)(.*)" + regex, "$1" + replacement);
    }

    public static String getCurrentWord(EditText editText) {
        return getCurrentWordWithOffset(editText, 0);
    }

    public static String getCurrentWordWithOffset(EditText editText, int offset) {
        return getWordAtPosition(Objects.requireNonNull(editText.getText()).toString(), editText.getSelectionStart() + offset);
    }


    public static String getWordAtPosition(String string, int position) {
        int length = 0;

        for (String currentWord : string.split(WORD_SPLITTER)) {
            length = length + currentWord.length();
            if (length >= position) {
                return currentWord;
            }
            length += 1;
        }

        return "";
    }

    public static void deleteWordAtPosition(Editable editable, int pos) {
        int start = pos;
        int end = pos;
        while (start > 0 && !Character.toString(editable.charAt(start)).matches(WORD_SPLITTER)) {
            start--;
        }
        while (end < editable.length() && !Character.toString(editable.charAt(end)).matches(WORD_SPLITTER)) {
            end++;
        }
        editable.delete(pos == 0 ? 0 : start + 1, end);
    }

    public static String insert(String destination, int position, String toInsert) {
        return destination.substring(0, position) + toInsert + destination.substring(position);
    }

    public static List<String> parseImports(String text) {
        try {
            text = text.replaceAll(IMPORTS_REGEX, "");
            return Arrays.asList(text.split("\n"));
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean hasNotAnyImport(String imports, String currentClass) {
        return !currentClass.contains(imports);
    }

    public static void addImport(EditText edt, String Import) {
        Editable editable = edt.getText();
        editable.insert(editable.toString().indexOf(';') + 1, "\n\nimport " + Import + ";");
    }
}
