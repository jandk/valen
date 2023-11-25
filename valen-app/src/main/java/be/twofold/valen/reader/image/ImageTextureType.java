package be.twofold.valen.reader.image;

import java.util.*;

public enum ImageTextureType {
    TT_2D(0x00),
    TT_3D(0x01),
    TT_CUBIC(0x02);

    private final int code;

    ImageTextureType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static ImageTextureType fromCode(int code) {
        return Arrays.stream(values())
            .filter(value -> value.code == code)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Unknown texture type: " + code));
    }
}
