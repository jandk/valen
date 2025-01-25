package be.twofold.valen.format.gltf;

import be.twofold.valen.format.gltf.gson.*;
import be.twofold.valen.format.gltf.model.*;

import java.io.*;

public interface JsonWriter {

    static JsonWriter create() {
        return new GsonWriter();
    }

    void writeJson(GltfSchema schema, Writer writer, boolean pretty);

}
