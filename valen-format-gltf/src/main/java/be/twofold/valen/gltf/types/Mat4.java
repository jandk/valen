package be.twofold.valen.gltf.types;

public final class Mat4 implements Primitive {
    private final float m00;
    private final float m01;
    private final float m02;
    private final float m03;
    private final float m10;
    private final float m11;
    private final float m12;
    private final float m13;
    private final float m20;
    private final float m21;
    private final float m22;
    private final float m23;
    private final float m30;
    private final float m31;
    private final float m32;
    private final float m33;

    public Mat4(
        float m00, float m01, float m02, float m03,
        float m10, float m11, float m12, float m13,
        float m20, float m21, float m22, float m23,
        float m30, float m31, float m32, float m33
    ) {
        this.m00 = m00;
        this.m01 = m01;
        this.m02 = m02;
        this.m03 = m03;
        this.m10 = m10;
        this.m11 = m11;
        this.m12 = m12;
        this.m13 = m13;
        this.m20 = m20;
        this.m21 = m21;
        this.m22 = m22;
        this.m23 = m23;
        this.m30 = m30;
        this.m31 = m31;
        this.m32 = m32;
        this.m33 = m33;
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

    public float m03() {
        return m03;
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

    public float m13() {
        return m13;
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

    public float m23() {
        return m23;
    }

    public float m30() {
        return m30;
    }

    public float m31() {
        return m31;
    }

    public float m32() {
        return m32;
    }

    public float m33() {
        return m33;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Mat4)) {
            return false;
        }

        Mat4 other = (Mat4) obj;
        return Float.floatToIntBits(m00) == Float.floatToIntBits(other.m00)
            && Float.floatToIntBits(m01) == Float.floatToIntBits(other.m01)
            && Float.floatToIntBits(m02) == Float.floatToIntBits(other.m02)
            && Float.floatToIntBits(m03) == Float.floatToIntBits(other.m03)
            && Float.floatToIntBits(m10) == Float.floatToIntBits(other.m10)
            && Float.floatToIntBits(m11) == Float.floatToIntBits(other.m11)
            && Float.floatToIntBits(m12) == Float.floatToIntBits(other.m12)
            && Float.floatToIntBits(m13) == Float.floatToIntBits(other.m13)
            && Float.floatToIntBits(m20) == Float.floatToIntBits(other.m20)
            && Float.floatToIntBits(m21) == Float.floatToIntBits(other.m21)
            && Float.floatToIntBits(m22) == Float.floatToIntBits(other.m22)
            && Float.floatToIntBits(m23) == Float.floatToIntBits(other.m23)
            && Float.floatToIntBits(m30) == Float.floatToIntBits(other.m30)
            && Float.floatToIntBits(m31) == Float.floatToIntBits(other.m31)
            && Float.floatToIntBits(m32) == Float.floatToIntBits(other.m32)
            && Float.floatToIntBits(m33) == Float.floatToIntBits(other.m33);
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + Float.hashCode(m00);
        result = 31 * result + Float.hashCode(m01);
        result = 31 * result + Float.hashCode(m02);
        result = 31 * result + Float.hashCode(m03);
        result = 31 * result + Float.hashCode(m10);
        result = 31 * result + Float.hashCode(m11);
        result = 31 * result + Float.hashCode(m12);
        result = 31 * result + Float.hashCode(m13);
        result = 31 * result + Float.hashCode(m20);
        result = 31 * result + Float.hashCode(m21);
        result = 31 * result + Float.hashCode(m22);
        result = 31 * result + Float.hashCode(m23);
        result = 31 * result + Float.hashCode(m30);
        result = 31 * result + Float.hashCode(m31);
        result = 31 * result + Float.hashCode(m32);
        result = 31 * result + Float.hashCode(m33);
        return result;
    }

    @Override
    public String toString() {
        return "Mat4(" +
            "m00=" + m00 + ", " +
            "m01=" + m01 + ", " +
            "m02=" + m02 + ", " +
            "m03=" + m03 + ", " +
            "m10=" + m10 + ", " +
            "m11=" + m11 + ", " +
            "m12=" + m12 + ", " +
            "m13=" + m13 + ", " +
            "m20=" + m20 + ", " +
            "m21=" + m21 + ", " +
            "m22=" + m22 + ", " +
            "m23=" + m23 + ", " +
            "m30=" + m30 + ", " +
            "m31=" + m31 + ", " +
            "m32=" + m32 + ", " +
            "m33=" + m33 + ")";
    }
}
