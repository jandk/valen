package be.twofold.valen.game.eternal.reader.mapfilestaticinstances;

import be.twofold.valen.core.io.*;

import java.io.*;

public record MapFileStaticInstancesDeclExtra(
    float unknown00,
    float unknown04,
    float unknown08,
    float unknown12,
    float unknown16,
    float unknown20,
    float unknown24,
    float unknown28,
    float unknown32,
    float unknown36,
    float unknown40,
    float unknown44,
    float unknown48,
    float unknown52,
    float unknown56,
    float unknown60,
    float unknown64,
    float unknown68,
    int unknown72,
    float unknown76,
    float unknown80,
    float unknown84,
    float unknown88,
    byte unknown92,
    byte unknown93,
    short unknown94
) {
    public static MapFileStaticInstancesDeclExtra read(BinaryReader reader) throws IOException {
        float unknown00 = reader.readFloat();
        float unknown04 = reader.readFloat();
        float unknown08 = reader.readFloat();
        float unknown12 = reader.readFloat();
        float unknown16 = reader.readFloat();
        float unknown20 = reader.readFloat();
        float unknown24 = reader.readFloat();
        float unknown28 = reader.readFloat();
        float unknown32 = reader.readFloat();
        float unknown36 = reader.readFloat();
        float unknown40 = reader.readFloat();
        float unknown44 = reader.readFloat();
        float unknown48 = reader.readFloat();
        float unknown52 = reader.readFloat();
        float unknown56 = reader.readFloat();
        float unknown60 = reader.readFloat();
        float unknown64 = reader.readFloat();
        float unknown68 = reader.readFloat();
        int unknown72 = reader.readInt();
        float unknown76 = reader.readFloat();
        float unknown80 = reader.readFloat();
        float unknown84 = reader.readFloat();
        float unknown88 = reader.readFloat();
        byte unknown92 = reader.readByte();
        byte unknown93 = reader.readByte();
        short unknown94 = reader.readShort();

        return new MapFileStaticInstancesDeclExtra(
            unknown00,
            unknown04,
            unknown08,
            unknown12,
            unknown16,
            unknown20,
            unknown24,
            unknown28,
            unknown32,
            unknown36,
            unknown40,
            unknown44,
            unknown48,
            unknown52,
            unknown56,
            unknown60,
            unknown64,
            unknown68,
            unknown72,
            unknown76,
            unknown80,
            unknown84,
            unknown88,
            unknown92,
            unknown93,
            unknown94
        );
    }
}
