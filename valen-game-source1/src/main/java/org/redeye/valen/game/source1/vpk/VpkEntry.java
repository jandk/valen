package org.redeye.valen.game.source1.vpk;

public record VpkEntry(
    String name,
    int crc,
    short preloadBytes,
    short archiveIndex,
    int entryOffset,
    int entryLength
) {
}
