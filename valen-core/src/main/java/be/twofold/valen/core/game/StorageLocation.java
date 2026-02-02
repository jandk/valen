package be.twofold.valen.core.game;

/**
 * Represents a location where data is stored.
 */
public sealed interface StorageLocation {
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
    ) implements StorageLocation {
    }

    /**
     * Represents a compressed file.
     *
     * @param base             The base location.
     * @param compressionType  The compression type.
     * @param uncompressedSize The uncompressed size.
     */
    record Compressed(
        StorageLocation base,
        String compressionType,
        int uncompressedSize
    ) implements StorageLocation {
    }

    /**
     * Extension point for custom storage locations.
     */
    non-sealed interface Custom extends StorageLocation {
    }
}
