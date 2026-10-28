package be.twofold.valen.game.idtech.reader.mask;

import be.twofold.valen.core.game.*;
import be.twofold.valen.game.idtech.game.*;
import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.util.*;

public record ContainerMask(
    Map<Long, BitSet> masks
) {
    private static final String NAME = "generated/buildgame/container.mask";

    public ContainerMask {
        masks = Map.copyOf(masks);
    }

    public static ContainerMask empty() {
        return new ContainerMask(Map.of());
    }

    public static ContainerMask load(AssetLoader loader, IdTechGame game) throws IOException {
        var asset = loader.all()
            .filter(candidate -> candidate.id().fullName().equals(NAME))
            .findFirst()
            .orElseThrow(() -> new IOException("Could not find " + NAME));

        var bytes = loader.load(asset.id(), Bytes.class);
        return read(BinarySource.wrap(bytes), game);
    }

    public static ContainerMask read(BinarySource source, IdTechGame game) throws IOException {
        if (game == IdTechGame.ETERNAL) {
            source.skip(4);
        }

        var count = source.readInt();

        var masks = new HashMap<Long, BitSet>();
        for (var i = 0; i < count; i++) {
            var key = source.readLong();
            var bits = source.readBytes(source.readInt() * Long.BYTES);
            masks.put(key, BitSet.valueOf(bits.toArray()));
        }
        return new ContainerMask(masks);
    }

    public BitSet get(long hash, int count) {
        var mask = masks.get(hash);
        if (mask != null) {
            return mask;
        }
        var all = new BitSet(count);
        all.set(0, count);
        return all;
    }
}
