package be.twofold.valen.game.eternal.reader.havokshape;

import java.nio.*;

public record HkTag(
    int flagsAndSize,
    HkTagType type,
    int position
) {
    public static HkTag read(ByteBuffer buffer) {
        var flagsAndSize = buffer.getInt();
        var type = HkTagType.from(buffer.getInt());
        var position = buffer.position();

        return new HkTag(flagsAndSize, type, position);
    }

    public int size() {
        return flagsAndSize & 0x3fffffff;
    }

    public int sizeWithoutHeader() {
        return size() - 8;
    }

    public int endPosition() {
        return position() + sizeWithoutHeader();
    }

    public int flags() {
        return flagsAndSize >>> 30;
    }
}
