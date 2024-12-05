package be.twofold.valen.gltf;

import be.twofold.valen.gltf.glb.*;

import java.io.*;

public final class GlbWriter extends GltfCommon {
    public GlbWriter(GltfContext context) {
        super(context);
    }

    public void write(OutputStream out) throws IOException {
        var binSize = getContext().updateBufferViews(null);

        var rawJson = toRawJson();
        var jsonSize = GltfUtils.alignedLength(rawJson.length);

        var totalSize = GlbHeader.BYTES + GlbChunkHeader.BYTES + jsonSize + GlbChunkHeader.BYTES + binSize;

        out.write(new GlbHeader(totalSize).toBuffer().array());
        out.write(new GlbChunkHeader(jsonSize, GlbChunkType.JSON).toBuffer().array());
        out.write(rawJson);
        GltfUtils.align(out, rawJson.length, (byte) ' ');
        out.write(new GlbChunkHeader(binSize, GlbChunkType.BIN).toBuffer().array());
        writeBuffers(out);
    }
}
