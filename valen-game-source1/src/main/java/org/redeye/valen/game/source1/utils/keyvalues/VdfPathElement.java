package org.redeye.valen.game.source1.utils.keyvalues;

public sealed interface VdfPathElement {
    VdfValue get(VdfValue object);

    record Index(int index) implements VdfPathElement {
        @Override
        public VdfValue get(VdfValue object) {
            return object.asArray().get(index);
        }

        @Override
        public String toString() {
            return "[" + index + "]";
        }
    }

    record Key(String key) implements VdfPathElement {
        @Override
        public VdfValue get(VdfValue object) {
            return switch (object) {
                case VdfValue.VdfList vdfList -> vdfList.getFirst().asObject().get(key);
                case VdfValue.VdfObject vdfObject -> vdfObject.get(key);
                default -> throw new IllegalArgumentException("Cannot use string index on non-key value.");
            };
        }

        @Override
        public String toString() {
            return key;
        }
    }
}
