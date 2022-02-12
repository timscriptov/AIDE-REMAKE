package com.mcal.editor.lexer;

import static com.mcal.editor.lexer.XMLLexer.CLOSE;
import static com.mcal.editor.lexer.XMLLexer.COMMENT;
import static com.mcal.editor.lexer.XMLLexer.DTD;
import static com.mcal.editor.lexer.XMLLexer.EOF;
import static com.mcal.editor.lexer.XMLLexer.EQUALS;
import static com.mcal.editor.lexer.XMLLexer.Name;
import static com.mcal.editor.lexer.XMLLexer.OPEN;
import static com.mcal.editor.lexer.XMLLexer.SLASH;
import static com.mcal.editor.lexer.XMLLexer.SLASH_CLOSE;
import static com.mcal.editor.lexer.XMLLexer.SPECIAL_CLOSE;
import static com.mcal.editor.lexer.XMLLexer.STRING;
import static com.mcal.editor.lexer.XMLLexer.TEXT;
import static com.mcal.editor.lexer.XMLLexer.XMLDeclOpen;

import com.mcal.editor.common.ColorScheme;
import com.mcal.editor.common.Pair;

import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;

import java.util.List;

public class HtmlLexTask extends XmlLexTask {
    public HtmlLexTask() {
        super();
    }

    @Override
    protected void tokenize(List<Pair<ColorScheme.Colorable>> _tokens, XMLLexer lexer) {
        int lastType = 0;
        String lastTag = null;
        while (!abort) {
            int start = lexer.getCharIndex();
            Token token = lexer.nextToken();
            int tokenType = token.getType();
            if (tokenType == EOF)
                break;
            ColorScheme.Colorable type;
            switch (tokenType) {
                case XMLDeclOpen:
                    parse(lexer, SPECIAL_CLOSE);
                case DTD:
                    type = ColorScheme.Colorable.PACKAGE;
                    break;
                case COMMENT:
                    type = ColorScheme.Colorable.COMMENT;
                    break;
                case OPEN:
                case CLOSE:
                case SLASH_CLOSE:
                case SLASH:
                    type = ColorScheme.Colorable.SEPARATOR;
                    break;
                case Name:
                    if (lastType == OPEN) {
                        type = ColorScheme.Colorable.KEYWORD;
                        lastTag = token.getText();
                    } else if (lastType == SLASH) {
                        type = ColorScheme.Colorable.KEYWORD;
                        lastTag = null;
                    } else
                        type = ColorScheme.Colorable.TYPE;
                    break;
                case STRING:
                    type = ColorScheme.Colorable.LITERAL;
                    break;
                case EQUALS:
                    type = ColorScheme.Colorable.OPERATOR;
                    break;
                case TEXT:

                    type = ColorScheme.Colorable.NAME;
                    break;
                default:
                    type = ColorScheme.Colorable.NAME;
                    break;
            }
            lastType = tokenType;
            Pair<ColorScheme.Colorable> pair = new Pair<ColorScheme.Colorable>(lexer.getCharIndex() - start, type);
            _tokens.add(pair);
        }
        lexer.reset();
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        parser.setTokenStream(tokens);
        document = parser.document();
    }
}
