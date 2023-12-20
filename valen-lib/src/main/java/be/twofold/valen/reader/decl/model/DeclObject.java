package be.twofold.valen.reader.decl.model;

import java.util.*;

public final class DeclObject extends DeclValue implements Iterable<Map.Entry<String, DeclValue>> {

    private final Map<String, DeclValue> values;

    public DeclObject() {
        this(new LinkedHashMap<>());
    }

    public DeclObject(Map<String, DeclValue> values) {
        this.values = values;
    }


    @Override
    public Iterator<Map.Entry<String, DeclValue>> iterator() {
        return values.entrySet().iterator();
    }


    @Override
    public DeclValue copy() {
        Map<String, DeclValue> values = new LinkedHashMap<>();
        for (Map.Entry<String, DeclValue> entry : this.values.entrySet()) {
            values.put(entry.getKey(), entry.getValue().copy());
        }
        return new DeclObject(values);
    }


    @Override
    public DeclObject asObject() {
        return this;
    }


    // region Accessors

    public int size() {
        return values.size();
    }

    public DeclValue get(String key) {
        return values.get(key);
    }

    public void put(String key, DeclValue value) {
        values.put(key, value);
    }

    public void remove(String key) {
        values.remove(key);
    }

    // endregion


    @Override
    public boolean equals(Object obj) {
        return this == obj || obj instanceof DeclObject
            && values.equals(((DeclObject) obj).values);
    }

    @Override
    public int hashCode() {
        return values.hashCode();
    }

    @Override
    public String toString() {
        return "JsonObject(" + values + ")";
    }

    // Recursively merge objects, by key.
    public DeclObject merge(DeclObject other) {
        Map<String, DeclValue> values = new LinkedHashMap<>(this.values);
        for (Map.Entry<String, DeclValue> entry : other) {
            String key = entry.getKey();
            DeclValue thatValue = entry.getValue();
            DeclValue thisValue = values.get(key);
            if (thisValue != null && thisValue.isObject() && thatValue.isObject()) {
                thatValue = thisValue.asObject().merge(thatValue.asObject());
            }
            values.put(key, thatValue);
        }
        return new DeclObject(values);
    }
}
