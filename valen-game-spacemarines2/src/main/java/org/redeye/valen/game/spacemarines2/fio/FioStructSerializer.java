package org.redeye.valen.game.spacemarines2.fio;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.util.*;

import java.io.*;
import java.util.*;
import java.util.function.*;

public class FioStructSerializer<T> implements FioSerializer<T> {
    private final int sign;
    private final Supplier<T> factory;
    protected final List<FioStructMember<T, ?>> members;
    public int version = 0;
    public FioCallback<T> onReadFinishCallback;

    public FioStructSerializer(Supplier<T> factory, List<FioStructMember<T, ?>> members) {
        this(0, factory, members, null);
    }

    public FioStructSerializer(Supplier<T> factory, List<FioStructMember<T, ?>> members, FioCallback<T> onReadFinishCallback) {
        this(0, factory, members, onReadFinishCallback);
    }

    public FioStructSerializer(int sign, Supplier<T> factory, List<FioStructMember<T, ?>> members) {
        this(sign, factory, members, null);
    }

    public FioStructSerializer(int sign, Supplier<T> factory, List<FioStructMember<T, ?>> members, FioCallback<T> onReadFinishCallback) {
        this.sign = sign;
        this.factory = Check.notNull(factory);
        this.members = List.copyOf(members);
        this.onReadFinishCallback = onReadFinishCallback;
    }

    @Override
    public T load(DataSource source) throws IOException {
        if (sign != 0) {
            source.expectInt(sign);
        }
        var memberCount = source.readShort();

        var flags = source.readShort();
        if (flags == 2) {
            version = source.readShort();
        } else if (flags != 0) {
            throw new IllegalStateException("Flags are not 0");
        }
        var objectInstance = factory.get();
        var memberMask = BitSet.valueOf(source.readBytes((memberCount + 7) / 8));
        for (int i = 0; i < Math.min(memberCount, members.size()); i++) {
            if (memberMask.get(i)) {
                FioStructMember<T, ?> member = members.get(i);
                // System.out.printf("Reading %s at %d%n", member.name(), source.tell());
                setMember(member, objectInstance, source);
            } else {
                // System.out.printf("Skipped %s%n", members.get(i).name());
            }
        }

        if (onReadFinishCallback != null) {
            onReadFinishCallback.call(objectInstance, source, this);
        }

        return objectInstance;
    }

    @Override
    public int flags() {
        return 12;
    }

    protected <V> void setMember(FioStructMember<T, V> member, T holder, DataSource source) throws IOException {
        V value = member.deserializer().load(source);
        // System.out.printf("%s = %s%n", member.name(), value);
        member.setter().accept(holder, value);
    }

}
