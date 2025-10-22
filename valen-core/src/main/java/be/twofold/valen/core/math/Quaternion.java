package be.twofold.valen.core.math;

import be.twofold.valen.core.io.*;

import java.io.*;
import java.nio.*;

public record Quaternion(float x, float y, float z, float w) {
    public static final Quaternion Identity = new Quaternion(0.0f, 0.0f, 0.0f, 1.0f);

    public static Quaternion fromAxisAngle(Vector3 axis, float angle, Angle unit) {
        float halfAngle = unit.toRadians(angle) * 0.5f;
        float sin = MathF.sin(halfAngle);
        float cos = MathF.cos(halfAngle);
        float x = axis.x() * sin;
        float y = axis.y() * sin;
        float z = axis.z() * sin;
        return new Quaternion(x, y, z, cos);
    }

    public static Quaternion read(BinaryReader reader) throws IOException {
        float x = reader.readFloat();
        float y = reader.readFloat();
        float z = reader.readFloat();
        float w = reader.readFloat();
        return new Quaternion(x, y, z, w);
    }

    static Quaternion fromMatrix(
        float m00, float m01, float m02,
        float m10, float m11, float m12,
        float m20, float m21, float m22
    ) {
        float xLength = MathF.invSqrt(m00 * m00 + m01 * m01 + m02 * m02);
        float yLength = MathF.invSqrt(m10 * m10 + m11 * m11 + m12 * m12);
        float zLength = MathF.invSqrt(m20 * m20 + m21 * m21 + m22 * m22);

        return fromMatrixNormalized(
            m00 * xLength, m01 * xLength, m02 * xLength,
            m10 * yLength, m11 * yLength, m12 * yLength,
            m20 * zLength, m21 * zLength, m22 * zLength
        );
    }

    static Quaternion fromMatrixNormalized(
        float m00, float m01, float m02,
        float m10, float m11, float m12,
        float m20, float m21, float m22
    ) {
        if (m22 <= 0.0f) { // x^2 + y^2 >= z^2 + w^2
            float dif10 = m11 - m00;
            float omm22 = 1.0f - m22;
            if (dif10 <= 0.0f) { // x^2 >= y^2
                float four_xsq = omm22 - dif10;
                return new Quaternion(four_xsq, m01 + m10, m02 + m20, m12 - m21)
                    .multiply(0.5f / MathF.sqrt(four_xsq));
            } else { // y^2 >= x^2
                float four_ysq = omm22 + dif10;
                return new Quaternion(m01 + m10, four_ysq, m12 + m21, m20 - m02)
                    .multiply(0.5f / MathF.sqrt(four_ysq));
            }
        } else { // z^2 + w^2 >= x^2 + y^2
            float sum10 = m11 + m00;
            float opm22 = 1.0f + m22;
            if (sum10 <= 0.0f) { // z^2 >= w^2
                float four_zsq = opm22 - sum10;
                return new Quaternion(m02 + m20, m12 + m21, four_zsq, m01 - m10)
                    .multiply(0.5f / MathF.sqrt(four_zsq));
            } else { // w^2 >= z^2
                float four_wsq = opm22 + sum10;
                return new Quaternion(m12 - m21, m20 - m02, m01 - m10, four_wsq)
                    .multiply(0.5f / MathF.sqrt(four_wsq));
            }
        }
    }

    public Quaternion add(Quaternion other) {
        return new Quaternion(x + other.x, y + other.y, z + other.z, w + other.w);
    }

    public Quaternion subtract(Quaternion other) {
        return add(other.negate());
    }

    public Quaternion multiply(float scalar) {
        return new Quaternion(x * scalar, y * scalar, z * scalar, w * scalar);
    }

    public Quaternion divide(float scalar) {
        return multiply(1.0f / scalar);
    }

    public Quaternion negate() {
        return new Quaternion(-x, -y, -z, -w);
    }

    public float dot(Quaternion other) {
        return x * other.x + y * other.y + z * other.z + w * other.w;
    }

    public float lengthSquared() {
        return dot(this);
    }

    public float length() {
        return MathF.sqrt(lengthSquared());
    }

    public Quaternion normalize() {
        return divide(length());
    }

    // Custom methods

    public Quaternion multiply(Quaternion other) {
        return new Quaternion(
            w * other.x + x * other.w + y * other.z - z * other.y,
            w * other.y - x * other.z + y * other.w + z * other.x,
            w * other.z + x * other.y - y * other.x + z * other.w,
            w * other.w - x * other.x - y * other.y - z * other.z
        );
    }

    /**
     * Returns the conjugate of this quaternion.
     */
    public Quaternion conjugate() {
        return new Quaternion(-x, -y, -z, w);
    }

    /**
     * Returns the multiplicative inverse of this quaternion.
     * For unit quaternions this equals the conjugate.
     */
    public Quaternion invert() {
        float invLenSq = 1.0f / lengthSquared();
        return new Quaternion(-x * invLenSq, -y * invLenSq, -z * invLenSq, w * invLenSq);
    }

    public void toBuffer(FloatBuffer buffer) {
        buffer.put(x);
        buffer.put(y);
        buffer.put(z);
        buffer.put(w);
    }

    // Object methods

    @Override
    public boolean equals(Object obj) {
        return this == obj || obj instanceof Quaternion other
            && MathF.equals(x, other.x)
            && MathF.equals(y, other.y)
            && MathF.equals(z, other.z)
            && MathF.equals(w, other.w);
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + MathF.hashCode(x);
        result = 31 * result + MathF.hashCode(y);
        result = 31 * result + MathF.hashCode(z);
        result = 31 * result + MathF.hashCode(w);
        return result;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ", " + z + ", " + w + ")";
    }
}
