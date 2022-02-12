package com.mcal.editor.lexer;

import static com.mcal.editor.lexer.SmaliLexer.ACCESS_SPEC;
import static com.mcal.editor.lexer.SmaliLexer.ANNOTATION_DIRECTIVE;
import static com.mcal.editor.lexer.SmaliLexer.ANNOTATION_VISIBILITY;
import static com.mcal.editor.lexer.SmaliLexer.ARRAY_DATA_DIRECTIVE;
import static com.mcal.editor.lexer.SmaliLexer.ARRAY_DESCRIPTOR;
import static com.mcal.editor.lexer.SmaliLexer.ARROW;
import static com.mcal.editor.lexer.SmaliLexer.AT;
import static com.mcal.editor.lexer.SmaliLexer.BOOL_LITERAL;
import static com.mcal.editor.lexer.SmaliLexer.BYTE_LITERAL;
import static com.mcal.editor.lexer.SmaliLexer.CATCHALL_DIRECTIVE;
import static com.mcal.editor.lexer.SmaliLexer.CATCH_DIRECTIVE;
import static com.mcal.editor.lexer.SmaliLexer.CHAR_LITERAL;
import static com.mcal.editor.lexer.SmaliLexer.CLASS_DESCRIPTOR;
import static com.mcal.editor.lexer.SmaliLexer.CLASS_DIRECTIVE;
import static com.mcal.editor.lexer.SmaliLexer.CLOSE_BRACE;
import static com.mcal.editor.lexer.SmaliLexer.CLOSE_PAREN;
import static com.mcal.editor.lexer.SmaliLexer.COLON;
import static com.mcal.editor.lexer.SmaliLexer.COMMA;
import static com.mcal.editor.lexer.SmaliLexer.DOTDOT;
import static com.mcal.editor.lexer.SmaliLexer.DOUBLE_LITERAL;
import static com.mcal.editor.lexer.SmaliLexer.DOUBLE_LITERAL_OR_ID;
import static com.mcal.editor.lexer.SmaliLexer.END_ANNOTATION_DIRECTIVE;
import static com.mcal.editor.lexer.SmaliLexer.END_ARRAY_DATA_DIRECTIVE;
import static com.mcal.editor.lexer.SmaliLexer.END_FIELD_DIRECTIVE;
import static com.mcal.editor.lexer.SmaliLexer.END_LOCAL_DIRECTIVE;
import static com.mcal.editor.lexer.SmaliLexer.END_METHOD_DIRECTIVE;
import static com.mcal.editor.lexer.SmaliLexer.END_PACKED_SWITCH_DIRECTIVE;
import static com.mcal.editor.lexer.SmaliLexer.END_PARAMETER_DIRECTIVE;
import static com.mcal.editor.lexer.SmaliLexer.END_SPARSE_SWITCH_DIRECTIVE;
import static com.mcal.editor.lexer.SmaliLexer.END_SUBANNOTATION_DIRECTIVE;
import static com.mcal.editor.lexer.SmaliLexer.ENUM_DIRECTIVE;
import static com.mcal.editor.lexer.SmaliLexer.EOF;
import static com.mcal.editor.lexer.SmaliLexer.EPILOGUE_DIRECTIVE;
import static com.mcal.editor.lexer.SmaliLexer.EQUAL;
import static com.mcal.editor.lexer.SmaliLexer.FIELD_DIRECTIVE;
import static com.mcal.editor.lexer.SmaliLexer.FLOAT_LITERAL;
import static com.mcal.editor.lexer.SmaliLexer.FLOAT_LITERAL_OR_ID;
import static com.mcal.editor.lexer.SmaliLexer.IMPLEMENTS_DIRECTIVE;
import static com.mcal.editor.lexer.SmaliLexer.INSTRUCTION_FORMAT10t;
import static com.mcal.editor.lexer.SmaliLexer.INSTRUCTION_FORMAT35c_CALL_SITE;
import static com.mcal.editor.lexer.SmaliLexer.INSTRUCTION_FORMAT3rc_CALL_SITE;
import static com.mcal.editor.lexer.SmaliLexer.INSTRUCTION_FORMAT51l;
import static com.mcal.editor.lexer.SmaliLexer.INVALID_TOKEN;
import static com.mcal.editor.lexer.SmaliLexer.LINE_COMMENT;
import static com.mcal.editor.lexer.SmaliLexer.LINE_DIRECTIVE;
import static com.mcal.editor.lexer.SmaliLexer.LOCALS_DIRECTIVE;
import static com.mcal.editor.lexer.SmaliLexer.LOCAL_DIRECTIVE;
import static com.mcal.editor.lexer.SmaliLexer.LONG_LITERAL;
import static com.mcal.editor.lexer.SmaliLexer.METHOD_DIRECTIVE;
import static com.mcal.editor.lexer.SmaliLexer.NEGATIVE_INTEGER_LITERAL;
import static com.mcal.editor.lexer.SmaliLexer.NULL_LITERAL;
import static com.mcal.editor.lexer.SmaliLexer.OPEN_BRACE;
import static com.mcal.editor.lexer.SmaliLexer.OPEN_PAREN;
import static com.mcal.editor.lexer.SmaliLexer.PACKED_SWITCH_DIRECTIVE;
import static com.mcal.editor.lexer.SmaliLexer.PARAMETER_DIRECTIVE;
import static com.mcal.editor.lexer.SmaliLexer.POSITIVE_INTEGER_LITERAL;
import static com.mcal.editor.lexer.SmaliLexer.PRIMITIVE_LIST;
import static com.mcal.editor.lexer.SmaliLexer.PRIMITIVE_TYPE;
import static com.mcal.editor.lexer.SmaliLexer.PROLOGUE_DIRECTIVE;
import static com.mcal.editor.lexer.SmaliLexer.REGISTER;
import static com.mcal.editor.lexer.SmaliLexer.REGISTERS_DIRECTIVE;
import static com.mcal.editor.lexer.SmaliLexer.RESTART_LOCAL_DIRECTIVE;
import static com.mcal.editor.lexer.SmaliLexer.SHORT_LITERAL;
import static com.mcal.editor.lexer.SmaliLexer.SIMPLE_NAME;
import static com.mcal.editor.lexer.SmaliLexer.SOURCE_DIRECTIVE;
import static com.mcal.editor.lexer.SmaliLexer.SPARSE_SWITCH_DIRECTIVE;
import static com.mcal.editor.lexer.SmaliLexer.STRING_LITERAL;
import static com.mcal.editor.lexer.SmaliLexer.SUBANNOTATION_DIRECTIVE;
import static com.mcal.editor.lexer.SmaliLexer.SUPER_DIRECTIVE;
import static com.mcal.editor.lexer.SmaliLexer.TYPE_LIST;
import static com.mcal.editor.lexer.SmaliLexer.VOID_TYPE;
import static com.mcal.editor.lexer.SmaliLexer.WHITE_SPACE;

import com.mcal.editor.common.ColorScheme;
import com.mcal.editor.common.IndentStringBuilder;
import com.mcal.editor.common.Language;
import com.mcal.editor.common.Pair;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.jf.dexlib2.Opcode;
import org.jf.dexlib2.Opcodes;

import java.util.ArrayList;
import java.util.List;

public class SmaliLexTask extends Antlr4LexTask<SmaliLexer> {
    private final SmaliParser parser;
    private final CommonTokenStream tks;
    private final SmaliLexer lexer;
    private SmaliParser.SmaliContext smali;
    private boolean hasMethodHandle = false;

    public SmaliLexTask() {
        super(new LanguageSmali());
        parser = new SmaliParser(null);
        lexer = new SmaliLexer(null);
        tks = new CommonTokenStream(lexer);
        smali = null;
    }

    @Override
    protected ParseTree getTree() {
        return smali;
    }

    @Override
    protected boolean canAnalysis() {
        return true;
    }

    @Override
    protected void parse(ANTLRInputStream i) {
        lexer.setInputStream(i);
        parse(lexer);
    }

    @Override
    protected void parse(SmaliLexer lexer) {
        tks.setTokenSource(lexer);
        parser.setTokenStream(tks);
        SmaliParser.SmaliContext ctx = parser.smali();
        smali = ctx;
    }

    public Opcodes getCodes() {
        return Opcodes.forApi(hasMethodHandle ? 26 : 14);
    }

    @Override
    protected SmaliLexer generateLexer() {
        return new SmaliLexer(null);
    }

    @Override
    public void tokenize(List<Pair<ColorScheme.Colorable>> _tokens, SmaliLexer lexer) {
        boolean isLabel = false;
        boolean hasMethodHandle = false;
        while (!abort) {
            Token token = lexer.nextToken();
            int type = token.getType();
            if (type == EOF)
                break;
            ColorScheme.Colorable type1;
            switch (type) {
                case ANNOTATION_DIRECTIVE:
                case ARRAY_DATA_DIRECTIVE:
                case CATCHALL_DIRECTIVE:
                case CATCH_DIRECTIVE:
                case CLASS_DIRECTIVE:
                case END_ANNOTATION_DIRECTIVE:
                case END_ARRAY_DATA_DIRECTIVE:
                case END_FIELD_DIRECTIVE:
                case END_LOCAL_DIRECTIVE:
                case END_PARAMETER_DIRECTIVE:
                case END_METHOD_DIRECTIVE:
                case END_PACKED_SWITCH_DIRECTIVE:
                case END_SPARSE_SWITCH_DIRECTIVE:
                case END_SUBANNOTATION_DIRECTIVE:
                case ENUM_DIRECTIVE:
                case EPILOGUE_DIRECTIVE:
                case FIELD_DIRECTIVE:
                case IMPLEMENTS_DIRECTIVE:
                case LINE_DIRECTIVE:
                case LOCAL_DIRECTIVE:
                case LOCALS_DIRECTIVE:
                case METHOD_DIRECTIVE:
                case PACKED_SWITCH_DIRECTIVE:
                case PARAMETER_DIRECTIVE:
                case PROLOGUE_DIRECTIVE:
                case REGISTERS_DIRECTIVE:
                case RESTART_LOCAL_DIRECTIVE:
                case SOURCE_DIRECTIVE:
                case SPARSE_SWITCH_DIRECTIVE:
                case SUBANNOTATION_DIRECTIVE:
                case SUPER_DIRECTIVE:
                    type1 = ColorScheme.Colorable.PACKAGE;
                    break;
                case ACCESS_SPEC:
                case ANNOTATION_VISIBILITY:
                    type1 = ColorScheme.Colorable.KEYWORD;
                    break;
                case CLASS_DESCRIPTOR:
                case VOID_TYPE:
                case PRIMITIVE_TYPE:
                case PRIMITIVE_LIST:
                case TYPE_LIST:
                case ARRAY_DESCRIPTOR:
                    type1 = ColorScheme.Colorable.TYPE;
                    break;
                case LINE_COMMENT:
                    type1 = ColorScheme.Colorable.COMMENT;
                    break;
                case INSTRUCTION_FORMAT3rc_CALL_SITE:
                case INSTRUCTION_FORMAT35c_CALL_SITE:
                    hasMethodHandle = true;
                    type1 = ColorScheme.Colorable.KEYWORD;
                    break;
                case INVALID_TOKEN:
                    type1 = ColorScheme.Colorable.ERROR;
                    break;
                case BOOL_LITERAL:
                case BYTE_LITERAL:
                case CHAR_LITERAL:
                case DOUBLE_LITERAL:
                case DOUBLE_LITERAL_OR_ID:
                case FLOAT_LITERAL_OR_ID:
                case FLOAT_LITERAL:
                case LONG_LITERAL:
                case NULL_LITERAL:
                case POSITIVE_INTEGER_LITERAL:
                case NEGATIVE_INTEGER_LITERAL:
                case SHORT_LITERAL:
                case STRING_LITERAL:
                    type1 = ColorScheme.Colorable.LITERAL;
                    break;
                case ARROW:
                case AT:
                case COMMA:
                case DOTDOT:
                case EQUAL:
                case REGISTER:
                    type1 = ColorScheme.Colorable.OPERATOR;
                    break;
                case OPEN_BRACE:
                case OPEN_PAREN:
                case CLOSE_BRACE:
                case CLOSE_PAREN:
                    type1 = ColorScheme.Colorable.SEPARATOR;
                    break;
                case COLON:
                    isLabel = true;
                    type1 = ColorScheme.Colorable.OPERATOR;
                    break;
                case SIMPLE_NAME:
                    if (isLabel)
                        type1 = ColorScheme.Colorable.OPERATOR;
                    else
                        type1 = ColorScheme.Colorable.NAME;
                    isLabel = false;
                    break;
                case WHITE_SPACE:
                    isLabel = false;
                default:
                    if (type >= INSTRUCTION_FORMAT10t && type <= INSTRUCTION_FORMAT51l)
                        type1 = ColorScheme.Colorable.KEYWORD;
                    else
                        type1 = ColorScheme.Colorable.NAME;
                    break;
            }
            Pair<ColorScheme.Colorable> pair = new Pair<ColorScheme.Colorable>(
                    token.getStopIndex(), type1);
            _tokens.add(pair);
        }
        this.hasMethodHandle = hasMethodHandle;
    }

    @Override
    public boolean canFormat() {
        return true;
    }

    @Override
    public int format(IndentStringBuilder sb, SmaliLexer lexer, int width, int curPos) {
        int newPos = -1;
        int lastDirectiveType = 0;
        int start = 0;
        while (true) {
            Token token = lexer.nextToken();
            int type = token.getType();
            if (type == EOF)
                break;
            String text = token.getText();
            switch (type) {
                case ANNOTATION_DIRECTIVE:
                    if (lastDirectiveType == FIELD_DIRECTIVE ||
                            lastDirectiveType == LOCAL_DIRECTIVE ||
                            lastDirectiveType == PARAMETER_DIRECTIVE)
                        sb.indent(width);
                    lastDirectiveType = 0;
                    sb.append(text);
                    sb.indent(width);
                    break;
                case SUBANNOTATION_DIRECTIVE:
                case METHOD_DIRECTIVE:
                case PACKED_SWITCH_DIRECTIVE:
                case ARRAY_DATA_DIRECTIVE:
                case SPARSE_SWITCH_DIRECTIVE:
                case OPEN_BRACE:
                    sb.append(text);
                    sb.indent(width);
                    break;
                case FIELD_DIRECTIVE:
                case LOCAL_DIRECTIVE:
                case PARAMETER_DIRECTIVE:
                    sb.append(text);
                    lastDirectiveType = type;
                    break;
                case END_ANNOTATION_DIRECTIVE:
                case END_SUBANNOTATION_DIRECTIVE:
                case END_FIELD_DIRECTIVE:
                case END_METHOD_DIRECTIVE:
                case END_PACKED_SWITCH_DIRECTIVE:
                case END_ARRAY_DATA_DIRECTIVE:
                case END_SPARSE_SWITCH_DIRECTIVE:
                case END_LOCAL_DIRECTIVE:
                case END_PARAMETER_DIRECTIVE:
                case CLOSE_BRACE:
                    sb.deindent(width);
                    sb.append(text);
                    break;
                case STRING_LITERAL:
                    SmaliFormater.processStringOrChar(sb, text, true);
                    break;
                case CHAR_LITERAL:
                    SmaliFormater.processStringOrChar(sb, text, false);
                    break;
                case WHITE_SPACE:
                    SmaliFormater.processWhiteSpace(sb, text);
                    break;
                default:
                    sb.append(text);
            }

            if (type != FIELD_DIRECTIVE ||
                    type != LOCAL_DIRECTIVE ||
                    type != PARAMETER_DIRECTIVE)
                lastDirectiveType = 0;
            int end = token.getStopIndex() + 1;
            newPos = Antlr4LexTask.compute(sb.length(), start, end, curPos, newPos);
            start = end;
        }
        if (newPos == -1)
            newPos = sb.length() - 1;
        return newPos;
    }

    private static class LanguageSmali extends Language {
        LanguageSmali() {
            Opcode[] opcodes = Opcode.values();
            int size = opcodes.length;
            String[] keywords = new String[size];
            for (int i = 0; i < size; i++)
                keywords[i] = opcodes[i].name;
            setKeywords(keywords);
        }

        @Override
        public CharSequence complete(ArrayList<String> buf, CharSequence constraint) {
            if (constraint.charAt(0) != 'L')
                return super.complete(buf, constraint);
            String word = constraint.toString();
            int i = word.indexOf(';');
            if (i == -1) {
                word = word.toLowerCase();
                if (word.startsWith("[")) {
                    i = word.lastIndexOf('[');

                }
                // TODO
				/*for (String type:Packages.getTypes()) {
					if (type.toLowerCase().startsWith(word))
						buf.add(type);
				}*/
                return word;
            }
            String word1 = word.substring(i + 1);
            if (word1.startsWith("->")) {
                word1 = word1.substring(2).toLowerCase();
                String type = word.substring(0, i + 1);
                for (String m : Packages.getMembers(type)) {
                    if (m.toLowerCase().startsWith(word1))
                        buf.add(m);
                }
            }
            return word1;
        }
    }
}