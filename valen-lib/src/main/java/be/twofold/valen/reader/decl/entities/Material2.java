package be.twofold.valen.reader.decl.entities;

import com.google.gson.annotations.*;

import java.util.*;

public record Material2(
    @SerializedName("Passes")
    Map<MaterialPassType, MaterialPass> passes,
    @SerializedName("RenderLayers")
    List<RenderLayer> renderLayers
) {
}
