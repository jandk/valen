package be.twofold.valen.reader.decl.model;

public final class DeclBoolean extends DeclValue {

    public static final DeclBoolean True = new DeclBoolean(true);
    public static final DeclBoolean False = new DeclBoolean(false);

    private final boolean value;

    private DeclBoolean(boolean value) {
        this.value = value;
    }


    @Override
    public DeclValue copy() {
        return this;
    }


    @Override
    public boolean asBoolean() {
        return value;
    }


    @Override
    public boolean equals(Object obj) {
        return obj instanceof DeclBoolean other
            && value == other.value;
    }

    @Override
    public int hashCode() {
        return Boolean.hashCode(value);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

}
