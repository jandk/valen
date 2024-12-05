package be.twofold.valen.core.hashing;

public sealed interface HashCode {

    int asInt();

    long asLong();

    record IntHashCode(int hash) implements HashCode {
        @Override
        public int asInt() {
            return hash;
        }

        @Override
        public long asLong() {
            return Integer.toUnsignedLong(hash);
        }
    }

    record LongHashCode(long hash) implements HashCode {
        @Override
        public int asInt() {
            return (int) hash;
        }

        @Override
        public long asLong() {
            return hash;
        }
    }
}
