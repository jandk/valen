package be.twofold.valen.export.gltf.mappers;

import be.twofold.valen.core.material.*;
import be.twofold.valen.core.texture.*;
import be.twofold.valen.core.texture.op.*;
import be.twofold.valen.core.util.fi.*;
import be.twofold.valen.gltf.*;
import be.twofold.valen.gltf.model.extension.*;
import be.twofold.valen.gltf.model.material.*;
import be.twofold.valen.gltf.model.texture.*;
import org.slf4j.*;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;
import java.util.stream.*;

public final class GltfMaterialMapper {
    private static final Logger log = LoggerFactory.getLogger(GltfMaterialMapper.class);

    private final Map<String, MaterialID> materials = new HashMap<>();

    private final GltfContext context;
    private final GltfTextureMapper textureMapper;

    public GltfMaterialMapper(GltfContext context) {
        this.context = context;
        this.textureMapper = new GltfTextureMapper(context);
    }

    public MaterialID map(Material material) throws IOException {
        var existingMaterialID = materials.get(material.name());
        if (existingMaterialID != null) {
            return existingMaterialID;
        }

        var textures = material.textures().stream()
            .collect(Collectors.groupingBy(TextureReference::type));

        var builder = MaterialSchema.builder().name(material.name());

        // Either spec-smooth or metal-rough
        boolean specSmooth = false;
        if (textures.containsKey(TextureType.Specular) || textures.containsKey(TextureType.Smoothness)) {
            var diffuse = textures.get(TextureType.Albedo).getFirst();
            var specular = textures.get(TextureType.Specular).getFirst();
            var smoothness = textures.get(TextureType.Smoothness).getFirst();
            var specularGlossiness = mapSpecularGlossiness(specular, smoothness, material.name());

            var specularGlossinessSchema = KHRMaterialsPBRSpecularGlossinessSchema.builder()
                .diffuseTexture(textureSchema(textureMapper.map(diffuse)))
                .specularGlossinessTexture(textureSchema(textureMapper.map(specularGlossiness)))
                .build();

            builder.putExtensions(specularGlossinessSchema.getName(), specularGlossinessSchema);
            specSmooth = true;
        }

        var pbrBuilder = PbrMetallicRoughnessSchema.builder();
        for (var reference : material.textures()) {
            switch (reference.type()) {
                case Albedo -> pbrBuilder.baseColorTexture(textureSchema(textureMapper.map(reference)));
                case Emissive -> builder.emissiveTexture(textureSchema(textureMapper.map(reference)));
                case Normal -> builder.normalTexture(normalTextureInfoSchema(textureMapper.map(reference)));
            }
        }

        if (!specSmooth) {
            builder.pbrMetallicRoughness(pbrBuilder.build());
        }

        var materialSchema = builder.build();
        var materialID = context.addMaterial(materialSchema);
        materials.put(material.name(), materialID);
        return materialID;
    }

    private TextureReference mapSpecularGlossiness(TextureReference specular, TextureReference glossiness, String name) {
        ThrowingSupplier<Texture, IOException> supplier = () -> {
            Surface specularSurface = specular != null ? specular.supplier().get().surfaces().getFirst() : null;
            Surface glossinessSurface = glossiness != null ? glossiness.supplier().get().surfaces().getFirst() : null;

            final int width = Math.max(specularSurface == null ? 0 : specularSurface.width(), glossinessSurface == null ? 0 : glossinessSurface.width());
            final int height = Math.max(specularSurface == null ? 0 : specularSurface.height(), glossinessSurface == null ? 0 : glossinessSurface.height());

            U8PixelOp specularOp;
            if (specularSurface != null) {
                if (specularSurface.width() != width || specularSurface.height() != height) {
                    specularSurface = rescale(specularSurface, width, height);
                }

                specularOp = PixelOp.source(specularSurface).asU8().rgb();
            } else {
                specularOp = U8ChannelOp.constant(255).rgba();
            }

            U8ChannelOp glossinessOp;
            if (glossinessSurface != null) {
                if (glossinessSurface.width() != width || glossinessSurface.height() != height) {
                    glossinessSurface = rescale(glossinessSurface, width, height);
                }

                glossinessOp = PixelOp.source(glossinessSurface).asU8().red();
            } else {
                glossinessOp = U8ChannelOp.constant(255);
            }

            Surface surface = U8PixelOp.combine(specularOp, glossinessOp)
                .toSurface(width, height, TextureFormat.R8G8B8A8_UNORM);

            return Texture.fromSurface(surface);
        };

        return new TextureReference(name + "_sg", TextureType.Unknown, supplier);
    }

    private static Surface rescale(Surface source, int width, int height) {
        var image = new BufferedImage(source.width(), source.height(), BufferedImage.TYPE_4BYTE_ABGR_PRE);
        byte[] rawImage = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        PixelOp.source(source).asU8().toPixels(source.width(), source.height(), rawImage);

        Surface result = Surface.create(width, height, TextureFormat.R8G8B8A8_UNORM);
        var scaled = rescale(image, width, height);
        var scaledRaw = ((DataBufferByte) scaled.getRaster().getDataBuffer()).getData();
        System.arraycopy(scaledRaw, 0, result.data(), 0, result.data().length);

        return result;
    }

    private static BufferedImage rescale(BufferedImage source, int width, int height) {
        var result = new BufferedImage(width, height, source.getType());

        Graphics2D g2d = result.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.drawImage(source, 0, 0, width, height, null);
        g2d.dispose();

        return result;
    }

    private static TextureInfoSchema textureSchema(TextureID textureID) {
        return TextureInfoSchema.builder()
            .index(textureID)
            .build();
    }

    private static NormalTextureInfoSchema normalTextureInfoSchema(TextureID textureID) {
        return NormalTextureInfoSchema.builder()
            .index(textureID)
            .build();
    }
}
