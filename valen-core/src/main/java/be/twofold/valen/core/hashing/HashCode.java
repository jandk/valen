package be.twofold.valen.core.hashing;

import be.twofold.valen.core.util.collect.*;

import java.nio.charset.*;
import java.util.*;

public abstract class HashCode {
    private HashCode() {
    }

    public static HashCode ofInt(int hashCode) {
        return new IntHashCode(hashCode);
    }

    public static HashCode ofLong(long hashCode) {
        return new LongHashCode(hashCode);
    }

    public static HashCode ofBytes(Bytes hashCode) {
        return new BytesHashCode(hashCode);
    }

    public abstract int asInt();

    public abstract long asLong();

    @Override
    public abstract boolean equals(Object obj);

    @Override
    public abstract int hashCode();

    @Override
    public abstract String toString();

    private static final class IntHashCode extends HashCode {
        private final int hashCode;

        private IntHashCode(int hashCode) {
            this.hashCode = hashCode;
        }

        @Override
        public int asInt() {
            return hashCode;
        }

        @Override
        public long asLong() {
            return Integer.toUnsignedLong(hashCode);
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof IntHashCode other
                && hashCode == other.hashCode;
        }

        @Override
        public int hashCode() {
            return Integer.hashCode(hashCode);
        }

        @Override
        public String toString() {
            return HexFormat.of().toHexDigits(hashCode);
        }
    }

    private static final class LongHashCode extends HashCode {
        private final long hashCode;

        private LongHashCode(long hashCode) {
            this.hashCode = hashCode;
        }

        @Override
        public int asInt() {
            return (int) hashCode;
        }

        @Override
        public long asLong() {
            return hashCode;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof LongHashCode other
                && hashCode == other.hashCode;
        }

        @Override
        public int hashCode() {
            return Long.hashCode(hashCode);
        }

        @Override
        public String toString() {
            return HexFormat.of().toHexDigits(hashCode);
        }
    }

    private static final class BytesHashCode extends HashCode {
        private final Bytes hashCode;

        private BytesHashCode(Bytes hashCode) {
            this.hashCode = hashCode;
        }

        @Override
        public int asInt() {
            return hashCode.getInt(0);
        }

        @Override
        public long asLong() {
            return hashCode.getLong(0);
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof BytesHashCode other
                && hashCode.equals(other.hashCode);
        }

        @Override
        public int hashCode() {
            return hashCode.hashCode();
        }

        @Override
        public String toString() {
            // TODO: Encoding a Bytes should be done through some sort of encoding...
            byte[] hex = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
            byte[] result = new byte[hashCode.size() * 2];
            for (int i = 0; i < hashCode.size(); i++) {
                int b = hashCode.getByte(i);
                result[i * 2] = hex[(b >> 4) & 0xf];
                result[i * 2 + 1] = hex[b & 0xf];
            }
            return new String(result, StandardCharsets.ISO_8859_1);
        }
    }
}
