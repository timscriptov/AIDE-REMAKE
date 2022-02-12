package com.mcal.editor.lexer;

import java.util.Stack;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Token;

public abstract class JavaScriptBaseLexer extends Lexer {
    private Stack<Boolean> scopeStrictModes = new Stack<>();
    private Token lastToken = null;
    private boolean useStrictDefault = false;
    private boolean useStrictCurrent = false;

    public JavaScriptBaseLexer(CharStream input) {
        super(input);
    }

    public boolean getStrictDefault() {
        return this.useStrictDefault;
    }

    public void setUseStrictDefault(boolean value) {
        this.useStrictDefault = value;
        this.useStrictCurrent = value;
    }

    public boolean IsSrictMode() {
        return this.useStrictCurrent;
    }

    public Token nextToken() {
        Token next = nextToken();
        if (next.getChannel() == 0) {
            this.lastToken = next;
        }
        return next;
    }

    protected void ProcessOpenBrace() {
        this.useStrictCurrent = (this.scopeStrictModes.size() <= 0 || !this.scopeStrictModes.peek().booleanValue()) ? this.useStrictDefault : true;
        this.scopeStrictModes.push(Boolean.valueOf(this.useStrictCurrent));
    }

    protected void ProcessCloseBrace() {
        this.useStrictCurrent = this.scopeStrictModes.size() > 0 ? this.scopeStrictModes.pop().booleanValue() : this.useStrictDefault;
    }

    protected void ProcessStringLiteral() {
        if (this.lastToken == null || this.lastToken.getType() == 8) {
            String text = getText();
            if (text.equals("\"use strict\"") || text.equals("'use strict'")) {
                if (this.scopeStrictModes.size() > 0) {
                    this.scopeStrictModes.pop();
                }
                this.useStrictCurrent = true;
                this.scopeStrictModes.push(Boolean.valueOf(this.useStrictCurrent));
            }
        }
    }

    protected boolean IsRegexPossible() {
        if (this.lastToken == null) {
            return true;
        }
        switch (this.lastToken.getType()) {
            case 5:
            case 7:
            case 17:
            case 18:
            case 54:
            case 55:
            case 56:
            case 57:
            case 58:
            case 79:
            case 103:
            case 104:
                return false;
            default:
                return true;
        }
    }
}