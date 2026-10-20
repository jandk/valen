package be.twofold.valen.game.idtech.game;

import be.twofold.valen.core.game.*;
import wtf.reversed.toolbox.util.*;

import java.util.*;

public abstract class IdclAsset<I extends IdclAssetID<?, ?>> implements Asset {
    private final I id;
    private final Location location;
    private final long hash;
    private final int version;

    protected IdclAsset(I id, Location location, long hash, int version) {
        this.id = Check.nonNull(id, "id");
        this.location = Check.nonNull(location, "location");
        this.hash = hash;
        this.version = version;
    }

    @Override
    public final I id() {
        return id;
    }

    @Override
    public final Location location() {
        return location;
    }

    public final long hash() {
        return hash;
    }

    public final int version() {
        return version;
    }

    @Override
    public Map<String, Object> properties() {
        var properties = new HashMap<String, Object>();
        properties.put("hash", hash);
        properties.put("Type", id.type().toString());
        if (id.variation().value() != 0) {
            properties.put("Variation", id.variation());
        }
        return properties;
    }

    @Override
    public String exportName() {
        if (id.type().value().equals("image")) {
            var hashString = HexFormat.of().toHexDigits(id.resourceId());
            return id.path().baseName() + "@" + hashString;
        }
        return id.path().baseName();
    }

    @Override
    public final boolean equals(Object obj) {
        return obj instanceof IdclAsset<?> other
            && getClass() == other.getClass()
            && id.equals(other.id);
    }

    @Override
    public final int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
            "id=" + id + ", " +
            "location=" + location + ", " +
            "hash=" + hash + ", " +
            "version=" + version +
            "}";
    }
}
