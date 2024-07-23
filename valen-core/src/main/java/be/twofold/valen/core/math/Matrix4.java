package be.twofold.valen.core.math;

public record Matrix4(
    float m00, float m01, float m02, float m03,
    float m10, float m11, float m12, float m13,
    float m20, float m21, float m22, float m23,
    float m30, float m31, float m32, float m33
) {
    public static Matrix4 fromArray(float[] array) {
        return new Matrix4(
            array[+0], array[+1], array[+2], array[+3],
            array[+4], array[+5], array[+6], array[+7],
            array[+8], array[+9], array[10], array[11],
            array[12], array[13], array[14], array[15]
        );
    }

    public Matrix4 transpose() {
        return new Matrix4(
            m00, m10, m20, m30,
            m01, m11, m21, m31,
            m02, m12, m22, m32,
            m03, m13, m23, m33
        );
    }

    public float[] toArray() {
        return new float[]{
            m00, m01, m02, m03,
            m10, m11, m12, m13,
            m20, m21, m22, m23,
            m30, m31, m32, m33
        };
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || obj instanceof Matrix4 other
            && MathF.equals(m00, other.m00)
            && MathF.equals(m01, other.m01)
            && MathF.equals(m02, other.m02)
            && MathF.equals(m03, other.m03)
            && MathF.equals(m10, other.m10)
            && MathF.equals(m11, other.m11)
            && MathF.equals(m12, other.m12)
            && MathF.equals(m13, other.m13)
            && MathF.equals(m20, other.m20)
            && MathF.equals(m21, other.m21)
            && MathF.equals(m22, other.m22)
            && MathF.equals(m23, other.m23)
            && MathF.equals(m30, other.m30)
            && MathF.equals(m31, other.m31)
            && MathF.equals(m32, other.m32)
            && MathF.equals(m33, other.m33);
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + MathF.hashCode(m00);
        result = 31 * result + MathF.hashCode(m01);
        result = 31 * result + MathF.hashCode(m02);
        result = 31 * result + MathF.hashCode(m03);
        result = 31 * result + MathF.hashCode(m10);
        result = 31 * result + MathF.hashCode(m11);
        result = 31 * result + MathF.hashCode(m12);
        result = 31 * result + MathF.hashCode(m13);
        result = 31 * result + MathF.hashCode(m20);
        result = 31 * result + MathF.hashCode(m21);
        result = 31 * result + MathF.hashCode(m22);
        result = 31 * result + MathF.hashCode(m23);
        result = 31 * result + MathF.hashCode(m30);
        result = 31 * result + MathF.hashCode(m31);
        result = 31 * result + MathF.hashCode(m32);
        result = 31 * result + MathF.hashCode(m33);
        return result;
    }

    @Override
    public String toString() {
        return "(" +
            m00 + ", " + m01 + ", " + m02 + ", " + m03 + ", " +
            m10 + ", " + m11 + ", " + m12 + ", " + m13 + ", " +
            m20 + ", " + m21 + ", " + m22 + ", " + m23 + ", " +
            m30 + ", " + m31 + ", " + m32 + ", " + m33 +
            ")";
    }
}
