package org.redeye.valen.game.spacemarines2.fio;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.util.*;

import java.io.*;
import java.util.*;
import java.util.function.*;

public class FioStructSerializer<T> implements FioSerializer<T> {
    public static boolean debugPrint = true;

    private final int sign;
    private final Supplier<T> factory;
    private final int flags;
    protected final List<FioStructMember<T, ?>> members;
    public int version = 0;
    public FioCallback<T> onReadFinishCallback;

    public FioStructSerializer(Supplier<T> factory, int flags, List<FioStructMember<T, ?>> members) {
        this(0, factory, flags, members, null);
    }

    public FioStructSerializer(Supplier<T> factory, int flags, List<FioStructMember<T, ?>> members, FioCallback<T> onReadFinishCallback) {
        this(0, factory, flags, members, onReadFinishCallback);
    }

    public FioStructSerializer(int sign, Supplier<T> factory, int flags, List<FioStructMember<T, ?>> members) {
        this(sign, factory, flags, members, null);
    }

    public FioStructSerializer(int sign, Supplier<T> factory, int flags, List<FioStructMember<T, ?>> members, FioCallback<T> onReadFinishCallback) {
        this.sign = sign;
        this.factory = Check.notNull(factory);
        this.flags = flags;
        this.members = List.copyOf(members);
        this.onReadFinishCallback = onReadFinishCallback;
    }

    @Override
    public T load(DataSource source) throws IOException {
        if (sign != 0) {
            source.expectInt(sign);
        }
        var memberCount = source.readShort();
        // Check.state(memberCount <= members.size(), "Invalid member count");

        var flags = source.readShort();
        if (flags == 2) {
            version = source.readShort();
        } else if (flags != 0) {
            throw new IllegalStateException("Flags are not 0");
        }
        var holder = factory.get();
        var memberMaskSize = (memberCount + 7) / 8;
        var memberMask = source.readBytes(memberMaskSize);
        for (int i = 0; i < memberCount; i++) {
            if (i >= members.size()) {
                break;
            }
            byte b = memberMask[i / 8];
            if ((b & (1 << (i % 8))) != 0) {
                FioStructMember<T, ?> member = members.get(i);
                if (debugPrint) {
                    System.out.printf("Reading member %s at %d%n", member.name(), source.tell());
                }
                setMember(member, holder, source);
            } else {
                if (debugPrint) {
                    System.out.printf("Member %s was skipped%n", members.get(i).name());
                }
            }
        }

        if (onReadFinishCallback != null) {
            onReadFinishCallback.call(holder, source, this);
        }

        return holder;
    }

    @Override
    public int flags() {
        return flags;
    }

    protected <V> void setMember(FioStructMember<T, V> member, T holder, DataSource source) throws IOException {
        member.setter().accept(holder, member.deserializer().load(source));
    }
}
