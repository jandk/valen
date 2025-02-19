package be.twofold.valen.game.greatcircle.reader.packagemapspec;

import com.google.gson.annotations.*;

import java.util.*;

public record Map(
    @SerializedName("container_refs") List<Integer> containerRefs,
    @SerializedName("file_refs") List<Integer> fileRefs,
    @SerializedName("name") String name
) {
    public Map {
        containerRefs = List.copyOf(containerRefs);
        fileRefs = List.copyOf(fileRefs);
        Objects.requireNonNull(name);
    }
}
