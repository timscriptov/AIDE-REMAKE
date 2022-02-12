package com.mcal.editor.lexer;

import static com.mcal.editor.lexer.ObjectiveCLexer.ATOMIC;
import static com.mcal.editor.lexer.ObjectiveCLexer.AUTO;
import static com.mcal.editor.lexer.ObjectiveCLexer.AUTORELEASEPOOL;
import static com.mcal.editor.lexer.ObjectiveCLexer.BINARY_LITERAL;
import static com.mcal.editor.lexer.ObjectiveCLexer.BOOL;
import static com.mcal.editor.lexer.ObjectiveCLexer.BOOL_;
import static com.mcal.editor.lexer.ObjectiveCLexer.BREAK;
import static com.mcal.editor.lexer.ObjectiveCLexer.BYCOPY;
import static com.mcal.editor.lexer.ObjectiveCLexer.BYREF;
import static com.mcal.editor.lexer.ObjectiveCLexer.CASE;
import static com.mcal.editor.lexer.ObjectiveCLexer.CATCH;
import static com.mcal.editor.lexer.ObjectiveCLexer.CHAR;
import static com.mcal.editor.lexer.ObjectiveCLexer.CHARACTER_LITERAL;
import static com.mcal.editor.lexer.ObjectiveCLexer.CLASS;
import static com.mcal.editor.lexer.ObjectiveCLexer.COMPLEX;
import static com.mcal.editor.lexer.ObjectiveCLexer.CONST;
import static com.mcal.editor.lexer.ObjectiveCLexer.CONTINUE;
import static com.mcal.editor.lexer.ObjectiveCLexer.Class;
import static com.mcal.editor.lexer.ObjectiveCLexer.DECIMAL_LITERAL;
import static com.mcal.editor.lexer.ObjectiveCLexer.DEFAULT;
import static com.mcal.editor.lexer.ObjectiveCLexer.DO;
import static com.mcal.editor.lexer.ObjectiveCLexer.DOUBLE;
import static com.mcal.editor.lexer.ObjectiveCLexer.DYNAMIC;
import static com.mcal.editor.lexer.ObjectiveCLexer.ELIPSIS;
import static com.mcal.editor.lexer.ObjectiveCLexer.ELSE;
import static com.mcal.editor.lexer.ObjectiveCLexer.ENCODE;
import static com.mcal.editor.lexer.ObjectiveCLexer.END;
import static com.mcal.editor.lexer.ObjectiveCLexer.ENUM;
import static com.mcal.editor.lexer.ObjectiveCLexer.EOF;
import static com.mcal.editor.lexer.ObjectiveCLexer.EXTERN;
import static com.mcal.editor.lexer.ObjectiveCLexer.FALSE;
import static com.mcal.editor.lexer.ObjectiveCLexer.FINALLY;
import static com.mcal.editor.lexer.ObjectiveCLexer.FLOAT;
import static com.mcal.editor.lexer.ObjectiveCLexer.FLOATING_POINT_LITERAL;
import static com.mcal.editor.lexer.ObjectiveCLexer.FOR;
import static com.mcal.editor.lexer.ObjectiveCLexer.GOTO;
import static com.mcal.editor.lexer.ObjectiveCLexer.HEX_LITERAL;
import static com.mcal.editor.lexer.ObjectiveCLexer.ID;
import static com.mcal.editor.lexer.ObjectiveCLexer.IF;
import static com.mcal.editor.lexer.ObjectiveCLexer.IMAGINERY;
import static com.mcal.editor.lexer.ObjectiveCLexer.IMP;
import static com.mcal.editor.lexer.ObjectiveCLexer.IMPLEMENTATION;
import static com.mcal.editor.lexer.ObjectiveCLexer.IMPORT;
import static com.mcal.editor.lexer.ObjectiveCLexer.IN;
import static com.mcal.editor.lexer.ObjectiveCLexer.INLINE;
import static com.mcal.editor.lexer.ObjectiveCLexer.INOUT;
import static com.mcal.editor.lexer.ObjectiveCLexer.INT;
import static com.mcal.editor.lexer.ObjectiveCLexer.INTERFACE;
import static com.mcal.editor.lexer.ObjectiveCLexer.LONG;
import static com.mcal.editor.lexer.ObjectiveCLexer.LP;
import static com.mcal.editor.lexer.ObjectiveCLexer.MULTI_COMMENT;
import static com.mcal.editor.lexer.ObjectiveCLexer.NIL;
import static com.mcal.editor.lexer.ObjectiveCLexer.NO;
import static com.mcal.editor.lexer.ObjectiveCLexer.NONATOMIC;
import static com.mcal.editor.lexer.ObjectiveCLexer.NULL;
import static com.mcal.editor.lexer.ObjectiveCLexer.OCTAL_LITERAL;
import static com.mcal.editor.lexer.ObjectiveCLexer.ONEWAY;
import static com.mcal.editor.lexer.ObjectiveCLexer.OPTIONAL;
import static com.mcal.editor.lexer.ObjectiveCLexer.OUT;
import static com.mcal.editor.lexer.ObjectiveCLexer.PACKAGE;
import static com.mcal.editor.lexer.ObjectiveCLexer.PRIVATE;
import static com.mcal.editor.lexer.ObjectiveCLexer.PROPERTY;
import static com.mcal.editor.lexer.ObjectiveCLexer.PROTECTED;
import static com.mcal.editor.lexer.ObjectiveCLexer.PROTOCOL;
import static com.mcal.editor.lexer.ObjectiveCLexer.PROTOCOL_;
import static com.mcal.editor.lexer.ObjectiveCLexer.PUBLIC;
import static com.mcal.editor.lexer.ObjectiveCLexer.REGISTER;
import static com.mcal.editor.lexer.ObjectiveCLexer.REQUIRED;
import static com.mcal.editor.lexer.ObjectiveCLexer.RESTRICT;
import static com.mcal.editor.lexer.ObjectiveCLexer.RETAIN;
import static com.mcal.editor.lexer.ObjectiveCLexer.RETURN;
import static com.mcal.editor.lexer.ObjectiveCLexer.SEL;
import static com.mcal.editor.lexer.ObjectiveCLexer.SELECTOR;
import static com.mcal.editor.lexer.ObjectiveCLexer.SELF;
import static com.mcal.editor.lexer.ObjectiveCLexer.SHORT;
import static com.mcal.editor.lexer.ObjectiveCLexer.SIGNED;
import static com.mcal.editor.lexer.ObjectiveCLexer.SINGLE_COMMENT;
import static com.mcal.editor.lexer.ObjectiveCLexer.SIZEOF;
import static com.mcal.editor.lexer.ObjectiveCLexer.STATIC;
import static com.mcal.editor.lexer.ObjectiveCLexer.STRING_END;
import static com.mcal.editor.lexer.ObjectiveCLexer.STRING_NEWLINE;
import static com.mcal.editor.lexer.ObjectiveCLexer.STRING_START;
import static com.mcal.editor.lexer.ObjectiveCLexer.STRING_VALUE;
import static com.mcal.editor.lexer.ObjectiveCLexer.STRUCT;
import static com.mcal.editor.lexer.ObjectiveCLexer.SUPER;
import static com.mcal.editor.lexer.ObjectiveCLexer.SWITCH;
import static com.mcal.editor.lexer.ObjectiveCLexer.SYNCHRONIZED;
import static com.mcal.editor.lexer.ObjectiveCLexer.SYNTHESIZE;
import static com.mcal.editor.lexer.ObjectiveCLexer.THROW;
import static com.mcal.editor.lexer.ObjectiveCLexer.TRUE;
import static com.mcal.editor.lexer.ObjectiveCLexer.TRY;
import static com.mcal.editor.lexer.ObjectiveCLexer.TYPEDEF;
import static com.mcal.editor.lexer.ObjectiveCLexer.TYPEOF;
import static com.mcal.editor.lexer.ObjectiveCLexer.UNION;
import static com.mcal.editor.lexer.ObjectiveCLexer.VOID;
import static com.mcal.editor.lexer.ObjectiveCLexer.VOLATILE;
import static com.mcal.editor.lexer.ObjectiveCLexer.WHILE;
import static com.mcal.editor.lexer.ObjectiveCLexer.YES;

import com.mcal.editor.common.ColorScheme;
import com.mcal.editor.common.Pair;

import org.antlr.v4.runtime.Token;

import java.util.List;

public class ObjcLexTask extends Antlr4LexTask<ObjectiveCLexer> {
    public ObjcLexTask() {
        super(ObjectiveCLexer.VOCABULARY);
    }

    @Override
    protected ObjectiveCLexer generateLexer() {
        return new ObjectiveCLexer(null);
    }

    @Override
    protected void tokenize(List<Pair<ColorScheme.Colorable>> _tokens, ObjectiveCLexer lexer) {
        while (!abort) {
            Token token = lexer.nextToken();
            int tokenType = token.getType();
            if (tokenType == EOF)
                break;
            ColorScheme.Colorable type;
            switch (tokenType) {
                case AUTO:
                case BREAK:
                case CASE:
                case CONST:
                case CONTINUE:
                case DEFAULT:
                case DO:
                case ELSE:
                case ENUM:
                case EXTERN:
                case FOR:
                case GOTO:
                case IF:
                case INLINE:
                case REGISTER:
                case RESTRICT:
                case RETURN:
                case SIGNED:
                case SIZEOF:
                case STATIC:
                case STRUCT:
                case SWITCH:
                case TYPEDEF:
                case TYPEOF:
                case UNION:
                case VOLATILE:
                case WHILE:
                case ID:
                case SELF:
                case SUPER:
                case BYCOPY:
                case BYREF:
                case IMP:
                case IN:
                case OUT:
                case INOUT:
                case ONEWAY:
                case ATOMIC:
                case NONATOMIC:
                case RETAIN:
                    type = ColorScheme.Colorable.KEYWORD;
                    break;
                case CHAR:
                case DOUBLE:
                case FLOAT:
                case INT:
                case LONG:
                case SHORT:
                case VOID:
                case BOOL_:
                case COMPLEX:
                case IMAGINERY:
                case BOOL:
                case Class:
                case PROTOCOL_:
                case SEL:
                    type = ColorScheme.Colorable.TYPE;
                    break;
                case TRUE:
                case FALSE:
                case NO:
                case YES:
                case NIL:
                case NULL:
                case CHARACTER_LITERAL:
                case STRING_START:
                case STRING_NEWLINE:
                case STRING_VALUE:
                case STRING_END:
                case HEX_LITERAL:
                case OCTAL_LITERAL:
                case BINARY_LITERAL:
                case DECIMAL_LITERAL:
                case FLOATING_POINT_LITERAL:
                    type = ColorScheme.Colorable.LITERAL;
                    break;
                case AUTORELEASEPOOL:
                case CATCH:
                case CLASS:
                case DYNAMIC:
                case END:
                case ENCODE:
                case FINALLY:
                case IMPLEMENTATION:
                case INTERFACE:
                case IMPORT:
                case PACKAGE:
                case PROTOCOL:
                case OPTIONAL:
                case PRIVATE:
                case PROTECTED:
                case PUBLIC:
                case PROPERTY:
                case REQUIRED:
                case SELECTOR:
                case SYNCHRONIZED:
                case SYNTHESIZE:
                case THROW:
                case TRY:
                    type = ColorScheme.Colorable.PACKAGE;
                    break;
                case MULTI_COMMENT:
                case SINGLE_COMMENT:
                    type = ColorScheme.Colorable.COMMENT;
                    break;
                default:
                    if (tokenType >= LP && tokenType <= ELIPSIS)
                        type = ColorScheme.Colorable.OPERATOR;
                    else
                        type = ColorScheme.Colorable.NAME;
                    break;
            }
            Pair<ColorScheme.Colorable> pair = new Pair<ColorScheme.Colorable>(token.getStopIndex(), type);
            _tokens.add(pair);
        }
    }

    @Override
    protected void parse(ObjectiveCLexer lexer) {
    }
}
