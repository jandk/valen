package be.twofold.valen.gltf.types;

public final class Mat2 implements Primitive {
    private final float m00;
    private final float m01;
    private final float m10;
    private final float m11;

    public Mat2(
        float m00, float m01,
        float m10, float m11
    ) {
        this.m00 = m00;
        this.m01 = m01;
        this.m10 = m10;
        this.m11 = m11;
    }

    public float m00() {
        return m00;
    }

    public float m01() {
        return m01;
    }

    public float m10() {
        return m10;
    }

    public float m11() {
        return m11;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Mat2)) {
            return false;
        }

        Mat2 other = (Mat2) obj;
        return Float.floatToIntBits(m00) == Float.floatToIntBits(other.m00)
            && Float.floatToIntBits(m01) == Float.floatToIntBits(other.m01)
            && Float.floatToIntBits(m10) == Float.floatToIntBits(other.m10)
            && Float.floatToIntBits(m11) == Float.floatToIntBits(other.m11);
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + Float.hashCode(m00);
        result = 31 * result + Float.hashCode(m01);
        result = 31 * result + Float.hashCode(m10);
        result = 31 * result + Float.hashCode(m11);
        return result;
    }

    @Override
    public String toString() {
        return "Mat2(" +
            "m00=" + m00 + ", " +
            "m01=" + m01 + ", " +
            "m10=" + m10 + ", " +
            "m11=" + m11 + ")";
    }
}
