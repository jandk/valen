package be.twofold.valen.ui.component.rawview;

import be.twofold.valen.core.util.*;
import be.twofold.valen.core.util.collect.*;
import javafx.scene.paint.*;
import javafx.scene.text.*;

import java.nio.charset.*;
import java.util.*;

final class HexDump {
    private static final char[] ALPHABET = generateAlphabet(Charset.forName("windows-1252"));
    private static final char[] HEX = {
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
    };

    private final TextFlowBuilder builder;
    private final Bytes binary;
    private final Color primary;
    private final Color secondary;

    public HexDump(Bytes binary, Font font, Color primary, Color secondary) {
        this.builder = new TextFlowBuilder(font);
        this.binary = Check.notNull(binary, "binary");
        this.primary = primary;
        this.secondary = secondary;
    }

    private static char[] generateAlphabet(Charset charset) {
        var bytes = new byte[256];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) i;
        }

        char[] chars = new char[256];
        var decoded = new String(bytes, charset);
        for (int i = 0; i < decoded.length(); i++) {
            var ch = decoded.charAt(i);
            chars[i] = Character.getType(ch) != Character.CONTROL && ch != 0xFFFD ? ch : '.';
        }
        return chars;
    }

    public TextFlow hexdump(int index) {
        builder.clear();

        int offset = index * 16;
        toHex(offset, 8, primary);
        builder.append(' ');

        dumpBytes(offset);
        dumpAscii(offset);

        return builder.build();
    }

    private void dumpBytes(int offset) {
        for (int i = offset; i < offset + 16; i++) {
            if (i % 8 == 0) {
                builder.append(' ');
            }
            if (i < binary.length()) {
                int value = binary.getUnsignedByte(i);
                var color = value != 0 ? primary : secondary;
                toHex(value, 2, color);
            } else {
                builder.append(' ');
                builder.append(' ');
            }
            builder.append(' ');
        }
    }

    private void dumpAscii(int offset) {
        builder.append(' ');
        builder.append('|', primary);
        for (int i = offset; i < offset + 16; i++) {
            if (i < binary.length()) {
                int value = binary.getUnsignedByte(i);
                var color = value != 0 ? primary : secondary;
                builder.append(ALPHABET[value], color);
            } else {
                builder.append(' ');
            }
        }
        builder.append('|', primary);
    }

    private void toHex(int value, int length, Color color) {
        for (int i = length - 1, o = 0; i >= 0; i--, o++) {
            builder.append(HEX[(value >> (i * 4)) & 0xF], color);
        }
    }

    static final class TextFlowBuilder {
        private final List<Text> texts = new ArrayList<>();
        private final StringBuilder builder = new StringBuilder();
        private final Font font;
        private Color color = null;

        TextFlowBuilder(Font font) {
            this.font = font;
        }

        void append(char c) {
            append(c, color);
        }

        void append(char c, Color color) {
            checkColor(color);
            builder.append(c);
        }

        TextFlow build() {
            addNewText();
            return new TextFlow(texts.toArray(new Text[0]));
        }

        void clear() {
            texts.clear();
            builder.setLength(0);
        }

        private void checkColor(Color color) {
            if (this.color == null || !this.color.equals(color)) {
                if (this.color != null) {
                    addNewText();
                }
                this.color = color;
            }
        }

        private void addNewText() {
            var text = new Text(builder.toString());
            text.setFill(this.color);
            text.setFont(this.font);
            texts.add(text);
            builder.setLength(0);
        }
    }
}
