package be.twofold.valen.format.gltf.types;

public final class Scalar implements Primitive {
    private final float value;

    public Scalar(float value) {
        this.value = value;
    }

    public float value() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Scalar
            && Float.floatToIntBits(value) == Float.floatToIntBits(((Scalar) obj).value);
    }

    @Override
    public int hashCode() {
        return Float.hashCode(value);
    }

    @Override
    public String toString() {
        return "Scalar(value=" + value + ")";
    }
}
