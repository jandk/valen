package be.twofold.valen.game.qc.reader.pct;

import wtf.reversed.toolbox.io.*;
import wtf.reversed.toolbox.util.*;

import java.io.*;

public enum PctTagType implements ValueEnum<Short> {
    /**
     * zero-length, sits at EOF
     */
    TERMINATOR(0x0001),
    /**
     * u32 'PICT' (bytes "TCIP")
     */
    MAGIC(0x00F0),
    /**
     * u32 pixel format enum
     */
    FORMAT(0x00F2),
    /**
     * u32; 1 = 2D, 6 = cube map
     */
    MIP_COUNT(0x00F9),
    /**
     * bulk pixel data, all faces back to back
     */
    PIXELS(0x00FF),
    /**
     * u32 width, height, depth, mipCount
     */
    DIMENSIONS(0x0102),
    /**
     * mipCount x { u32 offset, u32 size }, for ONE face
     */
    MIP_TABLE(0x0107),
    ;

    private final short value;

    PctTagType(int value) {
        this.value = (short) value;
    }

    public static PctTagType read(BinarySource source) throws IOException {
        return ValueEnum.fromValue(PctTagType.class, source.readShort());
    }

    @Override
    public Short value() {
        return value;
    }
}
