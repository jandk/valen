package org.redeye.valen.game.spacemarines2.fio;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.util.*;

import java.io.*;
import java.util.*;
import java.util.function.*;

public class FioArraySerializer<T> implements FioSerializer<List<T>> {

    private final Supplier<T> defaultFactory;
    private final FioSerializer<T> serializer;

    public FioArraySerializer(Supplier<T> defaultFactory, FioSerializer<T> serializer) {
        this.defaultFactory = defaultFactory;
        this.serializer = serializer;
    }

    @Override
    public List<T> load(DataSource source) throws IOException {
        var count = source.readInt();
        Check.state(count >= 0);
        var innerFlags = serializer.flags();
        var array = new ArrayList<T>(count);

        if ((innerFlags & 4) != 0) {
            if (!(serializer instanceof FioStructSerializer<T> structSerializer)) {
                throw new UnsupportedOperationException("Serializer has flag 4, but is not a struct type");
            }
            var memberCount = source.readShort();
            var flags = source.readShort();
            Check.state(flags == 0, "Flags are not 0");
            for (int i = 0; i < count; i++) {
                array.add(defaultFactory.get());
            }
            for (int i = 0; i < memberCount; i++) {
                var mode = source.readByte();
                if (mode > 0) {
                    readArray(source, mode, count, array, structSerializer.members.get(i));
                }
            }
            return array;
        }
        if ((innerFlags & 16) != 0) {
            if (!(serializer instanceof FioPrimitiveSerializer<T> primitiveSerializer)) {
                throw new UnsupportedOperationException("Flag 16 is not supported");
            }
            return primitiveSerializer.loadArray(source, count);
        }

        readArray(source, source.readByte(), count, array);
        return array;
    }

    @Override
    public int flags() {
        return 9;
    }

    private void readArray(DataSource source, byte mode, int count, ArrayList<T> array) throws IOException {
        if (mode == 1) {
            for (int i = 0; i < count; i++) {
                array.add(serializer.load(source));
            }
            return;
        }

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

    private <B> void readArray(DataSource source, byte mode, int count, ArrayList<T> array, FioStructMember<T, B> member) throws IOException {
        if (mode == 1) {
            for (int i = 0; i < count; i++) {
                member.setter().accept(array.get(i), member.deserializer().load(source));
            }
            return;
        }

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
