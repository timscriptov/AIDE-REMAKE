package com.mcal.editor.lexer;

import static com.mcal.editor.lexer.XMLLexer.CLOSE;
import static com.mcal.editor.lexer.XMLLexer.COMMENT;
import static com.mcal.editor.lexer.XMLLexer.DTD;
import static com.mcal.editor.lexer.XMLLexer.EOF;
import static com.mcal.editor.lexer.XMLLexer.EQUALS;
import static com.mcal.editor.lexer.XMLLexer.Name;
import static com.mcal.editor.lexer.XMLLexer.OPEN;
import static com.mcal.editor.lexer.XMLLexer.SEA_WS;
import static com.mcal.editor.lexer.XMLLexer.SLASH;
import static com.mcal.editor.lexer.XMLLexer.SLASH_CLOSE;
import static com.mcal.editor.lexer.XMLLexer.SPECIAL_CLOSE;
import static com.mcal.editor.lexer.XMLLexer.STRING;
import static com.mcal.editor.lexer.XMLLexer.XMLDeclOpen;

import android.util.Log;

import com.mcal.editor.common.ColorScheme;
import com.mcal.editor.common.IndentStringBuilder;
import com.mcal.editor.common.Language;
import com.mcal.editor.common.Pair;
import com.mcal.editor.lexer.XMLParser.AttributeContext;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.List;

public class XmlLexTask extends Antlr4LexTask<XMLLexer> {
    protected final XMLParser parser;
    private final XMLLexer lexer;
    protected XMLParser.DocumentContext document;

    public XmlLexTask() {
        super(XMLLexer.VOCABULARY);
        parser = new XMLParser(null);
        lexer = new XMLLexer(null);
        document = null;
    }

    @Override
    protected ParseTree getTree() {
        return document;
    }

    @Override
    protected XMLLexer generateLexer() {
        return new XMLLexer(null);
    }

    @Override
    protected void tokenize(List<Pair<ColorScheme.Colorable>> _tokens, XMLLexer lexer) {
        int lastType = 0;
        document = null;
        while (!abort) {
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
                    if (lastType == OPEN || lastType == SLASH)
                        type = ColorScheme.Colorable.KEYWORD;
                    else
                        type = ColorScheme.Colorable.TYPE;
                    break;
                case STRING:
                    type = ColorScheme.Colorable.LITERAL;
                    break;
                case EQUALS:
                    type = ColorScheme.Colorable.OPERATOR;
                    break;
                default:
                    type = ColorScheme.Colorable.NAME;
                    break;
            }
            lastType = tokenType;
            Pair<ColorScheme.Colorable> pair = new Pair<ColorScheme.Colorable>(token.getStopIndex(), type);
            _tokens.add(pair);
        }
    }

    @Override
    protected void parse(XMLLexer lexer) {
        Log.i("APKTOOL PARSE", "parse xml");
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        parser.setTokenStream(tokens);
        document = parser.document();
    }

    @Override
    public boolean canFormat() {
        return true;
    }

    @Override
    protected void parse(ANTLRInputStream i) {
        lexer.setInputStream(i);
        parse(lexer);
    }

    protected void parse(XMLLexer lexer, int endType) {
        while (true) {
            if (lexer.nextToken().getType() == endType)
                break;
        }
    }

    @Override
    protected int format(IndentStringBuilder sb, XMLLexer lexer, int width, int curPos) {
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        parser.setTokenStream(tokens);
        XMLFormatter f = new XMLFormatter(sb, width, curPos);
        parser.document().accept(f);
        return f.newPos;
    }

    private static class LanguageXml extends Language {
        LanguageXml() {
            setKeywords(new String[0]);
        }
    }

    private static class XMLFormatter extends XMLParserBaseVisitor<Void> {
        private final IndentStringBuilder sb;
        private final int width;
        private final int curPos;
        int newPos;
        private boolean shouldNewLine = false;

        public XMLFormatter(IndentStringBuilder sb, int width, int curPos) {
            this.sb = sb;
            this.width = width;
            this.curPos = curPos;
            newPos = curPos;
        }

        @Override
        public Void visitElement(XMLParser.ElementContext ctx) {
            sb.append('<');
            sb.indent(width);
            ctx.Name(0).accept(this);
            List<AttributeContext> attrs = ctx.attribute();
            shouldNewLine = attrs.size() > 1;
            for (AttributeContext attr : attrs)
                attr.accept(this);
            XMLParser.ContentContext content = ctx.content();
            if (content == null) {
                sb.append("/>");
                sb.deindent(width);
            } else {
                sb.append('>');
                content.accept(this);
                sb.deindent(width);
                sb.append("</");
                visitTerminal(ctx.Name(1));
                //ctx.Name(1).accept(this);
                sb.append('>');
            }
            return null;
        }

        @Override
        public Void visitAttribute(XMLParser.AttributeContext ctx) {
            sb.append(shouldNewLine ? '\n' : ' ');
            ctx.Name().accept(this);
            sb.append('=');
            ctx.STRING().accept(this);
            return null;
        }

        @Override
        public Void visitTerminal(TerminalNode node) {
            if (node == null)
                return null;
            Token token = node.getSymbol();
            String text = token.getText();
            int type = token.getType();
            switch (type) {
                case SEA_WS:
                    SmaliFormater.processWhiteSpace(sb, text);
                    break;
                case SPECIAL_CLOSE:
                    sb.append(' ');
                    sb.append(text);
                    break;
                default:
                    sb.append(text);
                    break;
            }
            int start = token.getStartIndex();
            int end = token.getStopIndex();
            newPos = compute(sb.length(), start, end, curPos, newPos);
            return null;
        }
    }
}