package be.twofold.valen.export.gltf.mappers;

import be.twofold.valen.core.export.*;
import be.twofold.valen.core.material.*;
import be.twofold.valen.core.texture.*;
import be.twofold.valen.format.gltf.*;
import be.twofold.valen.format.gltf.model.image.*;
import be.twofold.valen.format.gltf.model.texture.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.nio.*;
import java.util.*;
import java.util.concurrent.*;

public final class GltfTextureMapper {
    private final Exporter<Texture> pngExporter = Exporters.forTypeAndId(Texture.class, "texture.png");
    private final Map<String, TextureID> textures = new ConcurrentHashMap<>();
    private final GltfContext context;

    public GltfTextureMapper(GltfContext context) {
        this.context = context;
        pngExporter.setProperty("reconstructZ", true);
    }

    public TextureID map(TextureReference reference) throws IOException {
        var existingTextureID = textures.get(reference.filename());
        if (existingTextureID != null) {
            return existingTextureID;
        }

        var texture = reference.supplier().get();
        var imageID = context.createImage(
            textureToPng(texture),
            reference.name(),
            reference.filename(),
            ImageMimeType.IMAGE_PNG
        );

        var textureSchema = ImmutableTexture.builder()
            .name(reference.name())
            .source(imageID)
            .build();
        var textureID = context.addTexture(textureSchema);

        textures.put(reference.filename(), textureID);
        return textureID;
    }

    private ByteBuffer textureToPng(Texture texture) throws IOException {
        try (var writer = new BytesBinaryWriter()) {
            pngExporter.export(texture, writer);
            // TODO: Fix duplicate copy, but GLTF needs work first
            return ByteBuffer.wrap(writer.toBytes().toArray());
        }
    }
}
