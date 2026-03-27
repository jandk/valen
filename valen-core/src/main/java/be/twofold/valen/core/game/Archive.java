package be.twofold.valen.core.game;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public interface Archive {

    Optional<Asset> get(AssetID id);

    Stream<Asset> all();

    static Archive simple(Map<? extends AssetID, ? extends Asset> assets) {
        return new Archive() {
            @Override
            public Optional<Asset> get(AssetID id) {
                return Optional.ofNullable(assets.get(id));
            }

            @Override
            public Stream<Asset> all() {
                return assets.values().stream()
                    .map(Function.identity());
            }
        };
    }

    static Archive combine(List<Archive> archives) {
        return new Archive() {
            @Override
            public Optional<Asset> get(AssetID id) {
                return archives.stream()
                    .flatMap(archive -> archive.get(id).stream())
                    .findFirst();
            }

            @Override
            public Stream<Asset> all() {
                return archives.stream()
                    .flatMap(Archive::all);
            }
        };
    }

}
