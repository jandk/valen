package be.twofold.valen.reader.decl.model;

import be.twofold.valen.core.util.*;

public final class DeclString extends DeclValue {

    private final String value;

    public DeclString(String value) {
        this.value = Check.notNull(value);
    }


    @Override
    public DeclValue copy() {
        return this;
    }


    @Override
    public String asString() {
        return value;
    }


    @Override
    public boolean equals(Object obj) {
        return obj instanceof DeclString other
            && value.equals(other.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return "'" + value + "'";
    }

}
