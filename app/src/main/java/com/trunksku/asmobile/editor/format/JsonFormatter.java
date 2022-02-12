package com.trunksku.asmobile.editor.format;

import org.json.JSONObject;

public class JsonFormatter {

    public CharSequence format(CharSequence input) {
        try {
            JSONObject jsonObject = new JSONObject(input.toString());
            return jsonObject.toString(1);
        } catch (Exception e) {
        }
        return null;
    }
}

