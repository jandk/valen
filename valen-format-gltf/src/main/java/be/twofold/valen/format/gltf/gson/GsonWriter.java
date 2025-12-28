package be.twofold.valen.format.gltf.gson;

import be.twofold.valen.format.gltf.*;
import be.twofold.valen.format.gltf.model.*;
import be.twofold.valen.format.gltf.types.*;
import com.google.gson.*;

import java.io.*;
import java.util.*;

public final class GsonWriter implements JsonWriter {
    private static final Gson GSON = new GsonBuilder()
        .registerTypeHierarchyAdapter(GltfID.class, new GltfIDTypeAdapter().nullSafe())
        .registerTypeHierarchyAdapter(SerializableEnum.class, new SerializableEnumTypeAdapter().nullSafe())
        .registerTypeHierarchyAdapter(Collection.class, new CollectionSerializer())
        .registerTypeHierarchyAdapter(Map.class, new MapSerializer())
        .registerTypeAdapter(Mat2.class, new Mat2TypeAdapter().nullSafe())
        .registerTypeAdapter(Mat3.class, new Mat3TypeAdapter().nullSafe())
        .registerTypeAdapter(Mat4.class, new Mat4TypeAdapter().nullSafe())
        .registerTypeAdapter(Scalar.class, new ScalarTypeAdapter().nullSafe())
        .registerTypeAdapter(Vec2.class, new Vec2TypeAdapter().nullSafe())
        .registerTypeAdapter(Vec3.class, new Vec3TypeAdapter().nullSafe())
        .registerTypeAdapter(Vec4.class, new Vec4TypeAdapter().nullSafe())
        .disableHtmlEscaping()
        .create();

    private static final Gson GSON_PRETTY = GSON.newBuilder()
        .setPrettyPrinting()
        .create();

    @Override
    public void writeJson(GltfSchema schema, Writer writer, boolean pretty) {
        var gson = pretty ? GSON_PRETTY : GSON;
        gson.toJson(schema, writer);
    }
}
