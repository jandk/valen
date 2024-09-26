package org.redeye.valen.game.spacemarines2.td;

import java.io.*;
import java.util.*;

import static java.lang.Character.*;

public class TDParser {
    private enum TokenType {
        ObjectStart,
        ObjectEnd,
        ListStart,
        ListEnd,
        Identifier,
        String,
        Number,
        Equals,
        Comma,
        Eof,
    }

    private static final int NotPeeked = -2;
    private final Reader reader;
    private int peeked = NotPeeked;

    private final StringBuilder builder = new StringBuilder();
    private TokenType token;
    private String value;

    public TDParser(Reader reader) {
        this.reader = new BufferedReader(Objects.requireNonNull(reader));
    }

    public TDValue.TDObject parse() throws IOException {
        nextToken(); // First token read
        var item = parseObjectInner(true);
        expectAndConsume(TokenType.Eof);
        return item;
    }

    private TDValue.TDObject parseObject() throws IOException {
        expectAndConsume(TokenType.ObjectStart);
        var obj = parseObjectInner(false);
        expectAndConsume(TokenType.ObjectEnd);
        return obj;
    }

    private TDValue.TDObject parseObjectInner(boolean isTopLevel) throws IOException {
        var object = new LinkedHashMap<String, TDValue>();
        while (token != TokenType.ObjectEnd) {
            if (token == TokenType.Eof && isTopLevel) {
                break;
            }
            if (token == TokenType.Identifier) {
                expect(TokenType.Identifier);
            } else {
                expect(TokenType.String);
            }
            var key = value;
            nextToken();
            var value = parseAssignment();
            object.put(key, value);
        }
        return new TDValue.TDObject(object);
    }

    private TDValue.TDList parseArray() throws IOException {
        expectAndConsume(TokenType.ListStart);
        var items = new ArrayList<TDValue>();
        if (token != TokenType.ListEnd) {
            items.add(parseValue());
            while (token == TokenType.Comma) {
                expectAndConsume(TokenType.Comma);
                items.add(parseValue());
            }
        }
        expectAndConsume(TokenType.ListEnd);
        return new TDValue.TDList(items);
    }

    private TDValue parseAssignment() throws IOException {
        expectAndConsume(TokenType.Equals);
        return parseValue();
    }

    private TDValue parseValue() throws IOException {
        switch (token) {
            case ObjectStart -> {
                return parseObject();
            }
            case ListStart -> {
                return parseArray();
            }
            case String -> {
                TDValue.TDString tdString = new TDValue.TDString(value);
                expectAndConsume(TokenType.String);
                return tdString;
            }
            case Identifier -> {
                var bool = switch (value.toLowerCase()) {
                    case "true" -> new TDValue.TDBoolean(true);
                    case "false" -> new TDValue.TDBoolean(false);
                    default -> throw new IllegalStateException("Unexpected value: " + value);
                };
                expectAndConsume(TokenType.Identifier);
                return bool;
            }
            case Number -> {
                TDValue.TDNumber tdNumber = new TDValue.TDNumber(new StringNumber(value));
                expectAndConsume(TokenType.Number);
                return tdNumber;
            }
            default -> throw new IllegalStateException("Unexpected token: " + token);
        }
    }

    private void nextToken() throws IOException {
        while (true) {
            skipWhitespace();
            if (!skipComments()) {
                break;
            }
        }
        switch (peek()) {
            case '{':
                read();
                token(TokenType.ObjectStart, null);
                break;
            case '}':
                read();
                token(TokenType.ObjectEnd, null);
                break;
            case '[':
                read();
                token(TokenType.ListStart, null);
                break;
            case ']':
                read();
                token(TokenType.ListEnd, null);
                break;
            case '=':
                read();
                token(TokenType.Equals, null);
                break;
            case ',':
                read();
                token(TokenType.Comma, null);
                break;
            case '-':
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
                token(TokenType.Number, parseNumber());
                break;
            case -1:
                token(TokenType.Eof, null);
                break;
            case '"':
                token(TokenType.String, parseQuotedString());
                break;
            default:
                token(TokenType.Identifier, parseUnquotedString());
        }
    }

    private void expectAndConsume(TokenType expected) throws IOException {
        if (token == expected) {
            nextToken();
            return;
        }
        throw new IllegalStateException("Unexpected token: %s, but got: %s".formatted(expected, token));
    }

    private void expect(TokenType expected) {
        if (token == expected) {
            return;
        }
        throw new IllegalStateException("Unexpected token: %s, but got: %s".formatted(expected, token));
    }

    private String parseUnquotedString() {
        builder.setLength(0);
        while (true) {
            int peek = peek();
            if (isWhitespace(peek)) {
                break;
            }
            appendNext();
        }
        return builder.toString();
    }

    private String parseQuotedString() {
        // skip leading '"'
        read();
        builder.setLength(0);
        while (!isEof() && peek() != '"') {
            if (peek() < 0x20) {
                throw new IllegalStateException("Raw control character");
            }
            if (peek() == '\\') {
                read();
                switch (read()) {
                    case '"':
                        builder.append('"');
                        break;
                    case '\\':
                        builder.append('\\');
                        break;
                    case '/':
                        builder.append('/');
                        break;
                    case 'b':
                        builder.append('\b');
                        break;
                    case 'f':
                        builder.append('\f');
                        break;
                    case 'n':
                        builder.append('\n');
                        break;
                    case 'r':
                        builder.append('\r');
                        break;
                    case 't':
                        builder.append('\t');
                        break;
                    default:
                        throw new IllegalStateException("Illegal escape");
                }
            } else {
                appendNext();
            }
        }
        if (read() != '"') {
            throw new IllegalStateException("Unclosed string literal");
        }
        return builder.toString();
    }

    private String parseNumber() {
        builder.setLength(0);
        if (peek() == '-') {
            appendNext();
        }
        // integer part
        if (peek() == '0') {
            appendNext();
        } else {
            if (peek() < '1' || peek() > '9') {
                throw new IllegalStateException("Invalid number character: " + peek());
            }
            digits();
        }
        // decimal part
        if (peek() == '.') {
            appendNext();
            digits();
        }
        // exponent part
        if (peek() == 'e' || peek() == 'E') {
            appendNext();
            if (peek() == '-' || peek() == '+') {
                appendNext();
            }
            digits();
        }
        return builder.toString();
    }

    private void digits() {
        if (!isDigit(peek())) {
            throw new IllegalStateException("Expected a digit");
        }
        while (isDigit(peek())) {
            appendNext();
        }
    }

    private void token(TokenType type, String value) {
        this.token = type;
        this.value = value;
    }

    private void appendNext() {
        builder.append((char) read());
    }

    private void skipWhitespace() {
        while (Character.isWhitespace(peek())) {
            read();
        }
    }

    private boolean skipComments() throws IOException {
        reader.mark(2);
        if (peek() != '/') {
            return false;
        }
        read();
        if (peek() != '/') {
            reader.reset();
            return false;
        }
        while (peek() != '\n' && peek() != -1) {
            read();
        }
        return true;
    }

    private int peek() {
        if (peeked == NotPeeked) {
            peeked = readChar();
        }
        return peeked;
    }

    private int read() {
        if (peeked == NotPeeked) {
            return readChar();
        }
        int result = peeked;
        peeked = NotPeeked;
        return result;
    }

    private boolean isEof() {
        return peek() == -1;
    }

    private int readChar() {
        try {
            return reader.read();
        } catch (IOException e) {
            throw new IllegalStateException("Unexpected I/O error", e);
        }
    }

    public static void main(String[] args) throws IOException {
        FileReader fileReader = new FileReader("D:\\projects\\java\\valen\\valen-game-spacemarines2\\ch_calgar_head_hair_01_dm.td");
        TDParser reader = new TDParser(fileReader);
        var value = reader.parse();
        System.out.println(value);
    }
}


