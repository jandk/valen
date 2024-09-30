package org.redeye.valen.game.spacemarines2.psSection;

import be.twofold.valen.core.util.*;

import java.util.*;
import java.util.function.*;

public sealed interface PsSectionValue {

    record PsSectionString(String value) implements PsSectionValue {
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

    record PsSectionNumber(Number value) implements PsSectionValue {
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

    record PsSectionObject(Map<String, PsSectionValue> values) implements PsSectionValue {
        @Override
        public String toString() {
            return values.toString();
        }

        @Override
        public PsSectionObject asObject() {
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

        public PsSectionValue get(String key) {
            return values.get(key);
        }

        public Set<String> keySet() {
            return values.keySet();
        }
    }

    record PsSectionList(List<PsSectionValue> values) implements PsSectionValue, Iterable<PsSectionValue> {
        @Override
        public String toString() {
            return values.toString();
        }

        @Override
        public PsSectionList asArray() {
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

        public PsSectionValue get(int index) {
            Check.index(index, size());
            return values.get(index);
        }

        @Override
        public Iterator<PsSectionValue> iterator() {
            return values.iterator();
        }

        public void forEach(Consumer<? super PsSectionValue> action) {
            values.forEach(action);
        }

        @Override
        public Spliterator<PsSectionValue> spliterator() {
            return values.spliterator();
        }
    }

    record PsSectionBoolean(boolean value) implements PsSectionValue {

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

    default PsSectionObject asObject() {
        throw ex("Object");
    }

    default PsSectionList asArray() {
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
