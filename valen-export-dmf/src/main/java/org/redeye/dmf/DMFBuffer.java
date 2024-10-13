package org.redeye.dmf;

import com.google.gson.*;

import java.io.*;

public abstract class DMFBuffer {
    public final String name;
    protected final DataProvider provider;

    public DMFBuffer(String name, DataProvider provider) {
        this.name = name;
        this.provider = provider;
    }


    public JsonObject serialize(JsonSerializationContext context) throws IOException {
        final JsonObject object = new JsonObject();
        object.addProperty("name", name);
        object.addProperty("size", provider.length());
        return object;
    }

    public interface DataProvider {

        InputStream openInputStream() throws IOException;

        int length();
    }
}
