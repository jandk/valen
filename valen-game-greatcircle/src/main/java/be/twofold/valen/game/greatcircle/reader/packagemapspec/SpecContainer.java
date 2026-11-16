package be.twofold.valen.game.greatcircle.reader.packagemapspec;

import com.google.gson.annotations.*;
import wtf.reversed.toolbox.util.*;

public record SpecContainer(
    @SerializedName("basename") String basename,
    @SerializedName("chunk_id") int chunkId,
    @SerializedName("debug_name") String debugName,
    @SerializedName("file_ref") int fileRef,
    @SerializedName("id") int id,
    @SerializedName("patch_index") int patchIndex,
    @SerializedName("type") String type
) {
    public SpecContainer {
        Check.nonNull(basename, "basename");
        Check.nonNull(debugName, "debugName");
        Check.nonNull(type, "type");
    }
}
