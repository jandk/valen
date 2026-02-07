package be.twofold.valen.core.game.io;

/**
 * Represents a location where data is stored.
 */
public sealed interface Location {
    /**
     * Represents a slice of a file.
     *
     * @param fileId The id of the file to read from.
     * @param offset The offset in the container file.
     * @param size   The size of the slice.
     */
    record FileSlice(
        FileId fileId,
        long offset,
        int size
    ) implements Location {
    }

    /**
     * Represents a compressed file.
     *
     * @param base             The base location.
     * @param type             The compression type.
     * @param uncompressedSize The uncompressed size.
     */
    record Compressed(
        Location base,
        CompressionType type,
        int uncompressedSize
    ) implements Location {
    }

    /**
     * Extension point for custom storage locations.
     */
    non-sealed interface Custom extends Location {
    }
}
