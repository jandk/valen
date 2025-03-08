package org.redeye.valen.game.source1.utils.keyvalues;

import java.util.*;

public sealed interface VdfValue {
    default VdfObject asObject() {
        throw ex("Object");
    }

    default VdfList asArray() {
        throw ex("Array");
    }

    default String asString() {
        throw ex("String");
    }

    default Number asNumber() {
        throw ex("Number");
    }

    private RuntimeException ex(String type) {
        return new ClassCastException("Value is not of type " + type);
    }

    record VdfString(String value) implements VdfValue {
        @Override
        public String asString() {
            return value;
        }

        @Override
        public String toString() {
            return "\"" + value + "\"";
        }
    }

    record VdfNumber(Number value) implements VdfValue {
        @Override
        public Number asNumber() {
            return value;
        }

        @Override
        public String toString() {
            return value.toString();
        }
    }

    final class VdfObject extends AbstractMap<String, VdfValue> implements VdfValue {
        private final Map<String, VdfValue> values;

        public VdfObject(Map<String, VdfValue> values) {
            this.values = Map.copyOf(values);
        }

        @Override
        public VdfObject asObject() {
            return this;
        }

        @Override
        public Set<Entry<String, VdfValue>> entrySet() {
            return values.entrySet();
        }

        @Override
        public boolean containsKey(Object key) {
            return values.containsKey(key);
        }

        @Override
        public VdfValue get(Object key) {
            return values.get(key);
        }
    }

    final class VdfList extends AbstractList<VdfValue> implements VdfValue, RandomAccess {
        private final List<VdfValue> values;

        public VdfList() {
            this(new ArrayList<>());
        }

        public VdfList(List<VdfValue> values) {
            this.values = values;
        }

        @Override
        public VdfList asArray() {
            return this;
        }

        @Override
        public VdfValue get(int index) {
            return values.get(index);
        }

        @Override
        public int size() {
            return values.size();
        }

        @Override
        public VdfValue set(int index, VdfValue element) {
            return values.set(index, element);
        }

        @Override
        public boolean add(VdfValue vdfValue) {
            return values.add(vdfValue);
        }

        @Override
        public boolean remove(Object o) {
            return values.remove(o);
        }
    }
}
