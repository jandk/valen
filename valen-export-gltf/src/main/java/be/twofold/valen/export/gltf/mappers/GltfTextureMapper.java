package be.twofold.valen.export.gltf.mappers;

import be.twofold.valen.core.material.*;
import be.twofold.valen.core.texture.*;
import be.twofold.valen.export.png.*;
import be.twofold.valen.gltf.*;
import be.twofold.valen.gltf.model.image.*;
import be.twofold.valen.gltf.model.texture.*;

import java.io.*;
import java.nio.*;
import java.util.*;

public final class GltfTextureMapper {
    private final PngExporter pngExporter = new PngExporter();
    private final Map<String, TextureID> textures = new HashMap<>();

    private final GltfContext context;

    public GltfTextureMapper(GltfContext context) {
        this.context = context;
    }

    public TextureID map(TextureReference reference) throws IOException {
        var existingSchema = textures.get(reference.filename());
        if (existingSchema != null) {
            return existingSchema;
        }

        var texture = reference.supplier().get();
        var buffer = textureToPng(texture);
        var bufferViewID = context.createBufferView(buffer);

        var imageSchema = ImageSchema.builder()
            .name(reference.filename())
            .mimeType(ImageMimeType.IMAGE_PNG)
            .bufferView(bufferViewID)
            .build();
        var imageID = context.addImage(imageSchema);

        var textureSchema = TextureSchema.builder()
            .name(reference.filename())
            .source(imageID)
            .build();
        var textureID = context.addTexture(textureSchema);

        textures.put(reference.filename(), textureID);

        return textureID;
    }

    private ByteBuffer textureToPng(Texture texture) throws IOException {
        try (var out = new ByteArrayOutputStream()) {
            pngExporter.export(texture, out);
            return ByteBuffer.wrap(out.toByteArray());
        }
    }
}
