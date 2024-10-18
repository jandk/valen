package org.redeye.valen.game.spacemarines2.fio;

import be.twofold.valen.core.io.*;
import com.google.gson.*;
import org.redeye.valen.game.spacemarines2.psSection.*;

import java.io.*;

public class FioPsSectionSerializer implements FioSerializer<JsonObject> {
    @Override
    public JsonObject load(DataSource source) throws IOException {
        String val = source.readPString();
        return PsSectionAscii.parseFromString(val);
    }

    @Override
    public int flags() {
        return 9;
    }
}
