package be.twofold.valen.core.hashing;

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

    public abstract int asInt();

    public abstract long asLong();

    public abstract boolean equals(Object obj);

    public abstract int hashCode();

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
}
