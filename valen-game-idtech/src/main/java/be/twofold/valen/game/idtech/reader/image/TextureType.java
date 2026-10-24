package be.twofold.valen.game.idtech.reader.image;

import wtf.reversed.toolbox.io.*;
import wtf.reversed.toolbox.util.*;

import java.io.*;

public enum TextureType implements ValueEnum<Integer> {
    TT_2D(0),
    TT_3D(1),
    TT_CUBIC(2),
    ;

    private final int value;

    TextureType(int value) {
        this.value = value;
    }

    public static TextureType read(BinarySource source) throws IOException {
        int value = source.readInt();
        return ValueEnum.fromValue(TextureType.class, value);
    }

    @Override
    public Integer value() {
        return value;
    }
}
