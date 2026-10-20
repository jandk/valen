package be.twofold.valen.game.idtech.game;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.util.*;
import wtf.reversed.toolbox.hash.*;
import wtf.reversed.toolbox.util.*;

import java.util.*;

public abstract class IdclAssetID<
    T extends Comparable<T> & ValueEnum<String>,
    V extends Enum<V> & ValueEnum<Integer>
    > implements AssetID {
    private static final Comparator<IdclAssetID<?, ?>> COMPARATOR = Comparator
        .<IdclAssetID<?, ?>, String>comparing(IdclAssetID::name, AlphanumericComparator.instance())
        .thenComparing(IdclAssetID::type)
        .thenComparing(IdclAssetID::variation);

    private final String name;
    private final T type;
    private final V variation;

    protected IdclAssetID(String name, T type, V variation) {
        this.name = Check.nonNull(name, "name");
        this.type = Check.nonNull(type, "type");
        this.variation = Check.nonNull(variation, "variation");
    }

    public final String name() {
        return name;
    }

    public final T type() {
        return type;
    }

    public final V variation() {
        return variation;
    }

    public final long resourceId() {
        return resourceId(type.toString(), name);
    }

    private long resourceId(String type, String name) {
        var hash = HashFunction.farmHashFingerprint64();
        long lo = hash.hash(type).asLong();
        long hi = hash.hash(name).asLong();
        return fingerprint(lo, hi);
    }

    private long fingerprint(long lo, long hi) {
        long kMul = 0x9DDF_EA08_EB38_2D69L;
        long a = (lo ^ hi) * kMul;
        a ^= (a >>> 47);
        long b = (hi ^ a) * kMul;
        b ^= (b >>> 44);
        b *= kMul;
        b ^= (b >>> 41);
        b *= kMul;
        return b;
    }

    @Override
    public final String fullName() {
        return name;
    }

    @Override
    public final AssetPath path() {
        int index = name.indexOf('$');
        return AssetPath.of(index < 0 ? name : name.substring(0, index));
    }

    @Override
    public final int compareTo(AssetID o) {
        return COMPARATOR.compare(this, (IdclAssetID<?, ?>) o);
    }

    @Override
    public final boolean equals(Object obj) {
        return obj instanceof IdclAssetID<?, ?> other
            && getClass() == other.getClass()
            && name.equals(other.name)
            && type.equals(other.type)
            && variation == other.variation;
    }

    @Override
    public final int hashCode() {
        int result = 1;
        result = 31 * result + name.hashCode();
        result = 31 * result + type.hashCode();
        result = 31 * result + variation.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
            "name=" + name + ", " +
            "type=" + type + ", " +
            "variation=" + variation +
            "}";
    }
}
