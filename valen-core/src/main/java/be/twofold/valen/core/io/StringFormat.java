package be.twofold.valen.core.io;

/**
 * Defines the strategy used to encode or decode strings within a binary stream.
 * <p>
 * This enum is typically used by {@link BinaryReader} to determine how to identify
 * the length or boundaries of a string during read operations.
 */
public enum StringFormat {
    /**
     * The string is preceded by a single unsigned byte indicating its length.
     */
    BYTE_LENGTH,

    /**
     * The string is preceded by an unsigned short (2 bytes) indicating its length.
     */
    SHORT_LENGTH,

    /**
     * The string is preceded by a signed integer (4 bytes) indicating its length.
     */
    INT_LENGTH,

    /**
     * The string length is not specified; it is read until a null terminator ({@code \0}) is encountered.
     */
    NULL_TERM,
}
