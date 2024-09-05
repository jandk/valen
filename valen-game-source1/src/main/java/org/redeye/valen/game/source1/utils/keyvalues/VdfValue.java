package org.redeye.valen.game.source1.utils.keyvalues;

import be.twofold.valen.core.util.*;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public sealed interface VdfValue {

    record VdfString(String value) implements VdfValue {
        @Override
        public String toString() {
            return "\"%s\"".formatted(value);
        }

        @Override
        public String asString() {
            return value;
        }

        @Override
        public Object valueRepr() {
            return value;
        }
    }

    record VdfNumber(Number value) implements VdfValue {
        @Override
        public String toString() {
            return value.toString();
        }

        @Override
        public Number asNumber() {
            return value;
        }

        @Override
        public Object valueRepr() {
            return value;
        }
    }

    record VdfObject(Map<String, VdfValue> values) implements VdfValue {
        @Override
        public String toString() {
            return values.toString();
        }

        @Override
        public VdfObject asObject() {
            return this;
        }

        @Override
        public Object valueRepr() {
            var tmp = new LinkedHashMap<String, Object>();
            values.forEach((k, v) -> tmp.put(k, v.valueRepr()));
            return tmp;
        }

        public int size() {
            return values.size();
        }

        public boolean isEmpty() {
            return values.isEmpty();
        }

        public boolean has(String key) {
            return values.containsKey(key);
        }

        public VdfValue get(String key) {
            return values.get(key);
        }

        public Set<String> keySet() {
            return values.keySet();
        }
    }

    record VdfList(List<VdfValue> values) implements VdfValue, Iterable<VdfValue> {
        @Override
        public String toString() {
            return values.toString();
        }

        @Override
        public VdfList asArray() {
            return this;
        }

        @Override
        public Object valueRepr() {
            var tmp = new ArrayList<>();
            values.forEach(v -> tmp.add(v.valueRepr()));
            return tmp;
        }

        public int size() {
            return values.size();
        }

        public VdfValue get(int index) {
            Check.index(index, size());
            return values.get(index);
        }

        @Override
        public Iterator<VdfValue> iterator() {
            return values.iterator();
        }

        public void forEach(Consumer<? super VdfValue> action) {
            values.forEach(action);
        }

        @Override
        public Spliterator<VdfValue> spliterator() {
            return values.spliterator();
        }

        public Stream<VdfValue> stream() {
            return values.stream();
        }
    }

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
        return new IllegalStateException("Value is not of type " + type);
    }

    Object valueRepr();


}
