package be.twofold.valen.gltf.types;

public final class Vec3 implements Primitive {
    private final float x;
    private final float y;
    private final float z;

    public Vec3(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
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

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Vec3)) {
            return false;
        }
        var other = (Vec3) obj;
        return Float.floatToIntBits(this.x) == Float.floatToIntBits(other.x)
            && Float.floatToIntBits(this.y) == Float.floatToIntBits(other.y)
            && Float.floatToIntBits(this.z) == Float.floatToIntBits(other.z);
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + Float.floatToIntBits(x);
        result = 31 * result + Float.floatToIntBits(y);
        result = 31 * result + Float.floatToIntBits(z);
        return result;
    }

    @Override
    public String toString() {
        return "Vec3(" +
            "x=" + x + ", " +
            "y=" + y + ", " +
            "z=" + z + ")";
    }
}
