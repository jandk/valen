package org.redeye.valen.game.spacemarines2.td;

import be.twofold.valen.core.util.*;

import java.util.*;
import java.util.function.*;

public sealed interface TDValue {

    record TDString(String value) implements TDValue {
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

    record TDNumber(Number value) implements TDValue {
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

    record TDObject(Map<String, TDValue> values) implements TDValue {
        @Override
        public String toString() {
            return values.toString();
        }

        @Override
        public TDObject asObject() {
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

        public TDValue get(String key) {
            return values.get(key);
        }

        public Set<String> keySet() {
            return values.keySet();
        }
    }

    record TDList(List<TDValue> values) implements TDValue, Iterable<TDValue> {
        @Override
        public String toString() {
            return values.toString();
        }

        @Override
        public TDList asArray() {
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

        public TDValue get(int index) {
            Check.index(index, size());
            return values.get(index);
        }

        @Override
        public Iterator<TDValue> iterator() {
            return values.iterator();
        }

        public void forEach(Consumer<? super TDValue> action) {
            values.forEach(action);
        }

        @Override
        public Spliterator<TDValue> spliterator() {
            return values.spliterator();
        }
    }

    record TDBoolean(boolean value) implements TDValue {

        @Override
        public Object valueRepr() {
            return value;
        }

        @Override
        public String toString() {
            return Boolean.toString(value);
        }

        @Override
        public Boolean asBoolean() {
            return value;
        }
    }

    default TDObject asObject() {
        throw ex("Object");
    }

    default TDList asArray() {
        throw ex("Array");
    }

    default String asString() {
        throw ex("String");
    }

    default Number asNumber() {
        throw ex("Number");
    }

    default Boolean asBoolean() {
        throw ex("Boolean");
    }

    private RuntimeException ex(String type) {
        return new IllegalStateException("Value is not of type " + type);
    }

    Object valueRepr();


}
