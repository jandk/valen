package org.redeye.valen.game.source1.utils.keyvalues;

import java.util.*;

public sealed interface VdfPathElement {
    VdfValue get(VdfValue object);

    record Index(int index) implements VdfPathElement {
        @Override
        public VdfValue get(VdfValue object) {
            Objects.requireNonNull(object);
            return object.asArray().get(index);
        }

        @Override
        public String toString() {
            return "[%d]".formatted(index);
        }
    }

    record Key(String key) implements VdfPathElement {

        @Override
        public VdfValue get(VdfValue object) {
            Objects.requireNonNull(object);
            switch (object) {
                case VdfValue.VdfList vdfList -> {
                    return vdfList.get(0).asObject().get(key);
                }
                case VdfValue.VdfObject vdfObject -> {
                    return vdfObject.get(key);
                }
                default -> throw new IllegalArgumentException("Cannot use string index on non-key value.");
            }
        }

        @Override
        public String toString() {
            return key;
        }
    }
}
