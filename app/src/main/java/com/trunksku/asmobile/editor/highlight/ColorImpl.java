package com.trunksku.asmobile.editor.highlight;

public abstract class ColorImpl {
    public abstract Colors[] values();

    public abstract int getColorKeyword();

    public abstract int getColorParams();

    public abstract int getColorEnding();

    public abstract int getColorFunctions();

    public abstract int getColorBuiltin();

    public abstract int getColorComment();

    public abstract int getColorStrings();

    public abstract int getColorLineShadow();

    public abstract int getColorLineHighlight();

    public abstract int getColorNumber();

    public abstract int getColorBackground();

    public abstract int getColorText();
}
