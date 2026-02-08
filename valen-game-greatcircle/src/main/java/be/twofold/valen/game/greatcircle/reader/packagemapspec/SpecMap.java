package be.twofold.valen.game.greatcircle.reader.packagemapspec;

import com.google.gson.annotations.*;

import java.util.*;

public record SpecMap(
    @SerializedName("container_refs") List<Integer> containerRefs,
    @SerializedName("file_refs") List<Integer> fileRefs,
    @SerializedName("name") String name
) {
    public SpecMap {
        containerRefs = List.copyOf(containerRefs);
        fileRefs = List.copyOf(fileRefs);
        Objects.requireNonNull(name);
    }
}
