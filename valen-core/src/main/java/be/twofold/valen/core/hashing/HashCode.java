package be.twofold.valen.core.hashing;

import be.twofold.valen.core.util.collect.*;

import java.util.*;

public abstract class HashCode {
    private HashCode() {
    }

    public static HashCode ofInt(int hashCode) {
        return new OfInt(hashCode);
    }

    public static HashCode ofLong(long hashCode) {
        return new OfLong(hashCode);
    }

    public static HashCode ofBytes(Bytes hashCode) {
        return new OfBytes(hashCode);
    }

    public abstract int asInt();

    public abstract long asLong();

    public abstract Bytes asBytes();

    @Override
    public abstract boolean equals(Object obj);

    @Override
    public abstract int hashCode();

    @Override
    public abstract String toString();

    private static final class OfInt extends HashCode {
        private final int hashCode;

        private OfInt(int hashCode) {
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
        public Bytes asBytes() {
            return Bytes.Mutable
                .allocate(Integer.BYTES)
                .setInt(0, hashCode);
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof OfInt other
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

    private static final class OfLong extends HashCode {
        private final long hashCode;

        private OfLong(long hashCode) {
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
        public Bytes asBytes() {
            return Bytes.Mutable
                .allocate(Long.BYTES)
                .setLong(0, hashCode);
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof OfLong other
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

    private static final class OfBytes extends HashCode {
        private final Bytes hashCode;

        private OfBytes(Bytes hashCode) {
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
        public Bytes asBytes() {
            return hashCode;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof OfBytes other
                && hashCode.equals(other.hashCode);
        }

        @Override
        public int hashCode() {
            return hashCode.hashCode();
        }

        @Override
        public String toString() {
            var builder = new StringBuilder(hashCode.length() * 2);
            for (int i = 0; i < hashCode.length(); i++) {
                HexFormat.of().toHexDigits(builder, hashCode.get(i));
            }
            return builder.toString();
        }
    }
}
