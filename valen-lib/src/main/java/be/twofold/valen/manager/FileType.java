package be.twofold.valen.manager;

import be.twofold.valen.core.animation.*;
import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.texture.*;
import be.twofold.valen.reader.compfile.entities.*;
import be.twofold.valen.reader.staticinstances.*;
import be.twofold.valen.resource.*;

public record FileType<T>(
    Class<T> instanceType,
    ResourceType resourceType
) {
    public static final FileType<Texture> Image = new FileType<>(Texture.class, ResourceType.Image);
    public static final FileType<Model> StaticModel = new FileType<>(Model.class, ResourceType.Model);
    public static final FileType<Model> AnimatedModel = new FileType<>(Model.class, ResourceType.BaseModel);
    public static final FileType<StaticInstances> StaticInstances = new FileType<>(StaticInstances.class, ResourceType.StaticInstances);
    public static final FileType<EntityFile> Entities = new FileType<>(EntityFile.class, ResourceType.CompFile);
    public static final FileType<Skeleton> Skeleton = new FileType<>(Skeleton.class, ResourceType.Skeleton);
    public static final FileType<Animation> Animation = new FileType<>(Animation.class, ResourceType.Anim);
    public static final FileType<byte[]> BinaryFile = new FileType<>(byte[].class, ResourceType.BinaryFile);
    public static final FileType<byte[]> CompFile = new FileType<>(byte[].class, ResourceType.CompFile);
}
