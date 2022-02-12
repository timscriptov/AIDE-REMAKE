package com.trunksku.asmobile.editor.completion.util;

import android.widget.MultiAutoCompleteTextView;

public class SymbolsTokenizer implements MultiAutoCompleteTextView.Tokenizer {

    private static final String TOKEN = "!@#$%^&*()_+-={}|[]:;'<>/<.? \r\n\t";

    @Override
    public int findTokenStart(CharSequence text, int cursor) {
        int i = cursor;
        while (i > 0 && !TOKEN.contains(Character.toString(text.charAt(i - 1)))) {
            i--;
        }
        while (i < cursor && text.charAt(i) == ' ') {
            i++;
        }
        return i;
    }

    @Override
    public int findTokenEnd(CharSequence text, int cursor) {
        int i = cursor;
        int len = text.length();
        while (i < len) {
            if (TOKEN.contains(Character.toString(text.charAt(i - 1)))) {
                return i;
            } else {
                i++;
            }
        }
        return len;
    }

    @Override
    public CharSequence terminateToken(CharSequence text) {
        int i = text.length();
        while (i > 0 && text.charAt(i - 1) == ' ') {
            i--;
        }
        return text;
    }
}

