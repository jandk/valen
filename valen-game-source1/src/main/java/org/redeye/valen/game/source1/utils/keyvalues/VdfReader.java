package org.redeye.valen.game.source1.utils.keyvalues;

import java.io.*;
import java.util.*;

import static java.lang.Character.*;

public class VdfReader {
    private enum TokenType {
        ObjectStart,
        ObjectEnd,
        String,
        Number,
        Eof,
    }

    private static final int NotPeeked = -2;
    private final Reader reader;
    private int peeked = NotPeeked;

    private final StringBuilder builder = new StringBuilder();
    private TokenType token;
    private String value;

    public VdfReader(Reader reader) {
        this.reader = new BufferedReader(Objects.requireNonNull(reader));
    }

    public VdfValue parseObjectArray() throws IOException {
        nextToken(); //First token read
        List<VdfValue> values = new ArrayList<>();
        while (token != TokenType.Eof) {
            values.add(parseObject());
            nextToken();//Consume objectClose
        }
        return new VdfValue.VdfList(values);
    }

    public VdfValue parse() throws IOException {
        nextToken(); //First token read
        VdfValue result;
        if (token == TokenType.ObjectStart) {
            result = parseObject();
        } else if (token == TokenType.String) {
            var pair = parsePair();
            var object = new LinkedHashMap<String, VdfValue>();
            object.put(pair.key(), pair.value());
            result = new VdfValue.VdfObject(object);
        } else {
            throw new IllegalStateException("Unexpected token: " + token);
        }
        nextToken();
        if (token != TokenType.Eof) {
            throw new IllegalStateException("Expected EOF");
        }
        return result;
    }

    private VdfValue parseObject() throws IOException {
        expectAndConsume(TokenType.ObjectStart);
        var object = new LinkedHashMap<String, VdfValue>();
        while (token != TokenType.ObjectEnd) {
            var pair = parsePair();

            if (pair.key().contains("+")) {
                String[] splitKeys = pair.key().split("\\+");
                for (String splitKey : splitKeys) {
                    var items = object.computeIfAbsent(splitKey, s -> new VdfValue.VdfList(new ArrayList<>()));
                    ((VdfValue.VdfList) items).values().add(pair.value());
                }
            } else {
                var items = object.computeIfAbsent(pair.key(), s -> new VdfValue.VdfList(new ArrayList<>()));
                ((VdfValue.VdfList) items).values().add(pair.value());
            }
            nextToken();
        }
        expect(TokenType.ObjectEnd);
        return new VdfValue.VdfObject(object);
    }

    private VdfValue parseValue() throws IOException {
        nextToken();
        switch (token) {
            case ObjectStart -> {
                return parseObject();
            }
            case String -> {
                return new VdfValue.VdfString(value);
            }
            case Number -> {
                return new VdfValue.VdfNumber(new StringNumber(value));
            }
            default -> throw new IllegalStateException("Unexpected token: " + token);
        }
    }

    private KeyValuePair parsePair() throws IOException {
        Objects.requireNonNull(value, "Key is null");
        var key = value.toLowerCase();
        var value = parseValue();
        return new KeyValuePair(key, value);
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
                token(TokenType.String, parseUnquotedString());
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
            if (peek == '"' || peek == '{' || peek == '}' || isWhitespace(peek)) {
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
        FileReader fileReader = new FileReader("D:\\SteamLibrary\\steamapps\\common\\Portal\\portal\\kv_array.txt");
        VdfReader reader = new VdfReader(fileReader);
        var value = reader.parseObjectArray();
        System.out.println(value);
    }

}
