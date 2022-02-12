package com.mcal.editor.lexer;

import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;

public abstract class JavaScriptBaseParser extends Parser {
    public JavaScriptBaseParser(TokenStream input) {
        super(input);
    }

    protected boolean p(String str) {
        return prev(str);
    }

    protected boolean prev(String str) {
        return this._input.LT(-1).getText().equals(str);
    }

    protected boolean n(String str) {
        return next(str);
    }

    protected boolean next(String str) {
        return this._input.LT(1).getText().equals(str);
    }

    protected boolean notLineTerminator() {
        return !here(107);
    }

    protected boolean notOpenBraceAndNotFunction() {
        int nextTokenType = this._input.LT(1).getType();
        return (nextTokenType == 8 || nextTokenType == 78) ? false : true;
    }

    protected boolean closeBrace() {
        return this._input.LT(1).getType() == 9;
    }

    private boolean here(int type) {
        Token ahead = this._input.get(getCurrentToken().getTokenIndex() - 1);
        return ahead.getChannel() == 1 && ahead.getType() == type;
    }

    protected boolean lineTerminatorAhead() {
        Token ahead = this._input.get(getCurrentToken().getTokenIndex() - 1);
        if (ahead.getChannel() != 1) {
            return false;
        }
        if (ahead.getType() == 107) {
            return true;
        }
        if (ahead.getType() == 106) {
            ahead = this._input.get(getCurrentToken().getTokenIndex() - 2);
        }
        String text = ahead.getText();
        int type = ahead.getType();
        return (type == 1 && (text.contains("\r") || text.contains("\n"))) || type == 107;
    }
}