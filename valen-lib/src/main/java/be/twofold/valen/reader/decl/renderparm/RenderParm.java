package be.twofold.valen.reader.decl.renderparm;

import be.twofold.valen.core.math.*;
import be.twofold.valen.reader.decl.renderparm.enums.*;

import java.util.*;

public sealed interface RenderParm {
    record AccelerationStructure() implements RenderParm {
    }

    record Bool(boolean value) implements RenderParm {
    }

    record ColorLut(String value) implements RenderParm {
    }

    record F32(float value) implements RenderParm {
    }

    record F32Vec2(Vector2 v) implements RenderParm {
    }

    record F32Vec3(Vector3 v) implements RenderParm {
    }

    record F32Vec4(Vector4 v) implements RenderParm {
    }

    record I32(int value) implements RenderParm {
    }

    record ImageBuffer(ImageBufferFormat format) implements RenderParm {
    }

    record Program(String name) implements RenderParm {
    }

    record Sampler(String name) implements RenderParm {
    }

    record StorageBuffer(Set<BufferViewFlag> flags, String type) implements RenderParm {
    }

    record Str(String value) implements RenderParm {
    }

    record TexelBuffer(ImageBufferFormat format) implements RenderParm {
    }

    record Texture(ImageProperties props, String name) implements RenderParm {
    }

    record Type(String type) implements RenderParm {
    }

    record UniformBuffer(int value) implements RenderParm {
    }
}
