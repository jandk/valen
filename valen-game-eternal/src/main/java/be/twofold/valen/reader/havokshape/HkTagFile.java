package be.twofold.valen.reader.havokshape;

import java.nio.*;

public record HkTagFile() {

    public static HkTagFile read(ByteBuffer buffer) {
        var reader = new HkTagFileReader(buffer);
        return new HkTagFile();
    }
}
