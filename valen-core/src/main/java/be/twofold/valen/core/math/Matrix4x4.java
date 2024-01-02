package be.twofold.valen.core.math;

public record Matrix4x4(
    float m00, float m01, float m02, float m03,
    float m10, float m11, float m12, float m13,
    float m20, float m21, float m22, float m23,
    float m30, float m31, float m32, float m33
) {
    public static Matrix4x4 fromArray(float[] array) {
        return new Matrix4x4(
            array[+0], array[+1], array[+2], array[+3],
            array[+4], array[+5], array[+6], array[+7],
            array[+8], array[+9], array[10], array[11],
            array[12], array[13], array[14], array[15]
        );
    }

    public Matrix4x4 transpose() {
        return new Matrix4x4(
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
    public String toString() {
        return "(" +
            m00 + ", " + m01 + ", " + m02 + ", " + m03 + ", " +
            m10 + ", " + m11 + ", " + m12 + ", " + m13 + ", " +
            m20 + ", " + m21 + ", " + m22 + ", " + m23 + ", " +
            m30 + ", " + m31 + ", " + m32 + ", " + m33 +
            ")";
    }
}
