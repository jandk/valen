package be.twofold.valen.gltf.types;

public final class Vec2 implements Primitive {
    private final float x;
    private final float y;

    public Vec2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float x() {
        return x;
    }

    public float y() {
        return y;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Vec2)) {
            return false;
        }
        var other = (Vec2) obj;
        return Float.floatToIntBits(this.x) == Float.floatToIntBits(other.x)
            && Float.floatToIntBits(this.y) == Float.floatToIntBits(other.y);
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + Float.floatToIntBits(x);
        result = 31 * result + Float.floatToIntBits(y);
        return result;
    }

    @Override
    public String toString() {
        return "Vec2(" +
            "x=" + x + ", " +
            "y=" + y + ")";
    }
}
