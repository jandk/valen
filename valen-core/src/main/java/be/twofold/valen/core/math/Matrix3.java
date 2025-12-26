package be.twofold.valen.core.math;

import be.twofold.valen.core.io.*;

import java.io.*;

public record Matrix3(
    float m00, float m01, float m02,
    float m10, float m11, float m12,
    float m20, float m21, float m22
) {

    // region Constants and Factories

    public static final Matrix3 Zero = new Matrix3(
        0.0f, 0.0f, 0.0f,
        0.0f, 0.0f, 0.0f,
        0.0f, 0.0f, 0.0f
    );

    public static final Matrix3 Identity = new Matrix3(
        1.0f, 0.0f, 0.0f,
        0.0f, 1.0f, 0.0f,
        0.0f, 0.0f, 1.0f
    );

    public static Matrix3 fromRotationX(float angle) {
        float sin = MathF.sin(angle);
        float cos = MathF.cos(angle);

        return new Matrix3(
            1.0f, 0.0f, 0.0f,
            0.0f, +cos, +sin,
            0.0f, -sin, +cos
        );
    }

    public static Matrix3 fromRotationY(float angle) {
        float sin = MathF.sin(angle);
        float cos = MathF.cos(angle);

        return new Matrix3(
            +cos, 0.0f, -sin,
            0.0f, 1.0f, 0.0f,
            +sin, 0.0f, +cos
        );
    }

    public static Matrix3 fromRotationZ(float angle) {
        float sin = MathF.sin(angle);
        float cos = MathF.cos(angle);

        return new Matrix3(
            +cos, +sin, 0.0f,
            -sin, +cos, 0.0f,
            0.0f, 0.0f, 1.0f
        );
    }

    public static Matrix3 fromScale(Vector3 scale) {
        return fromScale(scale.x(), scale.y(), scale.z());
    }

    public static Matrix3 fromScale(float sclX, float sclY, float sclZ) {
        return new Matrix3(
            sclX, 0.0f, 0.0f,
            0.0f, sclY, 0.0f,
            0.0f, 0.0f, sclZ
        );
    }

    public static Matrix3 fromArray(float[] array) {
        return new Matrix3(
            array[0], array[1], array[2],
            array[3], array[4], array[5],
            array[6], array[7], array[8]
        );
    }

    public static Matrix3 read(BinarySource source) throws IOException {
        float m00 = source.readFloat();
        float m01 = source.readFloat();
        float m02 = source.readFloat();
        float m10 = source.readFloat();
        float m11 = source.readFloat();
        float m12 = source.readFloat();
        float m20 = source.readFloat();
        float m21 = source.readFloat();
        float m22 = source.readFloat();
        return new Matrix3(
            m00, m01, m02,
            m10, m11, m12,
            m20, m21, m22
        );
    }

    // endregion

    public Matrix3 add(Matrix3 other) {
        return new Matrix3(
            m00 + other.m00, m01 + other.m01, m02 + other.m02,
            m10 + other.m10, m11 + other.m11, m12 + other.m12,
            m20 + other.m20, m21 + other.m21, m22 + other.m22
        );
    }

    public Matrix3 subtract(Matrix3 other) {
        return add(other.negate());
    }

    public Matrix3 multiply(float scalar) {
        return new Matrix3(
            m00 * scalar, m01 * scalar, m02 * scalar,
            m10 * scalar, m11 * scalar, m12 * scalar,
            m20 * scalar, m21 * scalar, m22 * scalar
        );
    }

    public Matrix3 divide(float scalar) {
        return multiply(1.0f / scalar);
    }

    public Matrix3 negate() {
        return new Matrix3(
            -m00, -m01, -m02,
            -m10, -m11, -m12,
            -m20, -m21, -m22
        );
    }

    public Matrix3 transpose() {
        return new Matrix3(
            m00, m10, m20,
            m01, m11, m21,
            m02, m12, m22
        );
    }

    // Custom methods

    public Matrix3 multiply(Matrix3 other) {
        float m00 = this.m00 * other.m00 + this.m10 * other.m01 + this.m20 * other.m02;
        float m01 = this.m01 * other.m00 + this.m11 * other.m01 + this.m21 * other.m02;
        float m02 = this.m02 * other.m00 + this.m12 * other.m01 + this.m22 * other.m02;
        float m10 = this.m00 * other.m10 + this.m10 * other.m11 + this.m20 * other.m12;
        float m11 = this.m01 * other.m10 + this.m11 * other.m11 + this.m21 * other.m12;
        float m12 = this.m02 * other.m10 + this.m12 * other.m11 + this.m22 * other.m12;
        float m20 = this.m00 * other.m20 + this.m10 * other.m21 + this.m20 * other.m22;
        float m21 = this.m01 * other.m20 + this.m11 * other.m21 + this.m21 * other.m22;
        float m22 = this.m02 * other.m20 + this.m12 * other.m21 + this.m22 * other.m22;

        return new Matrix3(
            m00, m01, m02,
            m10, m11, m12,
            m20, m21, m22
        );
    }

    public float determinant() {
        float c00 = m11 * m22 - m12 * m21;
        float c10 = m12 * m20 - m10 * m22;
        float c20 = m10 * m21 - m11 * m20;
        return m00 * c00 + m01 * c10 + m02 * c20;
    }

    public Matrix3 inverse() {
        float c00 = m11 * m22 - m12 * m21;
        float c10 = m12 * m20 - m10 * m22;
        float c20 = m10 * m21 - m11 * m20;
        float determinant = m00 * c00 + m01 * c10 + m02 * c20;
        assert determinant != 0.0f;

        return new Matrix3(
            c00,
            this.m02 * this.m21 - this.m01 * this.m22,
            this.m01 * this.m12 - this.m02 * this.m11,
            c10,
            this.m00 * this.m22 - this.m02 * this.m20,
            this.m02 * this.m10 - this.m00 * this.m12,
            c20,
            this.m01 * this.m20 - this.m00 * this.m21,
            this.m00 * this.m11 - this.m01 * this.m10
        ).divide(determinant);
    }

    public Vector3 toScale() {
        float x = MathF.sqrt(m00 * m00 + m01 * m01 + m02 * m02);
        float y = MathF.sqrt(m10 * m10 + m11 * m11 + m12 * m12);
        float z = MathF.sqrt(m20 * m20 + m21 * m21 + m22 * m22);
        return new Vector3(x, y, z);
    }

    public Quaternion toRotation() {
        return Quaternion.fromMatrix(
            m00, m01, m02,
            m10, m11, m12,
            m20, m21, m22
        );
    }

    public float[] toArray() {
        return new float[]{
            m00, m01, m02,
            m10, m11, m12,
            m20, m21, m22
        };
    }

    // Object methods

    @Override
    public boolean equals(Object obj) {
        return this == obj || obj instanceof Matrix3 other
            && MathF.equals(m00, other.m00)
            && MathF.equals(m01, other.m01)
            && MathF.equals(m02, other.m02)
            && MathF.equals(m10, other.m10)
            && MathF.equals(m11, other.m11)
            && MathF.equals(m12, other.m12)
            && MathF.equals(m20, other.m20)
            && MathF.equals(m21, other.m21)
            && MathF.equals(m22, other.m22);
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + MathF.hashCode(m00);
        result = 31 * result + MathF.hashCode(m01);
        result = 31 * result + MathF.hashCode(m02);
        result = 31 * result + MathF.hashCode(m10);
        result = 31 * result + MathF.hashCode(m11);
        result = 31 * result + MathF.hashCode(m12);
        result = 31 * result + MathF.hashCode(m20);
        result = 31 * result + MathF.hashCode(m21);
        result = 31 * result + MathF.hashCode(m22);
        return result;
    }

    @Override
    public String toString() {
        return "(" +
            "(" + m00 + ", " + m10 + ", " + m20 + "), " +
            "(" + m01 + ", " + m11 + ", " + m21 + "), " +
            "(" + m02 + ", " + m12 + ", " + m22 + ")" +
            ")";
    }
}
