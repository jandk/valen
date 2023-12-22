package be.twofold.valen.reader.decl.parser;

import be.twofold.valen.core.util.*;

public final class DeclLexer {
    private static final int EOF = -1;
    private final String source;
    private int index = 0;

    public DeclLexer(String source) {
        this.source = Check.notNull(source);
    }

    public DeclToken peekToken() {
        int start = index;
        DeclToken token = nextToken();
        index = start;
        return token;
    }

    public DeclToken nextToken() {
        skipWhitespace();
        if (isEof()) {
            return token(DeclTokenType.Eof, null);
        }

        int ch = peek();
        switch (ch) {
            case '{':
                skip();
                return token(DeclTokenType.ObjectStart, null);
            case '}':
                skip();
                return token(DeclTokenType.ObjectEnd, null);
            case '=':
                skip();
                return token(DeclTokenType.Equals, null);
            case '"':
                skip();
                return token(DeclTokenType.String, parseQuotedString());
            case ';':
                skip();
                return token(DeclTokenType.Semicolon, null);
            case ',':
                skip();
                return token(DeclTokenType.Comma, null);
            case '/':
                skipComment();
                return nextToken();
            default:
                if (isAlpha(ch) || ch == '_') {
                    String value = parseUnquotedString();
                    return switch (value) {
                        case "NULL" -> token(DeclTokenType.Null, null);
                        case "true" -> token(DeclTokenType.True, null);
                        case "false" -> token(DeclTokenType.False, null);
                        default -> token(DeclTokenType.Name, value);
                    };
                }
                if (isDigit(ch) || ch == '-' || ch == '.') {
                    return token(DeclTokenType.Number, parseNumber());
                }
                throw new DeclParseException("Unexpected character '" + (char) ch + "'");
        }
    }

    private void skipComment() {
        skip();
        int peek = peek();
        if (peek == '/') {
            skipLineComment();
        } else if (peek == '*') {
            skipBlockComment();
        } else {
            throw new DeclParseException("Unexpected character '/'");
        }
    }

    private void skipLineComment() {
        while (true) {
            int ch = read();
            if (ch == '\n' || ch == EOF) {
                break;
            }
        }
    }

    private void skipBlockComment() {
        skip();
        while (true) {
            int ch = read();
            if (ch == EOF) {
                throw new DeclParseException("Unexpected end of input");
            }
            if (ch == '*') {
                if (peek() == '/') {
                    skip();
                    break;
                }
            }
        }
    }

    private DeclToken token(DeclTokenType type, String value) {
        return new DeclToken(type, value);
    }


    private String parseUnquotedString() {
        var builder = new StringBuilder();

        while (isIdentifier(peek())) {
            builder.append((char) read());
        }

        return builder.toString();
    }

    private String parseQuotedString() {
        var builder = new StringBuilder();

        while (true) {
            int ch = read();
            switch (ch) {
                case EOF -> throw new DeclParseException("Unexpected end of input");
                case '"' -> {
                    return builder.toString();
                }
                case '\\' -> {
                    builder.append(readEscape());
                }
                default -> builder.append((char) ch);
            }
        }
    }

    private char readEscape() {
        int c = read();
        return switch (c) {
            case '"' -> '"';
            case '?' -> '?';
            case '\'' -> '\'';
            case '\\' -> '\\';
            case 'a' -> 'a';
            case 'n' -> '\n';
            default -> throw new DeclParseException("Unexpected escape character '" + (char) c + "'");
        };
    }


    private String parseNumber() {
        int start = index;
        if (peek() == '-') {
            skip();
        }

        // integer part
        if (isDigit(peek())) {
            digits();
        }

        // decimal part
        if (peek() == '.') {
            skip();
            digits();
        }

        // exponent part
        if (peek() == 'e') {
            skip();
            if (peek() == '+' || peek() == '-') {
                skip();
            }
            digits();
        }

        return source.substring(start, index);
    }

    private void digits() {
        if (!isDigit(peek())) {
            throw new DeclParseException("Expected a digit");
        }
        skip();
        while (isDigit(peek())) {
            skip();
        }
    }


    private void skipWhitespace() {
        while (isWhitespace(peek())) {
            skip();
        }
    }

    private boolean isAlpha(int c) {
        return c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z';
    }

    private boolean isDigit(int c) {
        return c >= '0' && c <= '9';
    }

    private boolean isIdentifier(int c) {
        return isAlpha(c) || isDigit(c) || c == '[' || c == ']' || c == '_';
    }

    private boolean isWhitespace(int c) {
        return c == '\t' || c == ' ' || c == '\n' || c == '\r';
    }

    // Low level methods

    private int peek() {
        if (isEof()) {
            return EOF;
        }
        return source.charAt(index);
    }

    private int read() {
        if (isEof()) {
            return EOF;
        }
        return source.charAt(index++);
    }

    private void skip() {
        index++;
    }

    private boolean isEof() {
        return index >= source.length();
    }

}
