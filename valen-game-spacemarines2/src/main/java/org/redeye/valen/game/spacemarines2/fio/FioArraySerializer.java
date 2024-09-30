package org.redeye.valen.game.spacemarines2.fio;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.util.*;

import java.io.*;
import java.util.*;
import java.util.function.*;

public class FioArraySerializer<T> implements FioSerializer<List<T>> {

    private final Supplier<T> factory;
    private final Supplier<T> defaultFactory;
    private final int flags;
    private final FioSerializer<T> serializer;

    public FioArraySerializer(Supplier<T> factory, Supplier<T> defaultFactory, int flags, FioSerializer<T> serializer) {
        this.factory = factory;
        this.defaultFactory = defaultFactory;
        this.flags = flags;
        this.serializer = serializer;
    }

    public FioArraySerializer(Supplier<T> factory, int flags, FioSerializer<T> serializer) {
        this(factory, factory, flags, serializer);
    }

    @Override
    public List<T> load(DataSource source) throws IOException {
        var count = source.readInt();
        Check.state(count >= 0);
        var innerFlags = serializer.flags();
        var array = new ArrayList<T>(count);

        if ((innerFlags & 4) != 0) {
            Check.state(serializer instanceof FioStructSerializer<T>, "Serializer has flag 4, but is not a struct type");
            FioStructSerializer<T> structSerializer = (FioStructSerializer<T>) serializer;
            var memberCount = source.readShort();
            var flags = source.readShort();
            Check.state(flags == 0, "Flags are not 0");
            for (int i = 0; i < count; i++) {
                array.add(defaultFactory.get());
            }
            for (int memId = 0; memId < memberCount; memId++) {
                var mode = source.readByte();
                if (mode > 0) {
                    FioStructMember<T, ?> member = structSerializer.members.get(memId);
                    // System.out.printf("Reading array struct member: %s at %d%n", member.name(), source.tell());
                    readArray(source, mode, count, array, member);
                }
            }

            return array;
        } else if ((innerFlags & 16) != 0) {
            if (serializer instanceof FioPrimitiveSerializer<T> primitiveSerializer) {
                return primitiveSerializer.loadArray(source, count);
            }
            throw new IllegalStateException("Flag 16 is not supported");
        } else {
            var mode = source.readByte();

            readArray(source, mode, count, array);
        }
        return array;
    }

    @Override
    public int flags() {
        return flags;
    }

    private void readArray(DataSource source, byte mode, int count, ArrayList<T> array) throws IOException {

        if (mode == 1) {
            for (int i = 0; i < count; i++) {
                array.add(serializer.load(source));
            }
        } else {
            var maskSize = (count + 7) / 8;
            var mask = source.readBytes(maskSize);
            for (int i = 0; i < count; i++) {
                byte b = mask[i / 8];
                if ((b & (1 << (i % 8))) != 0) {
                    array.add(serializer.load(source));
                } else {
                    array.add(defaultFactory.get());
                }
            }
        }
    }

    private <B> void readArray(DataSource source, byte mode, int count, ArrayList<T> array, FioStructMember<T, B> member) throws IOException {
        if (mode == 1) {
            for (int i = 0; i < count; i++) {
                member.setter().accept(array.get(i), member.deserializer().load(source));
            }
        } else {
            var maskSize = (count + 7) / 8;
            var mask = source.readBytes(maskSize);
            for (int i = 0; i < count; i++) {
                byte b = mask[i / 8];
                if ((b & (1 << (i % 8))) != 0) {
                    member.setter().accept(array.get(i), member.deserializer().load(source));
                } else {
                    member.setter().accept(array.get(i), null);
                }
            }
        }
    }
}
