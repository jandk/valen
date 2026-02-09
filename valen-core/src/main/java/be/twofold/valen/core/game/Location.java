package be.twofold.valen.core.game;

import java.nio.file.*;

/**
 * Represents a location where data is stored.
 */
public sealed interface Location {
    /**
     * Returns the size of the location.
     */
    int size();

    /**
     * Represents a slice of a file.
     *
     * @param path   The path of the file to read from.
     * @param offset The offset in the container file.
     * @param size   The size of the slice.
     */
    record FileSlice(
        Path path,
        long offset,
        int size
    ) implements Location {
    }

    /**
     * Represents a compressed file.
     *
     * @param base The base location.
     * @param type The compression type.
     * @param size The uncompressed size.
     */
    record Compressed(
        Location base,
        CompressionType type,
        int size
    ) implements Location {
    }

    /**
     * Extension point for custom storage locations.
     */
    non-sealed interface Custom extends Location {
    }
}
