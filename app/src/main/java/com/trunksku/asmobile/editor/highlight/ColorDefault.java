package com.trunksku.asmobile.editor.highlight;


import android.graphics.Color;
import android.support.annotation.Keep;

@Keep
public class ColorDefault extends ColorImpl {


    public final Colors BACKGROUND;
    public final Colors COMMENT;
    public final Colors IDENTIFIER;
    public final Colors KEYWORD;
    public final Colors LINE_BACKGROUND;
    public final Colors LINE_NUMBER;
    public final Colors LITERAL;
    public final Colors NAMESPACE_IDENTIFIER;
    public final Colors OPERATOR;
    public final Colors PLAIN;
    public final Colors SELECTED_BACKGROUND;
    public final Colors SEPARATOR;
    public final Colors TYPE_IDENTIFIER;
    private Colors[] colors;

    public ColorDefault() {
        this.BACKGROUND = new Colors("background", "Background", "#00FFFFFF", "#FF181818");
        this.PLAIN = new Colors("Plain", "Plain", "#FF000000", "#FFFFFFFF");
        this.KEYWORD = new Colors("Keyword", "Keyword", "#FF0096FF", "#FFFF5252");
        this.IDENTIFIER = new Colors("Identifier", "Identifier", "#FF000000", "#FFFFD740");
        this.LITERAL = new Colors("Literal", "Literal", "#FFE91E63", "#FF8BC34A");
        this.OPERATOR = new Colors("Operator", "Operator", "#FF03A9F4", "#FFEC407A");
        this.TYPE_IDENTIFIER = new Colors("Type Identifier", "Type Identifier", "#FF0096FF", "#FF23BBFF");
        this.NAMESPACE_IDENTIFIER = new Colors("Namespace/Package Identifier", "Namespace/Package Identifier", "#FF818181", "#FFFFD740");
        this.SEPARATOR = new Colors("Separator/Punctuator", "Separator/Punctuator", "#FF0096FF", "#FF4DD0E1");
        this.COMMENT = new Colors("Comment", "Comment", "#FF818181", "#FF00C853");
        this.LINE_NUMBER = new Colors("line number", "Line number", "#FFBDBDBD", "#FF23BBFF");
        this.LINE_BACKGROUND = new Colors("line background", "Selested line background", "#10000000", "#15FFFFFF");
        this.SELECTED_BACKGROUND = new Colors("selected background", "Selected text background", "#FF2ABFFF", "#FF2E94C2");
        this.colors = new Colors[]{this.BACKGROUND, this.PLAIN, this.KEYWORD, this.IDENTIFIER, this.LITERAL, this.OPERATOR, this.TYPE_IDENTIFIER, this.NAMESPACE_IDENTIFIER, this.SEPARATOR, this.COMMENT, this.LINE_NUMBER, this.LINE_BACKGROUND, this.SELECTED_BACKGROUND};
    }

    @Override
    public Colors[] values() {
        return this.colors;
    }

    @Override
    public int getColorKeyword() {
        return Color.parseColor(KEYWORD.darkColor);
    }

    @Override
    public int getColorParams() {
        return Color.parseColor(KEYWORD.darkColor);
    }

    @Override
    public int getColorEnding() {
        return Color.parseColor(KEYWORD.darkColor);
    }

    @Override
    public int getColorFunctions() {
        return Color.parseColor(NAMESPACE_IDENTIFIER.darkColor);
    }

    @Override
    public int getColorBuiltin() {
        return Color.parseColor(KEYWORD.darkColor);
    }

    @Override
    public int getColorComment() {
        return Color.parseColor(COMMENT.darkColor);
    }

    @Override
    public int getColorStrings() {
        return Color.parseColor(KEYWORD.darkColor);
    }

    @Override
    public int getColorLineShadow() {
        return Color.parseColor(LINE_NUMBER.lightColor);
    }

    @Override
    public int getColorLineHighlight() {
        return Color.parseColor(SELECTED_BACKGROUND.darkColor);
    }

    @Override
    public int getColorNumber() {
        return Color.parseColor(LINE_NUMBER.lightColor);
    }

    @Override
    public int getColorBackground() {
        return Color.parseColor(BACKGROUND.darkColor);
    }

    @Override
    public int getColorText() {
        return Color.parseColor(PLAIN.darkColor);
    }
}

