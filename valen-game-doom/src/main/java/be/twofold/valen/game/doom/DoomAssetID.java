package be.twofold.valen.game.doom;

import be.twofold.valen.core.game.*;
import be.twofold.valen.game.doom.vmtr.*;
import be.twofold.valen.game.idtech.game.*;
import wtf.reversed.toolbox.hash.*;
import wtf.reversed.toolbox.util.*;

import java.util.*;

public record DoomAssetID(
    String name,
    ResourceType type,
    VmtrLayer layer
) implements AssetID {
    private static final HashFunction HASH = HashFunction.murmur2A(0x5671_3BD7);
    private static final ResourceType VMTR = ResourceType.of("vmtr");

    public DoomAssetID {
        Check.nonNull(name, "name");
        Check.nonNull(type, "type");
    }

    public DoomAssetID(String name, ResourceType type) {
        this(name, type, null);
    }

    public DoomAssetID(String name, VmtrLayer layer) {
        this(name, VMTR, Check.nonNull(layer, "layer"));
    }

    @Override
    public String fullName() {
        return layer == null ? name : name + layer.suffix();
    }

    public boolean isVmtr() {
        return layer != null;
    }

    public String hashedBaseName() {
        return path().baseName() + "#" + HexFormat.of().toHexDigits(resourceId());
    }

    public int resourceId() {
        var typeHash = HASH.hash(type.toString()).asInt();
        var hash = HashFunction.murmur2A(typeHash).hash(name.toLowerCase(Locale.ROOT)).asInt();
        return hash == 0 ? 1 : hash;
    }
}
