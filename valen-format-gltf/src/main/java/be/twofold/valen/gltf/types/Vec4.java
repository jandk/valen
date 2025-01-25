package be.twofold.valen.gltf.types;

public final class Vec4 implements Primitive {
    private final float x;
    private final float y;
    private final float z;
    private final float w;

    public Vec4(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public float x() {
        return x;
    }

    public float y() {
        return y;
    }

    public float z() {
        return z;
    }

    public float w() {
        return w;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Vec4)) {
            return false;
        }
        var other = (Vec4) obj;
        return Float.floatToIntBits(this.x) == Float.floatToIntBits(other.x)
            && Float.floatToIntBits(this.y) == Float.floatToIntBits(other.y)
            && Float.floatToIntBits(this.z) == Float.floatToIntBits(other.z)
            && Float.floatToIntBits(this.w) == Float.floatToIntBits(other.w);
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + Float.floatToIntBits(x);
        result = 31 * result + Float.floatToIntBits(y);
        result = 31 * result + Float.floatToIntBits(z);
        result = 31 * result + Float.floatToIntBits(w);
        return result;
    }

    @Override
    public String toString() {
        return "Vec4(" +
            "x=" + x + ", " +
            "y=" + y + ", " +
            "z=" + z + ", " +
            "w=" + w + ")";
    }

}
