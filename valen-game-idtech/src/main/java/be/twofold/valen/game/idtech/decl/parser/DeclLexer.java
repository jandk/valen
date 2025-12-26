package be.twofold.valen.game.idtech.decl.parser;

import be.twofold.valen.core.util.*;

public final class DeclLexer {
    private static final char Eof = '\0';

    private final boolean lenient;
    private final String source;
    private int index = 0;
    private DeclToken current;

    public DeclLexer(String source) {
        this(source, false);
    }

    public DeclLexer(String source, boolean lenient) {
        this.source = Check.nonNull(source, "source");
        this.lenient = lenient;
    }

    public DeclToken peekToken() {
        if (current == null) {
            current = doNextToken();
        }
        return current;
    }

    public DeclToken nextToken() {
        DeclToken result = peekToken();
        current = null;
        return result;
    }

    private DeclToken doNextToken() {
        skipWhitespace();
        if (isEof()) {
            return new DeclToken(DeclTokenType.Eof, null);
        }

        char ch = peek();
        switch (ch) {
            case '<':
                skip();
                if (peek() == '<') {
                    return punctuation(DeclTokenType.LeftShift, "<<");
                } else {
                    break;
                }
            case '=':
                return punctuation(DeclTokenType.Assign, "=");
            case ',':
                return punctuation(DeclTokenType.Comma, ",");
            case ';':
                return punctuation(DeclTokenType.Semicolon, ";");
            case '(':
                return punctuation(DeclTokenType.OpenParen, "(");
            case ')':
                return punctuation(DeclTokenType.CloseParen, ")");
            case '{':
                return punctuation(DeclTokenType.OpenBrace, "{");
            case '}':
                return punctuation(DeclTokenType.CloseBrace, "}");
            case '[':
                return punctuation(DeclTokenType.OpenBracket, "[");
            case ']':
                return punctuation(DeclTokenType.CloseBracket, "]");
            case '$':
                return punctuation(DeclTokenType.Dollar, "$");
            case '"':
                return new DeclToken(DeclTokenType.String, parseString());
            default:
                if (isAlpha(ch) || ch == '_' || ch == '#') {
                    return new DeclToken(DeclTokenType.Name, parseName());
                }
                if (isDigit(ch) || ch == '-' || ch == '.') {
                    return new DeclToken(DeclTokenType.Number, parseNumber());
                }
        }
        throw new DeclParseException("Unexpected character: " + ch);
    }

    private DeclToken punctuation(DeclTokenType type, String value) {
        skip();
        return new DeclToken(type, value);
    }

    private String parseString() {
        StringBuilder builder = new StringBuilder();
        skip();

        while (true) {
            char ch = next();
            switch (ch) {
                case '"' -> {
                    return builder.toString();
                }
                case '\n' -> {
                    // Only used in one typo in the original DECL files
                    System.err.println("Newline in string literal");
                    return builder.toString();
                }
                case '\\' -> {
                    char escaped = parseEscape();
                    builder.append(escaped);
                }
                default -> builder.append(ch);
            }
        }
    }

    private char parseEscape() {
        char ch = next();
        return switch (ch) {
            case '"' -> '"';
            case '?' -> '?';
            case '\'' -> '\'';
            case '\\' -> '\\';
            case 'a' -> '\u0007';
            case 'n' -> '\n';
            default -> throw new DeclParseException("Unexpected escape: " + ch);
        };
    }

    private String parseNumber() {
        int start = index;
        if (peek() == '-') {
            skip();
        }
        if (peek() == ' ') {
            skip();
        }
        digits();
        if (peek() == '.') {
            skip();
            digits();
        }
        if (peek() == 'e') {
            skip();
            if (peek() == '+' || peek() == '-') {
                skip();
            }
            digits();
        }
        if (peek() == 'f') {
            // Floats can have a trailing f
            skip();
        }
        return source.substring(start, index).replace(" ", "");
    }

    private void digits() {
        while (isDigit(peek())) {
            skip();
        }
    }

    private String parseName() {
        int start = index;
        while (isIdentifier(peek())) {
            skip();
        }
        return source.substring(start, index);
    }

    private void skipWhitespace() {
        while (!isEof()) {
            char ch = peek();
            if (ch <= ' ') {
                skip();
            } else if (ch == '/') {
                if (peekNext() == '/') {
                    skipLineComment();
                } else if (peekNext() == '*') {
                    skipBlockComment();
                } else {
                    break;
                }
            } else {
                break;
            }
        }
    }

    private void skipLineComment() {
        skip(2);
        while (!isEof()) {
            if (peek() == '\n') {
                skip();
                return;
            }
            skip();
        }
    }

    private void skipBlockComment() {
        skip(2);
        while (!isEof()) {
            if (peek() == '*' && peekNext() == '/') {
                skip(2);
                return;
            }
            skip();
        }
    }

    // Predicates

    private boolean isAlpha(char ch) {
        return (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z');
    }

    private boolean isDigit(char ch) {
        return (ch >= '0' && ch <= '9');
    }

    private boolean isIdentifier(char ch) {
        return isAlpha(ch) || isDigit(ch) || ch == '_' || ch == '#' ||
            lenient && (ch == '.' || ch == '/' || ch == ':' || ch == '\\');
    }

    // Low level methods

    private char peek() {
        return source.charAt(index);
    }

    private char peekNext() {
        return source.charAt(index + 1);
    }

    private char next() {
        return source.charAt(index++);
    }

    private void skip() {
        skip(1);
    }

    private void skip(int count) {
        index += count;
    }

    private boolean isEof() {
        return index >= source.length();
    }
}
