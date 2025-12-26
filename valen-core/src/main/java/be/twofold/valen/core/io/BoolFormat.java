package be.twofold.valen.core.io;

/**
 * Defines the binary representation of a boolean value.
 * <p>
 * This enum specifies the width of the numeric type used to store a boolean
 * in a binary stream, where typically {@code 0} represents {@code false}
 * and {@code 1} represents {@code true}.
 */
public enum BoolFormat {
    /**
     * The boolean is stored as a single byte (8 bits).
     */
    BYTE,

    /**
     * The boolean is stored as a short integer (16 bits).
     */
    SHORT,

    /**
     * The boolean is stored as a standard integer (32 bits).
     */
    INT,
}
