package be.twofold.valen.export.gltf.mappers;

import be.twofold.valen.core.material.*;
import be.twofold.valen.core.texture.*;
import be.twofold.valen.export.png.*;
import be.twofold.valen.gltf.*;
import be.twofold.valen.gltf.model.image.*;
import be.twofold.valen.gltf.model.texture.*;

import java.io.*;
import java.nio.*;

public final class GltfTextureMapper {
    private static final PngExporter EXPORTER = new PngExporter();

    private final GltfContext context;

    public GltfTextureMapper(GltfContext context) {
        this.context = context;
    }

    public TextureSchema map(TextureReference reference) throws IOException {
        var texture = reference.supplier().get();
        var buffer = textureToPng(texture);
        var bufferView = context.createBufferView(buffer);

        var image = ImageSchema.builder()
            .name(reference.filename())
            .mimeType(ImageMimeType.IMAGE_PNG)
            .bufferView(bufferView)
            .build();
        var imageID = context.addImage(image);

        return TextureSchema.builder()
            .name(reference.filename())
            .source(imageID)
            .build();
    }

    private ByteBuffer textureToPng(Texture texture) throws IOException {
        try (var out = new ByteArrayOutputStream()) {
            EXPORTER.export(texture, out);
            return ByteBuffer.wrap(out.toByteArray());
        }
    }
}
