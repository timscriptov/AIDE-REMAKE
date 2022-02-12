package com.mcal.editor.common;



import java.util.List;

public class NonProgLexTask extends LexTask {

    public static NonProgLexTask instance = new NonProgLexTask();

    private NonProgLexTask() {
        super(LanguageNonProg.getInstance());
    }

    @Override
    protected void tokenize(List<Pair<ColorScheme.Colorable>> _tokens, String text) {
        _tokens.add(new Pair<>(0, ColorScheme.Colorable.NAME));
    }
}