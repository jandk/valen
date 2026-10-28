package be.twofold.valen.ui.component.rawview.binary;

import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.util.*;

import java.nio.charset.*;
import java.util.*;

final class HexDump {
    static final int BYTES_PER_ROW = 16;
    static final int HEX_SPLIT_COLUMN = (BYTES_PER_ROW / 2) * 3;
    static final int SEPARATOR_COLUMN = BYTES_PER_ROW * 3 + 1;
    static final int ASCII_COLUMN = SEPARATOR_COLUMN + 2;
    static final int ROW_LENGTH = ASCII_COLUMN + BYTES_PER_ROW + 1;

    static final String[] HEX = hexStrings();
    static final String[] ALPHABET = alphabet(Charset.forName("windows-1252"));

    private final Bytes binary;

    HexDump(Bytes binary) {
        this.binary = Check.nonNull(binary, "binary");
    }

    private static String[] hexStrings() {
        var strings = new String[256];
        for (int value = 0; value < strings.length; value++) {
            strings[value] = String.format("%02X ", value);
        }
        return strings;
    }

    private static String[] alphabet(Charset charset) {
        var bytes = new byte[256];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) i;
        }

        var decoded = new String(bytes, charset);
        var strings = new String[256];
        for (int i = 0; i < strings.length; i++) {
            char ch = decoded.charAt(i);
            boolean printable = Character.getType(ch) != Character.CONTROL && ch != 0xFFFD;
            strings[i] = String.valueOf(printable ? ch : '.');
        }
        return strings;
    }

    int size() {
        return Math.ceilDiv(binary.length(), BYTES_PER_ROW);
    }

    String offset(int row) {
        int offset = row * BYTES_PER_ROW;
        var format = HexFormat.of().withUpperCase();
        var hi = format.toHexDigits((short) (offset >>> 16));
        var lo = format.toHexDigits((short) (offset));
        return hi + ":" + lo;
    }

    void row(int index, RowSink row) {
        int offset = index * BYTES_PER_ROW;
        dumpBytes(offset, row);
        dumpAscii(offset, row);
    }

    private void dumpBytes(int offset, RowSink row) {
        for (int i = 0; i < BYTES_PER_ROW; i++) {
            if (i == BYTES_PER_ROW / 2) {
                row.append(" ");
            }
            if (offset + i < binary.length()) {
                row.hex(binary.getUnsigned(offset + i));
            } else {
                row.append("   ");
            }
        }
    }

    private void dumpAscii(int offset, RowSink row) {
        row.append(" |");
        for (int i = 0; i < BYTES_PER_ROW; i++) {
            if (offset + i < binary.length()) {
                row.ascii(binary.getUnsigned(offset + i));
            } else {
                row.append(" ");
            }
        }
        row.append("|");
    }

    int byteIndex(int row, int column, boolean roundUp) {
        int index = row * BYTES_PER_ROW + byteIndexRow(column, roundUp);
        return Math.clamp(index, 0, binary.length());
    }

    private int byteIndexRow(int column, boolean roundUp) {
        if (column < SEPARATOR_COLUMN - 1) {
            int bias = roundUp ? 2 : 1;
            int start = column < HEX_SPLIT_COLUMN ? 0 : 1;
            return Math.clamp((column - start + bias) / 3, 0, BYTES_PER_ROW);
        }
        if (column < ASCII_COLUMN) {
            return BYTES_PER_ROW;
        }
        return Math.clamp(column - ASCII_COLUMN, 0, BYTES_PER_ROW);
    }

    String hex(int from, int to) {
        return binary.slice(from, to - from)
            .toHexString(HexFormat.ofDelimiter(" "));
    }

    String raw(int from, int to) {
        return binary.slice(from, to - from)
            .toString(StandardCharsets.ISO_8859_1);
    }

    interface RowSink {
        void append(String text);

        default void hex(int value) {
            append(HEX[value]);
        }

        default void ascii(int value) {
            append(ALPHABET[value]);
        }
    }
}
