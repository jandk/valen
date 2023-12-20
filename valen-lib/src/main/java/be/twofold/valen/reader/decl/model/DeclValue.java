package be.twofold.valen.reader.decl.model;

public abstract class DeclValue {

    DeclValue() {
    }


    public abstract DeclValue copy();


    public boolean isBoolean() {
        return this instanceof DeclBoolean;
    }

    public boolean isNumber() {
        return this instanceof DeclNumber;
    }

    public boolean isString() {
        return this instanceof DeclString;
    }

    public boolean isObject() {
        return this instanceof DeclObject;
    }


    public boolean asBoolean() {
        throw ex("Boolean");
    }

    public Number asNumber() {
        throw ex("Number");
    }

    public String asString() {
        throw ex("String");
    }

    public DeclObject asObject() {
        throw ex("Object");
    }


    private RuntimeException ex(String type) {
        return new IllegalStateException("Value is not of type " + type);
    }

}
