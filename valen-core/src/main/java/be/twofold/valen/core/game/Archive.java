package be.twofold.valen.core.game;

import java.util.*;
import java.util.stream.*;

public interface Archive {

    Optional<Asset> get(AssetID id);

    Stream<? extends Asset> all();

    static Archive of(Iterable<? extends Asset> assets) {
        var index = new HashMap<AssetID, Asset>();
        for (var asset : assets) {
            index.putIfAbsent(asset.id(), asset);
        }
        var indexed = Map.copyOf(index);
        return new Archive() {
            @Override
            public Optional<Asset> get(AssetID id) {
                return Optional.ofNullable(indexed.get(id));
            }

            @Override
            public Stream<? extends Asset> all() {
                return indexed.values().stream();
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
            public Stream<? extends Asset> all() {
                return archives.stream()
                    .flatMap(Archive::all);
            }
        };
    }

    static Archive layered(Archive primary, Archive... others) {
        if (others.length == 0) {
            return primary;
        }

        var archives = new ArrayList<Archive>(others.length + 1);
        archives.add(primary);
        archives.addAll(Arrays.asList(others));

        return new Archive() {
            @Override
            public Optional<Asset> get(AssetID id) {
                return archives.stream()
                    .flatMap(archive -> archive.get(id).stream())
                    .findFirst();
            }

            @Override
            public Stream<? extends Asset> all() {
                return primary.all();
            }
        };
    }

}
