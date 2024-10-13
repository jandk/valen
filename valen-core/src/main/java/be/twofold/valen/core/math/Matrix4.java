package be.twofold.valen.core.math;

import be.twofold.valen.core.io.*;

import java.io.*;

public record Matrix4(
    float m00, float m01, float m02, float m03,
    float m10, float m11, float m12, float m13,
    float m20, float m21, float m22, float m23,
    float m30, float m31, float m32, float m33
) {

    // region Constants and Factories

    public static Matrix4 Zero = new Matrix4(
        0.0f, 0.0f, 0.0f, 0.0f,
        0.0f, 0.0f, 0.0f, 0.0f,
        0.0f, 0.0f, 0.0f, 0.0f,
        0.0f, 0.0f, 0.0f, 0.0f
    );

    public static Matrix4 Identity = new Matrix4(
        1.0f, 0.0f, 0.0f, 0.0f,
        0.0f, 1.0f, 0.0f, 0.0f,
        0.0f, 0.0f, 1.0f, 0.0f,
        0.0f, 0.0f, 0.0f, 1.0f
    );

    public static Matrix4 fromRotationX(float angle) {
        float sin = MathF.sin(angle);
        float cos = MathF.cos(angle);

        return new Matrix4(
            1.0f, 0.0f, 0.0f, 0.0f,
            0.0f, +cos, +sin, 0.0f,
            0.0f, -sin, +cos, 0.0f,
            0.0f, 0.0f, 0.0f, 1.0f
        );
    }

    public static Matrix4 fromRotationY(float angle) {
        float sin = MathF.sin(angle);
        float cos = MathF.cos(angle);

        return new Matrix4(
            +cos, 0.0f, -sin, 0.0f,
            0.0f, 1.0f, 0.0f, 0.0f,
            +sin, 0.0f, +cos, 0.0f,
            0.0f, 0.0f, 0.0f, 1.0f
        );
    }

    public static Matrix4 fromRotationZ(float angle) {
        float sin = MathF.sin(angle);
        float cos = MathF.cos(angle);

        return new Matrix4(
            +cos, +sin, 0.0f, 0.0f,
            -sin, +cos, 0.0f, 0.0f,
            0.0f, 0.0f, 1.0f, 0.0f,
            0.0f, 0.0f, 0.0f, 1.0f
        );
    }

    public static Matrix4 fromScale(Vector3 scale) {
        return fromScale(scale.x(), scale.y(), scale.z());
    }

    public static Matrix4 fromScale(float sclX, float sclY, float sclZ) {
        return new Matrix4(
            sclX, 0.0f, 0.0f, 0.0f,
            0.0f, sclY, 0.0f, 0.0f,
            0.0f, 0.0f, sclZ, 0.0f,
            0.0f, 0.0f, 0.0f, 1.0f
        );
    }

    public static Matrix4 fromTranslation(Vector3 translation) {
        return fromTranslation(translation.x(), translation.y(), translation.z());
    }

    public static Matrix4 fromTranslation(float trnX, float trnY, float trnZ) {
        return new Matrix4(
            1.0f, 0.0f, 0.0f, 0.0f,
            0.0f, 1.0f, 0.0f, 0.0f,
            0.0f, 0.0f, 1.0f, 0.0f,
            trnX, trnY, trnZ, 1.0f
        );
    }

    public static Matrix4 fromArray(float[] array) {
        return new Matrix4(
            array[+0], array[+1], array[+2], array[+3],
            array[+4], array[+5], array[+6], array[+7],
            array[+8], array[+9], array[10], array[11],
            array[12], array[13], array[14], array[15]
        );
    }

    public static Matrix4 read(DataSource source) throws IOException {
        float m00 = source.readFloat();
        float m01 = source.readFloat();
        float m02 = source.readFloat();
        float m03 = source.readFloat();
        float m10 = source.readFloat();
        float m11 = source.readFloat();
        float m12 = source.readFloat();
        float m13 = source.readFloat();
        float m20 = source.readFloat();
        float m21 = source.readFloat();
        float m22 = source.readFloat();
        float m23 = source.readFloat();
        float m30 = source.readFloat();
        float m31 = source.readFloat();
        float m32 = source.readFloat();
        float m33 = source.readFloat();

        return new Matrix4(
            m00, m01, m02, m03,
            m10, m11, m12, m13,
            m20, m21, m22, m23,
            m30, m31, m32, m33
        );
    }

    // endregion

    public Matrix4 add(Matrix4 other) {
        return new Matrix4(
            m00 + other.m00, m01 + other.m01, m02 + other.m02, m03 + other.m03,
            m10 + other.m10, m11 + other.m11, m12 + other.m12, m13 + other.m13,
            m20 + other.m20, m21 + other.m21, m22 + other.m22, m23 + other.m23,
            m30 + other.m30, m31 + other.m31, m32 + other.m32, m33 + other.m33
        );
    }

    public Matrix4 subtract(Matrix4 other) {
        return add(other.negate());
    }

    public Matrix4 multiply(float scalar) {
        return new Matrix4(
            m00 + scalar, m01 + scalar, m02 + scalar, m03 + scalar,
            m10 + scalar, m11 + scalar, m12 + scalar, m13 + scalar,
            m20 + scalar, m21 + scalar, m22 + scalar, m23 + scalar,
            m30 + scalar, m31 + scalar, m32 + scalar, m33 + scalar
        );
    }

    public Matrix4 divide(float scalar) {
        return multiply(1.0f / scalar);
    }

    public Matrix4 negate() {
        return new Matrix4(
            -m00, -m01, -m02, -m03,
            -m10, -m11, -m12, -m13,
            -m20, -m21, -m22, -m23,
            -m30, -m31, -m32, -m33
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

    // Custom methods

    public Matrix4 multiply(Matrix4 other) {
        float m00 = this.m00 * other.m00 + this.m10 * other.m01 + this.m20 * other.m02 + this.m30 * other.m03;
        float m01 = this.m01 * other.m00 + this.m11 * other.m01 + this.m21 * other.m02 + this.m31 * other.m03;
        float m02 = this.m02 * other.m00 + this.m12 * other.m01 + this.m22 * other.m02 + this.m32 * other.m03;
        float m03 = this.m03 * other.m00 + this.m13 * other.m01 + this.m23 * other.m02 + this.m33 * other.m03;
        float m10 = this.m00 * other.m10 + this.m10 * other.m11 + this.m20 * other.m12 + this.m30 * other.m13;
        float m11 = this.m01 * other.m10 + this.m11 * other.m11 + this.m21 * other.m12 + this.m31 * other.m13;
        float m12 = this.m02 * other.m10 + this.m12 * other.m11 + this.m22 * other.m12 + this.m32 * other.m13;
        float m13 = this.m03 * other.m10 + this.m13 * other.m11 + this.m23 * other.m12 + this.m33 * other.m13;
        float m20 = this.m00 * other.m20 + this.m10 * other.m21 + this.m20 * other.m22 + this.m30 * other.m23;
        float m21 = this.m01 * other.m20 + this.m11 * other.m21 + this.m21 * other.m22 + this.m31 * other.m23;
        float m22 = this.m02 * other.m20 + this.m12 * other.m21 + this.m22 * other.m22 + this.m32 * other.m23;
        float m23 = this.m03 * other.m20 + this.m13 * other.m21 + this.m23 * other.m22 + this.m33 * other.m23;
        float m30 = this.m00 * other.m30 + this.m10 * other.m31 + this.m20 * other.m32 + this.m30 * other.m33;
        float m31 = this.m01 * other.m30 + this.m11 * other.m31 + this.m21 * other.m32 + this.m31 * other.m33;
        float m32 = this.m02 * other.m30 + this.m12 * other.m31 + this.m22 * other.m32 + this.m32 * other.m33;
        float m33 = this.m03 * other.m30 + this.m13 * other.m31 + this.m23 * other.m32 + this.m33 * other.m33;

        return new Matrix4(
            m00, m01, m02, m03,
            m10, m11, m12, m13,
            m20, m21, m22, m23,
            m30, m31, m32, m33
        );
    }

    public float determinant() {
        float a0 = m00 * m11 - m01 * m10;
        float a1 = m00 * m12 - m02 * m10;
        float a2 = m00 * m13 - m03 * m10;
        float a3 = m01 * m12 - m02 * m11;
        float a4 = m01 * m13 - m03 * m11;
        float a5 = m02 * m13 - m03 * m12;
        float b0 = m20 * m31 - m21 * m30;
        float b1 = m20 * m32 - m22 * m30;
        float b2 = m20 * m33 - m23 * m30;
        float b3 = m21 * m32 - m22 * m31;
        float b4 = m21 * m33 - m23 * m31;
        float b5 = m22 * m33 - m23 * m32;
        return a0 * b5 - a1 * b4 + a2 * b3 + a3 * b2 - a4 * b1 + a5 * b0;
    }

    public Matrix4 inverse() {
        float a0 = m00 * m11 - m01 * m10;
        float a1 = m00 * m12 - m02 * m10;
        float a2 = m00 * m13 - m03 * m10;
        float a3 = m01 * m12 - m02 * m11;
        float a4 = m01 * m13 - m03 * m11;
        float a5 = m02 * m13 - m03 * m12;
        float b0 = m20 * m31 - m21 * m30;
        float b1 = m20 * m32 - m22 * m30;
        float b2 = m20 * m33 - m23 * m30;
        float b3 = m21 * m32 - m22 * m31;
        float b4 = m21 * m33 - m23 * m31;
        float b5 = m22 * m33 - m23 * m32;
        float determinant = a0 * b5 - a1 * b4 + a2 * b3 + a3 * b2 - a4 * b1 + a5 * b0;
        assert determinant != 0.0f;

        return new Matrix4(
            +this.m11 * b5 - this.m12 * b4 + this.m13 * b3,
            -this.m01 * b5 + this.m02 * b4 - this.m03 * b3,
            +this.m31 * a5 - this.m32 * a4 + this.m33 * a3,
            -this.m21 * a5 + this.m22 * a4 - this.m23 * a3,
            -this.m10 * b5 + this.m12 * b2 - this.m13 * b1,
            +this.m00 * b5 - this.m02 * b2 + this.m03 * b1,
            -this.m30 * a5 + this.m32 * a2 - this.m33 * a1,
            +this.m20 * a5 - this.m22 * a2 + this.m23 * a1,
            +this.m10 * b4 - this.m11 * b2 + this.m13 * b0,
            -this.m00 * b4 + this.m01 * b2 - this.m03 * b0,
            +this.m30 * a4 - this.m31 * a2 + this.m33 * a0,
            -this.m20 * a4 + this.m21 * a2 - this.m23 * a0,
            -this.m10 * b3 + this.m11 * b1 - this.m12 * b0,
            +this.m00 * b3 - this.m01 * b1 + this.m02 * b0,
            -this.m30 * a3 + this.m31 * a1 - this.m32 * a0,
            +this.m20 * a3 - this.m21 * a1 + this.m22 * a0
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

    public Vector3 toTranslation() {
        return new Vector3(m30, m31, m32);
    }

    public float[] toArray() {
        return new float[]{
            m00, m01, m02, m03,
            m10, m11, m12, m13,
            m20, m21, m22, m23,
            m30, m31, m32, m33
        };
    }

    // Object methods

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
            "(" + m00 + ", " + m10 + ", " + m20 + ", " + m30 + "), " +
            "(" + m01 + ", " + m11 + ", " + m21 + ", " + m31 + "), " +
            "(" + m02 + ", " + m12 + ", " + m22 + ", " + m32 + "), " +
            "(" + m03 + ", " + m13 + ", " + m23 + ", " + m33 + ")" +
            ")";
    }
}
