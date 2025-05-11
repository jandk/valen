package be.twofold.valen.game.greatcircle.reader.packagemapspec;

import com.google.gson.annotations.*;

import java.util.*;

public record Container(
    @SerializedName("basename") String basename,
    @SerializedName("chunk_id") int chunkId,
    @SerializedName("debug_name") String debugName,
    @SerializedName("file_ref") int fileRef,
    @SerializedName("id") int id,
    @SerializedName("patch_index") int patchIndex,
    @SerializedName("type") String type
) {
    public Container {
        Objects.requireNonNull(basename);
        Objects.requireNonNull(debugName);
        Objects.requireNonNull(type);
    }
}
