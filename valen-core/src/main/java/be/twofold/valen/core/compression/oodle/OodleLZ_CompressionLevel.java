package be.twofold.valen.core.compression.oodle;

public enum OodleLZ_CompressionLevel {
    /**
     * don't compress, just copy raw bytes
     */
    None(0),
    /**
     * super fast mode, lower compression ratio
     */
    SuperFast(1),
    /**
     * fastest LZ mode with still decent compression ratio
     */
    VeryFast(2),
    /**
     * fast - good for daily use
     */
    Fast(3),
    /**
     * standard medium speed LZ mode
     */
    Normal(4),

    /**
     * optimal parse level 1 (faster optimal encoder)
     */
    Optimal1(5),
    /**
     * optimal parse level 2 (recommended baseline optimal encoder)
     */
    Optimal2(6),
    /**
     * optimal parse level 3 (slower optimal encoder)
     */
    Optimal3(7),
    /**
     * optimal parse level 4 (very slow optimal encoder)
     */
    Optimal4(8),
    /**
     * optimal parse level 5 (don't care about encode speed, maximum compression)
     */
    Optimal5(9),

    /**
     * faster than SuperFast, less compression
     */
    HyperFast1(-1),
    /**
     * faster than HyperFast1, less compression
     */
    HyperFast2(-2),
    /**
     * faster than HyperFast2, less compression
     */
    HyperFast3(-3),
    /**
     * fastest, less compression
     */
    HyperFast4(-4);

    private final int value;

    OodleLZ_CompressionLevel(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }
}
