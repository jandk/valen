package be.twofold.valen.export.cast.mappers;

import be.twofold.tinycast.*;
import be.twofold.valen.core.export.*;
import be.twofold.valen.core.material.*;
import be.twofold.valen.core.texture.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;

public final class CastTextureMapper {
    private final Exporter<Texture> pngExporter = Exporter.forTypeAndId(Texture.class, "texture.png");
    private final Map<String, Long> textures = new ConcurrentHashMap<>();
    private final Path castPath;
    private final Path imagePath;

    public CastTextureMapper(Path castPath, Path imagePath) {
        this.castPath = Objects.requireNonNull(castPath);
        this.imagePath = Objects.requireNonNull(imagePath);
        pngExporter.setProperty("reconstructZ", true);
    }

    public Long map(TextureReference reference, CastNodes.Material material) throws IOException {
        var existingHash = textures.get(reference.filename());
        if (existingHash != null) {
            return existingHash;
        }

        var bytes = textureToPng(reference.supplier().get());
        var path = imagePath.resolve(reference.filename() + ".png");
        Files.write(path, bytes);

        var pathString = castPath.getParent().relativize(path).toString().replace('\\', '/');
        var hash = material.createFile()
            .setPath(pathString)
            .getHash();

        textures.put(reference.filename(), hash);
        return hash;
    }

    private byte[] textureToPng(Texture texture) throws IOException {
        try (var out = new ByteArrayOutputStream()) {
            pngExporter.export(texture, out);
            return out.toByteArray();
        }
    }
}
