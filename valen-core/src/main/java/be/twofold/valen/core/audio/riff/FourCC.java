package be.twofold.valen.core.audio.riff;

import be.twofold.valen.core.util.*;
import wtf.reversed.toolbox.io.*;
import wtf.reversed.toolbox.util.*;

import java.io.*;
import java.nio.charset.*;

public record FourCC(int value) {
    public FourCC {
        for (int i = 0; i < 4; i++) {
            Check.argument(Ascii.isPrint(value >>> (i * 8) & 0xFF),
                "FourCC must only contain printable ASCII characters");
        }
    }

    public static FourCC read(BinaryReader reader) throws IOException {
        return new FourCC(reader.readInt());
    }

    public static FourCC parse(String s) {
        Check.argument(s.length() == 4, "FourCC must be 4 characters long");

        int result = 0;
        for (int i = 0; i < 4; i++) {
            char c = s.charAt(i);
            Check.argument(Ascii.isPrint(c), "FourCC must only contain printable ASCII characters");
            result = result >>> 8 | c << 24;
        }
        return new FourCC(result);
    }

    public void write(OutputStream out) throws IOException {
        for (int i = 0; i < 4; i++) {
            out.write((byte) (value >>> (i * 8)));
        }
    }

    @Override
    public String toString() {
        byte[] chars = new byte[4];
        for (int i = 0; i < 4; i++) {
            chars[i] = (byte) (value >>> (i * 8));
        }
        return new String(chars, StandardCharsets.ISO_8859_1);
    }
}
