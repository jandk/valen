package org.redeye.valen.game.spacemarines2.fio;

import be.twofold.valen.core.io.*;

import java.io.*;
import java.math.*;

public class FioVarIntFlagsSerializer implements FioSerializer<BigInteger> {
    private int flags;

    public FioVarIntFlagsSerializer() {
        this.flags = 0;
    }

    private BigInteger read(DataSource source, int count) throws IOException {
        var result = BigInteger.ZERO;
        for (int i = 0; i < count; i++) {
            long l = Byte.toUnsignedLong(source.readByte());
            result = result.add(BigInteger.valueOf(l).shiftLeft(i * 8));
        }
        return result;
    }

    @Override
    public BigInteger load(DataSource source) throws IOException {
        var count = source.readShort();
        return read(source, (count + 7) / 8);
    }

    @Override
    public int flags() {
        return flags;
    }

}
