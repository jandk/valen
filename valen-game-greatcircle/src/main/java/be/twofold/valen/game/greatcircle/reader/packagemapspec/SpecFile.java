package be.twofold.valen.game.greatcircle.reader.packagemapspec;

import com.google.gson.annotations.*;
import wtf.reversed.toolbox.util.*;

import java.util.*;

public record SpecFile(
    @SerializedName("blake3") String blake3,
    @SerializedName("chunk_id") int chunkId,
    @SerializedName("hq") boolean hq,
    @SerializedName("id") int id,
    @SerializedName("include_in_image") boolean includeInImage,
    @SerializedName("lang") List<String> lang,
    @SerializedName("name") String name,
    @SerializedName("sha256") String sha256
) {
    public SpecFile {
        Check.nonNull(blake3, "blake3");
        lang = lang == null ? List.of() : List.copyOf(lang);
        Check.nonNull(name, "name");
        Check.nonNull(sha256, "sha256");
    }
}
