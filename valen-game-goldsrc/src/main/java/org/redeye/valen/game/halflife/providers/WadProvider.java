package org.redeye.valen.game.halflife.providers;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.io.*;
import org.redeye.valen.game.halflife.*;
import org.redeye.valen.game.halflife.readers.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class WadProvider implements Provider {

    private Path path;
    private final Provider parent;
    private final Map<String, WadEntry> assets = new HashMap<>();

    public WadProvider(Path path, Provider parent) throws IOException {
        this.path = path;
        this.parent = parent;
        try (var source = DataSource.fromPath(path)) {
            var magic = source.readInt();
            if (magic != 0x33444157 && magic != 0x34444157) {
                throw new IOException("WAD Magic number mismatch");
            }
            var count = source.readInt();
            var offset = source.readInt();
            source.seek(offset);
            for (int i = 0; i < count; i++) {
                var entry = WadEntry.read(source);
                assets.put(entry.name(), entry);
            }
        }
    }

    @Override
    public String getName() {
        return path.getFileName().toString();
    }

    @Override
    public Provider getParent() {
        return this.parent == null ? this : this.parent.getParent();
    }

    @Override
    public List<Asset> assets() {
        return assets.values().stream().map(wadEntry -> new Asset(new HalfLifeAssetID(getName(), wadEntry.name()), wadEntry.getAssetType(), wadEntry.size(), Map.of())).toList();
    }

    @Override
    public boolean exists(AssetID identifier) {
        return false;
    }

    @Override
    public <T> T loadAsset(AssetID identifier, Class<T> clazz) throws IOException {
        if (!assets.containsKey(identifier.fullName())) {
            return null;
        }
        var entry = assets.get(identifier.fullName());
        try (var source = DataSource.fromPath(path)) {
            source.seek(entry.offset());

            if (clazz == byte[].class) {
                return (T) source.readBytes(entry.size());
            }

            var texture = switch (entry.type()) {
                case MIPTEX -> MipTex.read(source);
                case FONT -> Font.read(source);

                default -> {
                    throw new IllegalStateException("Cant read asset " + entry.name() + " of type: " + entry.type().name());
                }
            };

            return clazz.cast(texture);

        }

    }
}
