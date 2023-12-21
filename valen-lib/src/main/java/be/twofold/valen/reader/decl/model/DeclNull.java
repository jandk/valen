package be.twofold.valen.reader.decl.model;

public final class DeclNull extends DeclValue {
    public static final DeclNull Null = new DeclNull();

    private DeclNull() {
    }

    @Override
    public DeclValue copy() {
        return Null;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof DeclNull;
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Null";
    }
}
