package org.redeye.dmf;

import com.google.gson.*;

import java.io.*;
import java.util.*;
import java.util.zip.*;

public class DMFInternalBuffer extends DMFBuffer {
    public DMFInternalBuffer( String name,  DataProvider provider) {
        super(name, provider);
    }

    
    @Override
    public JsonObject serialize(JsonSerializationContext context) throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (OutputStream os = new DeflaterOutputStream(baos)) {
            try (InputStream is = provider.openInputStream()) {
                is.transferTo(os);
            }
        }

        final JsonObject object = super.serialize(context);
        object.addProperty("data", Base64.getEncoder().encodeToString(baos.toByteArray()));
        return object;
    }
}
