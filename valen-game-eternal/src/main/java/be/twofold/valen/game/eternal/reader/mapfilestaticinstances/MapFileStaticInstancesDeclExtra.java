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
    public static MapFileStaticInstancesDeclExtra read(DataSource source) throws IOException {
        float unknown00 = source.readFloat();
        float unknown04 = source.readFloat();
        float unknown08 = source.readFloat();
        float unknown12 = source.readFloat();
        float unknown16 = source.readFloat();
        float unknown20 = source.readFloat();
        float unknown24 = source.readFloat();
        float unknown28 = source.readFloat();
        float unknown32 = source.readFloat();
        float unknown36 = source.readFloat();
        float unknown40 = source.readFloat();
        float unknown44 = source.readFloat();
        float unknown48 = source.readFloat();
        float unknown52 = source.readFloat();
        float unknown56 = source.readFloat();
        float unknown60 = source.readFloat();
        float unknown64 = source.readFloat();
        float unknown68 = source.readFloat();
        int unknown72 = source.readInt();
        float unknown76 = source.readFloat();
        float unknown80 = source.readFloat();
        float unknown84 = source.readFloat();
        float unknown88 = source.readFloat();
        byte unknown92 = source.readByte();
        byte unknown93 = source.readByte();
        short unknown94 = source.readShort();

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
