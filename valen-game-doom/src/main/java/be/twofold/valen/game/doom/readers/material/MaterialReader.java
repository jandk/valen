package be.twofold.valen.game.doom.readers.material;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.material.*;
import be.twofold.valen.game.doom.*;
import be.twofold.valen.game.idtech.game.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.util.*;

public final class MaterialReader implements AssetReader.Binary<Material, DoomAsset> {
    @Override
    public boolean canRead(DoomAsset asset) {
        return asset.id().type() == ResourceType.of("material");
    }

    @Override
    public Material read(BinaryReader reader, DoomAsset asset, LoadingContext context) throws IOException {
        var parms = parse(reader.readString(Math.toIntExact(reader.remaining())));
        return new MaterialBuilder(asset.id().name(), parms, context).build();
    }

    // I am tired of writing Goddamn parsers for Goddamn id crap, so I kept it simple
    private Map<String, String> parse(String source) {
        var parms = new LinkedHashMap<String, String>();
        for (var line : source.lines().toList()) {
            var trimmed = line.strip();
            if (trimmed.isEmpty() || trimmed.equals("{") || trimmed.equals("}") || trimmed.startsWith("//")) {
                continue;
            }
            var split = trimmed.split("\\s+", 2);
            var value = split.length > 1 ? split[1].strip() : "";
            parms.put(split[0].toLowerCase(Locale.ROOT), unquote(value));
        }
        return parms;
    }

    private String unquote(String value) {
        if (value.length() >= 2 && value.startsWith("\"") && value.endsWith("\"")) {
            return value.substring(1, value.length() - 1);
        }
        return value;
    }
}
