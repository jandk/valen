package be.twofold.valen.game.eternal.manager;

import be.twofold.valen.core.animation.*;
import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.material.*;
import be.twofold.valen.core.texture.*;
import be.twofold.valen.game.eternal.reader.decl.renderparm.*;
import be.twofold.valen.game.eternal.reader.filecompressed.entities.*;
import be.twofold.valen.game.eternal.resource.*;
import com.google.gson.*;

public record FileType<T>(
    Class<T> instanceType,
    ResourceType resourceType
) {
    public static final FileType<Texture> Image = new FileType<>(Texture.class, ResourceType.Image);
    public static final FileType<Model> StaticModel = new FileType<>(Model.class, ResourceType.Model);
    public static final FileType<Model> AnimatedModel = new FileType<>(Model.class, ResourceType.BaseModel);
    public static final FileType<Skeleton> Skeleton = new FileType<>(Skeleton.class, ResourceType.Skeleton);
    public static final FileType<Animation> Animation = new FileType<>(Animation.class, ResourceType.Anim);
    public static final FileType<byte[]> BinaryFile = new FileType<>(byte[].class, ResourceType.BinaryFile);
    public static final FileType<byte[]> CompFile = new FileType<>(byte[].class, ResourceType.CompFile);
    public static final FileType<Material> Material = new FileType<>(Material.class, ResourceType.RsStreamFile);
    public static final FileType<RenderParm> RenderParam = new FileType<>(RenderParm.class, ResourceType.RsStreamFile);
    public static final FileType<EntityFile> Entities = new FileType<>(EntityFile.class, ResourceType.CompFile);
    public static final FileType<JsonObject> Declaration = new FileType<>(JsonObject.class, ResourceType.RsStreamFile);
}
