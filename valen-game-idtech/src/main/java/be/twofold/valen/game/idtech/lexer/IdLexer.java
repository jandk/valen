package be.twofold.valen.game.idtech.lexer;

import be.twofold.valen.core.text.*;
import be.twofold.valen.core.util.*;

import java.util.*;

import static be.twofold.valen.game.idtech.lexer.LexerFlags.*;
import static be.twofold.valen.game.idtech.lexer.LexerPunctuation.*;
import static be.twofold.valen.game.idtech.lexer.NumberType.*;

public final class IdLexer {
    private final SourceCursor cursor;
    private final Set<LexerFlags> flags;

    public IdLexer(String source, LexerFlags... flags) {
        this.cursor = new SourceCursor(source);
        this.flags = EnumSet.noneOf(LexerFlags.class);
        this.flags.addAll(Arrays.asList(flags));
    }

    IdToken readString(char quote) {
        var type = quote == '"' ? TokenType.TT_STRING : TokenType.TT_LITERAL;
        int line = cursor.line();
        int column = cursor.column();

        var sb = new StringBuilder();
        cursor.advance(); // skip leading quote

        while (true) {
            char c = cursor.peek();
            if (c == '\\' && !flags.contains(LEXFL_NOSTRINGESCAPECHARS)) {
                if (flags.contains(LEXFL_NOEMITSTRINGESCAPECHARS)) {
                    sb.append('\\');
                }
                sb.append(readEscapeCharacter());
            } else if (c == quote) {
                cursor.advance(); // step over the quote
                if (flags.contains(LEXFL_NOSTRINGCONCAT)) {
                    // MISSING: backslash string concat
                    break;
                }

                // consecutive strings are yuck
                int mark = cursor.mark();
                if (!skipWhiteSpace(false) || cursor.peek() != quote) {
                    cursor.reset(mark);
                    break;
                }
                cursor.advance();
            } else if (c == SourceCursor.EOF) {
                error("missing trailing quote");
                return null;
            } else if (c == '\n') {
                error("newline inside string");
                return null;
            } else {
                sb.append(cursor.advance());
            }
        }

        // strings keep subtype 0 since Dark Ages, earlier engines store the length there
        var value = sb.toString();
        int subtype = 0;
        if (type == TokenType.TT_LITERAL) {
            if (!flags.contains(LEXFL_ALLOWMULTICHARLITERALS) && value.length() != 1) {
                warning("literal is not one character long");
            }
            subtype = value.isEmpty() ? 0 : value.charAt(0);
        }
        return new IdToken(type, subtype, value, line, column);
    }

    char readEscapeCharacter() {
        cursor.advance(); // skip leading '\\'

        if (flags.contains(LexerFlags.LEXFL_NOEMITSTRINGESCAPECHARS)) {
            return cursor.advance();
        }

        if (Ascii.isDigit(cursor.peek())) {
            int value = 0;
            while (Ascii.isDigit(cursor.peek())) {
                value = value * 10 + Ascii.toDigit(cursor.advance());
            }
            if (value > 0xFF) {
                warning("too large value in escape character");
                value = 0xFF;
            }
            return (char) value;
        }

        return switch (cursor.advance()) {
            case '\\' -> '\\';
            case 'n' -> '\n';
            case 'r' -> '\r';
            case 't' -> '\t';
            case 'v' -> '\u000B';
            case 'b' -> '\b';
            case 'f' -> '\f';
            case 'a' -> '\u0007';
            case '\'' -> '\'';
            case '\"' -> '\"';
            case '?' -> '?';
            case 'x' -> {
                int val = 0;
                while (Ascii.isAlNum(cursor.peek())) { // Yuck
                    val = val * 16 + Ascii.toDigit(cursor.advance());
                }
                if (val > 0xFF) {
                    warning("too large value in escape character");
                    val = 0xFF;
                }
                yield (char) val;
            }
            default -> {
                error("unknown escape char");
                yield 0;
            }
        };
    }

    IdToken readName() {
        cursor.startLexeme();
        int line = cursor.line();
        int column = cursor.column();

        cursor.advance();
        cursor.skipWhile(this::isNameChar);

        String lexeme = cursor.lexeme();
        return new IdToken(
            TokenType.TT_NAME,
            lexeme.length(),
            lexeme,
            line,
            column
        );
    }

    private boolean isNameChar(int c) {
        return Ascii.isWord(c)
            || (flags.contains(LEXFL_ONLYSTRINGS) && c == '-')
            // MISSING: '@' for idTech 5
            || (flags.contains(LEXFL_ALLOWPATHNAMES) && (c == '/' || c == '\\' || c == ':' || c == '.' || c == '$'))
            || (flags.contains(LEXFL_ALLOWWILDCARD) && c == '*');
    }

    IdToken readNumber() {
        cursor.startLexeme();
        int line = cursor.line();
        int column = cursor.column();

        int subtype;
        if (cursor.peek() == '0' && cursor.peek(1) != '.') {
            cursor.advance(); // '0'
            if (cursor.matchIgnoreCase('x')) {
                cursor.skipWhile(Ascii::isHexDigit);
                subtype = TT_INTEGER | TT_HEX;
            } else if (cursor.matchIgnoreCase('b')) {
                cursor.skipWhile(Ascii::isBinDigit);
                subtype = TT_INTEGER | TT_BINARY;
            } else {
                cursor.skipWhile(Ascii::isOctDigit);
                subtype = TT_INTEGER | TT_OCTAL;
            }
        } else {
            // decimal integer or floating point number or ip address
            int dot = 0;
            while (true) {
                if (cursor.peek() == '.') {
                    dot++;
                } else if (!Ascii.isDigit(cursor.peek())) {
                    break;
                }
                cursor.advance();
            }

            if (dot == 1 || cursor.peek() == 'e') {
                subtype = TT_DECIMAL | TT_FLOAT;
                if (cursor.peek() == 'e') { // exponent
                    cursor.advance();
                    if (cursor.peek() == '-') {
                        cursor.advance();
                    } else if (cursor.peek() == '+') {
                        cursor.advance();
                    }
                    cursor.skipWhile(Ascii::isDigit);
                } else if (cursor.peek() == '#') { // exception
                    cursor.advance();
                    if (cursor.match("INF")) {
                        subtype |= TT_INFINITE;
                    } else if (cursor.match("IND")) {
                        subtype |= TT_INDEFINITE;
                    } else if (cursor.match("NAN")) {
                        subtype |= TT_NAN;
                    } else if (cursor.match("QNAN")) {
                        subtype |= TT_NAN;
                    } else if (cursor.match("SNAN")) {
                        subtype |= TT_NAN;
                    }
                    cursor.skipWhile(Ascii::isDigit);
                    if (!flags.contains(LEXFL_ALLOWFLOATEXCEPTIONS)) {
                        error("parsed " + cursor.lexeme());
                    }
                }
            } else if (dot > 1) {
                if (!flags.contains(LEXFL_ALLOWIPADDRESSES)) {
                    error("more than one dot in number");
                    return null;
                }
                if (dot != 3) {
                    error("ip address should have three dots");
                    return null;
                }
                subtype = TT_IPADDRESS;
            } else {
                subtype = TT_DECIMAL | TT_INTEGER;
            }
        }

        if ((subtype & TT_IPADDRESS) != 0) {
            if (cursor.match(':')) {
                cursor.skipWhile(Ascii::isDigit);
                subtype |= TT_IPPORT;
            }
        }

        String value = cursor.lexeme();
        if ((subtype & TT_FLOAT) != 0) {
            if (cursor.matchIgnoreCase('f')) {
                subtype |= TT_SINGLE_PRECISION;
            } else if (cursor.matchIgnoreCase('l')) {
                subtype |= TT_EXTENDED_PRECISION;
            } else if (cursor.matchIgnoreCase('h')) {
                subtype |= TT_HALF_PRECISION;
            } else {
                subtype |= TT_DOUBLE_PRECISION;
            }
        } else if ((subtype & TT_INTEGER) != 0) {
            for (int i = 0; i < 3; i++) {
                if (cursor.matchIgnoreCase('l')) {
                    subtype |= TT_LONG;
                } else if (cursor.matchIgnoreCase('u')) {
                    subtype |= TT_UNSIGNED;
                } else {
                    break;
                }
            }
        }

        return new IdToken(
            TokenType.TT_NUMBER,
            subtype,
            value,
            line,
            column
        );
    }

    IdToken readPunctuation() {
        int line = cursor.line();
        int column = cursor.column();

        var punctuation = matchPunctuation();
        if (punctuation == null) {
            error("unknown punctuation");
            return null;
        }

        cursor.skip(punctuation.text().length());
        return new IdToken(
            TokenType.TT_PUNCTUATION,
            punctuation.ordinal(),
            punctuation.text(),
            line,
            column
        );
    }

    private LexerPunctuation matchPunctuation() {
        // @formatter:off
        return switch (cursor.peek()) {
            // 5 alternatives
            case '-' -> cursor.check("->*") ? P_POINTER_TO_MEMBER_POINTER
                      : cursor.check("->")  ? P_MEMBER_SELECTION_POINTER
                      : cursor.check("-=")  ? P_SUB_ASSIGN
                      : cursor.check("--")  ? P_DEC
                      : P_SUB;

            // 4 alternatives
            case '<' -> cursor.check("<<=") ? P_LSHIFT_ASSIGN
                      : cursor.check("<<")  ? P_LSHIFT
                      : cursor.check("<=")  ? P_LOGIC_LEQ
                      : P_LOGIC_LESS;
            case '>' -> cursor.check(">>=") ? P_RSHIFT_ASSIGN
                      : cursor.check(">>")  ? P_RSHIFT
                      : cursor.check(">=")  ? P_LOGIC_GEQ
                      : P_LOGIC_GREATER;

            // 3 alternatives
            case '&' -> cursor.check("&&")  ? P_LOGIC_AND
                      : cursor.check("&=")  ? P_BIN_AND_ASSIGN
                      : P_BIN_AND;
            case '+' -> cursor.check("+=")  ? P_ADD_ASSIGN
                      : cursor.check("++")  ? P_INC
                      : P_ADD;
            case '.' -> cursor.check("...") ? P_PARMS
                      : cursor.check(".*")  ? P_POINTER_TO_MEMBER_OBJECT
                      : P_MEMBER_SELECTION_OBJECT;
            case '|' -> cursor.check("||")  ? P_LOGIC_OR
                      : cursor.check("|=")  ? P_BIN_OR_ASSIGN
                      : P_BIN_OR;

            // 2 alternatives
            case '!' -> cursor.check("!=") ? P_LOGIC_UNEQ       : P_LOGIC_NOT;
            case '#' -> cursor.check("##") ? P_PRECOMPMERGE     : P_PRECOMP;
            case '%' -> cursor.check("%=") ? P_MOD_ASSIGN       : P_MOD;
            case '*' -> cursor.check("*=") ? P_MUL_ASSIGN       : P_MUL;
            case '/' -> cursor.check("/=") ? P_DIV_ASSIGN       : P_DIV;
            case ':' -> cursor.check("::") ? P_SCOPE_RESOLUTION : P_COLON;
            case '=' -> cursor.check("==") ? P_LOGIC_EQ         : P_ASSIGN;
            case '^' -> cursor.check("^=") ? P_BIN_XOR_ASSIGN   : P_BIN_XOR;

            // 1 alternative
            case '"'  -> P_QUOTE;
            case '$'  -> P_DOLLAR;
            case '\'' -> P_APOSTROPHE;
            case '('  -> P_PARENTHESESOPEN;
            case ')'  -> P_PARENTHESESCLOSE;
            case ','  -> P_COMMA;
            case ';'  -> P_SEMICOLON;
            case '?'  -> P_QUESTIONMARK;
            case '@'  -> P_AT;
            case '['  -> P_SQBRACKETOPEN;
            case '\\' -> P_BACKSLASH;
            case ']'  -> P_SQBRACKETCLOSE;
            case '{'  -> P_BRACEOPEN;
            case '}'  -> P_BRACECLOSE;
            case '~'  -> P_BIN_NOT;

            default -> null;
        };
        // @formatter:on
    }

    boolean readWhiteSpace() {
        return skipWhiteSpace(false);
    }

    boolean skipWhiteSpace(boolean currentLine) {
        while (true) {
            // whitespace
            while (isWhitespace(cursor.peek())) {
                if (cursor.advance() == '\n' && currentLine) {
                    return true;
                }
            }
            if (cursor.isAtEnd()) {
                return false;
            }

            // line comments
            if (cursor.match("//")) {
                if (!cursor.skipPast('\n')) {
                    return false;
                }
                if (currentLine) {
                    return true;
                }
                continue;
            }

            // block comments
            if (cursor.match("/*")) {
                while (!cursor.match("*/")) {
                    // MISSING: nested comment warning
                    if (cursor.advance() == '\0') {
                        return false;
                    }
                }
                continue;
            }
            return true;
        }
    }

    private static boolean isWhitespace(int cp) {
        return cp != '\0' && (cp <= 0x20 || cp >= 0x80);
    }


    private void warning(String message) {
    }

    private void error(String message) {
    }
}
