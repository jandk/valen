package be.twofold.valen.reader.decl.parser;

import be.twofold.valen.core.util.*;

public final class DeclLexer {
    private final String source;
    private int index = 0;

    public DeclLexer(String source) {
        this.source = Check.notNull(source);
    }

    public DeclToken peekToken() {
        int oldIndex = index;
        DeclToken token = nextToken();
        index = oldIndex;
        return token;
    }

    public DeclToken nextToken() {
        if (isEof()) {
            return token(DeclTokenType.Eof, null);
        }
        skipWhitespace();
        switch (peek()) {
            case '{' -> {
                read();
                return token(DeclTokenType.ObjectStart, null);
            }
            case '}' -> {
                read();
                return token(DeclTokenType.ObjectEnd, null);
            }
            case '=' -> {
                read();
                return token(DeclTokenType.Equals, null);
            }
            case ';' -> {
                read();
                return token(DeclTokenType.Semicolon, null);
            }
            case '"' -> {
                return token(DeclTokenType.String, parseString(true));
            }
            default -> {
                if (isAlpha(peek()) || peek() == '_') {
                    String value = parseString(false);
                    return switch (value) {
                        case "true" -> token(DeclTokenType.True, null);
                        case "false" -> token(DeclTokenType.False, null);
                        default -> token(DeclTokenType.String, value);
                    };
                }
                if (isDigit(peek()) || peek() == '-') {
                    return token(DeclTokenType.Number, parseNumber());
                }
                throw new RuntimeException("Unexpected character '" + (char) peek() + "'");
            }
        }
    }

    private DeclToken token(DeclTokenType type, String value) {
        return new DeclToken(type, value);
    }


    private String parseString(boolean quoted) {
        var builder = new StringBuilder();

        if (quoted) {
            index++; // skip first quote
        }

        while (!isEof()) {
            if (quoted) {
                if (peek() == '"') {
                    break;
                }
            } else if (!isIdentifier(peek())) {
                break;
            }
            builder.append((char) read());
        }

        if (quoted) {
            index++; // skip last quote
        }

        return builder.toString();
    }


    private String parseNumber() {
        int start = index;
        if (peek() == '-') {
            index++;
        }

        // integer part
        if (peek() == '0') {
            index++;
        } else {
            if (peek() < '1' || peek() > '9') {
                throw new RuntimeException();
            }
            digits();
        }

        // decimal part
        if (peek() == '.') {
            index++;
            digits();
        }

        // exponent part
        if (peek() == 'e') {
            index++;
            if (peek() == '-') {
                index++;
            }
            digits();
        }

        return source.substring(start, index);
    }

    private void digits() {
        if (!isDigit(peek())) {
            throw new RuntimeException("Expected a digit");
        }
        while (isDigit(peek())) {
            index++;
        }
    }


    private void skipWhitespace() {
        while (isWhitespace(peek())) {
            index++;
        }
    }

    private boolean isAlpha(int c) {
        return c >= 'a' && c <= 'z'
            || c >= 'A' && c <= 'Z';
    }

    private boolean isDigit(int c) {
        return c >= '0' && c <= '9';
    }

    private boolean isIdentifier(int c) {
        return isAlpha(c) || isDigit(c) || c == '_' || c == '[' || c == ']';
    }

    private boolean isWhitespace(int c) {
        return switch (c) {
            case ' ', '\n', '\r', '\t' -> true;
            default -> false;
        };
    }

    // Low level methods

    private int peek() {
        return source.charAt(index);
    }

    private int read() {
        return source.charAt(index++);
    }

    private int next() {
        return source.charAt(++index);
    }

    private boolean isEof() {
        return index >= source.length();
    }

}
