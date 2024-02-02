package be.twofold.valen.reader.staticinstances;

import be.twofold.valen.core.util.*;

public record StaticInstanceDeclExtra(
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
    public static StaticInstanceDeclExtra read(BetterBuffer buffer) {
        float unknown00 = buffer.getFloat();
        float unknown04 = buffer.getFloat();
        float unknown08 = buffer.getFloat();
        float unknown12 = buffer.getFloat();
        float unknown16 = buffer.getFloat();
        float unknown20 = buffer.getFloat();
        float unknown24 = buffer.getFloat();
        float unknown28 = buffer.getFloat();
        float unknown32 = buffer.getFloat();
        float unknown36 = buffer.getFloat();
        float unknown40 = buffer.getFloat();
        float unknown44 = buffer.getFloat();
        float unknown48 = buffer.getFloat();
        float unknown52 = buffer.getFloat();
        float unknown56 = buffer.getFloat();
        float unknown60 = buffer.getFloat();
        float unknown64 = buffer.getFloat();
        float unknown68 = buffer.getFloat();
        int unknown72 = buffer.getInt();
        float unknown76 = buffer.getFloat();
        float unknown80 = buffer.getFloat();
        float unknown84 = buffer.getFloat();
        float unknown88 = buffer.getFloat();
        byte unknown92 = buffer.getByte();
        byte unknown93 = buffer.getByte();
        short unknown94 = buffer.getShort();

        return new StaticInstanceDeclExtra(
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
