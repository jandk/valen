package be.twofold.valen.gltf.types;

public final class Mat3 implements Primitive {
    private final float m00;
    private final float m01;
    private final float m02;
    private final float m10;
    private final float m11;
    private final float m12;
    private final float m20;
    private final float m21;
    private final float m22;

    public Mat3(
        float m00, float m01, float m02,
        float m10, float m11, float m12,
        float m20, float m21, float m22
    ) {
        this.m00 = m00;
        this.m01 = m01;
        this.m02 = m02;
        this.m10 = m10;
        this.m11 = m11;
        this.m12 = m12;
        this.m20 = m20;
        this.m21 = m21;
        this.m22 = m22;
    }

    public float m00() {
        return m00;
    }

    public float m01() {
        return m01;
    }

    public float m02() {
        return m02;
    }

    public float m10() {
        return m10;
    }

    public float m11() {
        return m11;
    }

    public float m12() {
        return m12;
    }

    public float m20() {
        return m20;
    }

    public float m21() {
        return m21;
    }

    public float m22() {
        return m22;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Mat3)) {
            return false;
        }

        Mat3 other = (Mat3) obj;
        return Float.floatToIntBits(m00) == Float.floatToIntBits(other.m00)
            && Float.floatToIntBits(m01) == Float.floatToIntBits(other.m01)
            && Float.floatToIntBits(m02) == Float.floatToIntBits(other.m02)
            && Float.floatToIntBits(m10) == Float.floatToIntBits(other.m10)
            && Float.floatToIntBits(m11) == Float.floatToIntBits(other.m11)
            && Float.floatToIntBits(m12) == Float.floatToIntBits(other.m12)
            && Float.floatToIntBits(m20) == Float.floatToIntBits(other.m20)
            && Float.floatToIntBits(m21) == Float.floatToIntBits(other.m21)
            && Float.floatToIntBits(m22) == Float.floatToIntBits(other.m22);
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + Float.hashCode(m00);
        result = 31 * result + Float.hashCode(m01);
        result = 31 * result + Float.hashCode(m02);
        result = 31 * result + Float.hashCode(m10);
        result = 31 * result + Float.hashCode(m11);
        result = 31 * result + Float.hashCode(m12);
        result = 31 * result + Float.hashCode(m20);
        result = 31 * result + Float.hashCode(m21);
        result = 31 * result + Float.hashCode(m22);
        return result;
    }

    @Override
    public String toString() {
        return "Mat3(" +
            "m00=" + m00 + ", " +
            "m01=" + m01 + ", " +
            "m02=" + m02 + ", " +
            "m10=" + m10 + ", " +
            "m11=" + m11 + ", " +
            "m12=" + m12 + ", " +
            "m20=" + m20 + ", " +
            "m21=" + m21 + ", " +
            "m22=" + m22 + ")";
    }
}
