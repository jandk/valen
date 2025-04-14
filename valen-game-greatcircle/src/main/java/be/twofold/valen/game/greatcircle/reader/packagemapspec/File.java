package be.twofold.valen.game.greatcircle.reader.packagemapspec;

import com.google.gson.annotations.*;

import java.util.*;

public record File(
    @SerializedName("blake3") String blake3,
    @SerializedName("chunk_id") int chunkId,
    @SerializedName("hq") boolean hq,
    @SerializedName("id") int id,
    @SerializedName("include_in_image") boolean includeInImage,
    @SerializedName("lang") List<String> lang,
    @SerializedName("name") String name,
    @SerializedName("sha256") String sha256
) {
    public File {
        Objects.requireNonNull(blake3);
        lang = lang == null ? List.of() : List.copyOf(lang);
        Objects.requireNonNull(name);
        Objects.requireNonNull(sha256);
    }
}
