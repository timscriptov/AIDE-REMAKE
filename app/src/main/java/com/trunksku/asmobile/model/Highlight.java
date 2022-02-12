package com.trunksku.asmobile.model;


public class Highlight {
    private String defaultColor;
    private String title;
    private String type;

    public Highlight(String str, String str2, String str3) {
        this.title = str;
        this.type = str2;
        this.defaultColor = str3;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String str) {
        this.type = str;
    }

    public String getColor(boolean z) {
        return getDefaultColor();
        // return HighlightUtils.getHSp().getString(HighlightUtils.getSpKey(getType(), z), getDefaultColor()).toUpperCase(Locale.ENGLISH);
    }

    public String getDefaultColor() {
        return this.defaultColor;
    }

    public void setDefaultColor(String str) {
        this.defaultColor = str;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String str) {
        this.title = str;
    }

    public String getSpKey(boolean z) {
        return "";
        // return HighlightUtils.getSpKey(getType(), z);
    }
}

