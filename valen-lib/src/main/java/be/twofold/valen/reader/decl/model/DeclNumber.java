package be.twofold.valen.reader.decl.model;

public final class DeclNumber extends DeclValue {

    private final Number value;

    public DeclNumber(Number value) {
        this.value = value;
    }

    public DeclNumber(String value) {
        this(new StringNumber(value));
    }

    @Override
    public DeclValue copy() {
        return this;
    }


    @Override
    public Number asNumber() {
        return value;
    }


    @Override
    public boolean equals(Object obj) {
        return obj instanceof DeclNumber other
            && value.equals(other.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

}
