package be.twofold.valen.gltf;

import be.twofold.valen.gltf.gson.*;
import be.twofold.valen.gltf.model.*;

import java.io.*;

public interface JsonWriter {

    static JsonWriter create() {
        return new GsonWriter();
    }

    void writeJson(GltfSchema schema, Writer writer, boolean pretty);

}
