package be.twofold.valen.game.source.readers.keyvalue;

import wtf.reversed.toolbox.util.*;

final class KeyValueLexer {
    private final Source source;
    private KeyValueToken current;

    KeyValueLexer(Source source) {
        this.source = Check.nonNull(source, "source");
    }

    KeyValueToken peekToken() {
        if (current == null) {
            current = doNextToken();
        }
        return current;
    }

    KeyValueToken nextToken() {
        KeyValueToken result = peekToken();
        current = null;
        return result;
    }

    private KeyValueToken doNextToken() {
        skipWhitespace();
        if (source.isEof()) {
            return new KeyValueToken(KeyValueTokenType.Eof, null);
        }

        return switch (source.peek()) {
            case '{' -> {
                source.skip();
                yield new KeyValueToken(KeyValueTokenType.OpenBrace, "{");
            }
            case '}' -> {
                source.skip();
                yield new KeyValueToken(KeyValueTokenType.CloseBrace, "}");
            }
            default -> new KeyValueToken(KeyValueTokenType.String, parseString());
        };
    }

    private String parseString() {
        if (source.peek() == '"') {
            return parseQuotedString();
        }

        return parseUnquotedString();
    }

    private String parseUnquotedString() {
        var builder = new StringBuilder();
        while (true) {
            char ch = source.peek();
            if (Character.isWhitespace(ch) || ch == '{' || ch == '}' || ch == '"') {
                break;
            }
            builder.append(source.next());
        }
        return builder.toString();
    }

    private String parseQuotedString() {
        var builder = new StringBuilder();
        source.skip(); // skip leading '"'
        while (true) {
            var next = source.next();
            switch (next) {
                case '"':
                    return builder.toString();
                case '\\':
                    builder.append(parseEscape());
                    break;
                default:
                    builder.append(next);
                    break;
            }
        }
    }

    private char parseEscape() {
        char next = source.next();
        return switch (next) {
            case 'n' -> '\n';
            case 't' -> '\t';
            case 'v' -> '\u000b';
            case 'b' -> '\b';
            case 'r' -> '\r';
            case 'f' -> '\f';
            case 'a' -> '\u0007';
            case '?' -> '?';
            case '"' -> '\"';
            case '\\' -> '\\';
            case '\'' -> '\'';
            default -> throw new UnsupportedOperationException("Unrecognized escape: " + next);
        };
    }

    private void skipWhitespace() {
        while (!source.isEof()) {
            int cp = source.peek();
            if (cp <= ' ') {
                source.skip();
            } else if (cp == '/') {
                if (source.peekNext() == '/') {
                    skipLineComment();
                } else if (source.peekNext() == '*') {
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
        source.skip(2);
        while (!source.isEof()) {
            if (source.peek() == '\n') {
                source.skip();
                return;
            }
            source.skip();
        }
    }

    private void skipBlockComment() {
        source.skip(2);
        while (!source.isEof()) {
            if (source.peek() == '*' && source.peekNext() == '/') {
                source.skip(2);
                return;
            }
            source.skip();
        }
    }
}
