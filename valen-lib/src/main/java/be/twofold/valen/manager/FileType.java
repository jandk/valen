package be.twofold.valen.manager;

import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.texture.*;
import be.twofold.valen.resource.*;

public record FileType<T>(
    Class<T> instanceType,
    ResourceType resourceType
) {
    public static final FileType<Texture> Image = new FileType<>(Texture.class, ResourceType.Image);
    public static final FileType<Model> StaticModel = new FileType<>(Model.class, ResourceType.Model);
    public static final FileType<Model> AnimatedModel = new FileType<>(Model.class, ResourceType.BaseModel);
}
