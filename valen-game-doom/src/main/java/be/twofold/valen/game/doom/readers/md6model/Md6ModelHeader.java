package be.twofold.valen.game.doom.readers.md6model;

import be.twofold.valen.game.doom.readers.*;
import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;
import wtf.reversed.toolbox.math.*;

import java.io.*;
import java.time.*;
import java.util.*;

public record Md6ModelHeader(
    Instant timestamp,
    Instant skelTimestamp,
    String skeleton,
    Vector3 minBoundsExpansion,
    Vector3 maxBoundsExpansion,
    boolean remapForSkinning,
    String morphSkinName,
    Bytes jointRemap,
    Bounds defaultBounds,
    List<String> blendShapeNames,
    Vector3 morphMapBias,
    Vector3 morphMapCompressionScale,
    Vector3 morphMapDecompressionScale
) {
    public static Md6ModelHeader read(BinaryReader reader) throws IOException {
        reader.expectInt(Md6Model.MAGIC);
        var timestamp = Instant.ofEpochSecond(reader.readInt());
        var skelTimestamp = Instant.ofEpochSecond(reader.readInt());
        var skeleton = IdStrings.read(reader);
        var minBoundsExpansion = Vector3.read(reader);
        var maxBoundsExpansion = Vector3.read(reader);
        var remapForSkinning = reader.readBool(BoolFormat.BYTE);
        var morphSkinName = IdStrings.read(reader);
        var jointRemap = reader.readBytes(reader.readUnsignedShort());
        var defaultBounds = Bounds.read(reader);
        var blendShapeNames = IdStrings.read(reader, reader.readInt());
        var morphMapBias = Vector3.read(reader);
        var morphMapCompressionScale = Vector3.read(reader);
        var morphMapDecompressionScale = Vector3.read(reader);

        return new Md6ModelHeader(
            timestamp,
            skelTimestamp,
            skeleton,
            minBoundsExpansion,
            maxBoundsExpansion,
            remapForSkinning,
            morphSkinName,
            jointRemap,
            defaultBounds,
            blendShapeNames,
            morphMapBias,
            morphMapCompressionScale,
            morphMapDecompressionScale
        );
    }
}
