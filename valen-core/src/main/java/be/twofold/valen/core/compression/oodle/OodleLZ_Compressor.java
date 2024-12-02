package be.twofold.valen.core.compression.oodle;

public enum OodleLZ_Compressor {
    Invalid(-1),
    /**
     * None = memcpy, pass through uncompressed bytes
     */
    None(3),

    /**
     * Fast decompression and high compression ratios, amazing!
     */
    Kraken(8),
    /**
     * Leviathan = Kraken's big brother with higher compression, slightly slower decompression.
     */
    Leviathan(13),
    /**
     * Mermaid is between Kraken & Selkie - crazy fast, still decent compression.
     */
    Mermaid(9),
    /**
     * Selkie is a super-fast relative of Mermaid. For maximum decode speed.
     */
    Selkie(11),
    /**
     * Hydra, the many-headed beast = Leviathan, Kraken, Mermaid, or Selkie (see $OodleLZ_About_Hydra)
     */
    Hydra(12),

    /**
     * no longer supported as of Oodle 2.9.0
     */
    @Deprecated
    BitKnit(10),
    /**
     * DEPRECATED but still supported
     */
    @Deprecated
    LZB16(4),
    /**
     * no longer supported as of Oodle 2.9.0
     */
    @Deprecated
    LZNA(7),
    /**
     * no longer supported as of Oodle 2.9.0
     */
    @Deprecated
    LZH(0),
    /**
     * no longer supported as of Oodle 2.9.0
     */
    @Deprecated
    LZHLW(1),
    /**
     * no longer supported as of Oodle 2.9.0
     */
    @Deprecated
    LZNIB(2),
    /**
     * no longer supported as of Oodle 2.9.0
     */
    @Deprecated
    LZBLW(5),
    /**
     * no longer supported as of Oodle 2.9.0
     */
    @Deprecated
    LZA(6);

    private final int value;

    OodleLZ_Compressor(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }
}
