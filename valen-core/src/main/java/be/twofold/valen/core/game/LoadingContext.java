package be.twofold.valen.core.game;

import wtf.reversed.toolbox.collect.*;

import java.io.*;

/**
 * Interface passed to {@link AssetReader}s to load assets.
 */
public interface LoadingContext {

    /**
     * Checks whether an asset with the given ID exists.
     *
     * @param id The asset ID.
     * @return True if the asset exists, false otherwise.
     */
    boolean exists(AssetID id);

    /**
     * Loads a dependent asset.
     *
     * @param id    The asset ID.
     * @param clazz The class of the asset to load.
     * @param <T>   The type of the asset to load.
     * @return The loaded asset.
     * @throws IOException If an I/O error occurs.
     */
    <T> T load(AssetID id, Class<T> clazz) throws IOException;

    /**
     * Opens a storage location for reading.
     *
     * @param location The storage location to open.
     * @return A binary source for reading the location.
     * @throws IOException If an I/O error occurs.
     */
    Bytes open(StorageLocation location) throws IOException;

}
