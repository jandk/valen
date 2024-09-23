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

    public static Matrix4 identity() {
        return new Matrix4(
            1, 0, 0, 0,
            0, 1, 0, 0,
            0, 0, 1, 0,
            0, 0, 0, 1
        );
    }

    public Vector3 translation() {
        return new Vector3(m30, m31, m32);
    }

    public Quaternion rotation() {
        // Extract the columns of the rotation matrix
        Vector3 col0 = new Vector3(m00, m10, m20);
        Vector3 col1 = new Vector3(m01, m11, m21);
        Vector3 col2 = new Vector3(m02, m12, m22);

        // Extract scaling factors
        float scaleX = col0.length();
        float scaleY = col1.length();
        float scaleZ = col2.length();

        // Prevent division by zero
        if (scaleX == 0 || scaleY == 0 || scaleZ == 0) {
            // Handle the error as appropriate for your application
            return Quaternion.Identity;
        }

        // Normalize the columns to remove scaling
        Vector3 normCol0 = col0.divide(scaleX);
        Vector3 normCol1 = col1.divide(scaleY);
        Vector3 normCol2 = col2.divide(scaleZ);

        // Reconstruct the normalized rotation matrix elements
        float r00 = normCol0.x();
        float r01 = normCol1.x();
        float r02 = normCol2.x();

        float r10 = normCol0.y();
        float r11 = normCol1.y();
        float r12 = normCol2.y();

        float r20 = normCol0.z();
        float r21 = normCol1.z();
        float r22 = normCol2.z();

        // Compute the trace of the matrix
        float trace = r00 + r11 + r22;
        float w, x, y, z;

        if (trace > 0) {
            float s = 0.5f / (float) Math.sqrt(trace + 1.0f);
            w = 0.25f / s;
            x = (r21 - r12) * s;
            y = (r02 - r20) * s;
            z = (r10 - r01) * s;
        } else if (r00 > r11 && r00 > r22) {
            float s = 2.0f * (float) Math.sqrt(1.0f + r00 - r11 - r22);
            w = (r21 - r12) / s;
            x = 0.25f * s;
            y = (r01 + r10) / s;
            z = (r02 + r20) / s;
        } else if (r11 > r22) {
            float s = 2.0f * (float) Math.sqrt(1.0f + r11 - r00 - r22);
            w = (r02 - r20) / s;
            x = (r01 + r10) / s;
            y = 0.25f * s;
            z = (r12 + r21) / s;
        } else {
            float s = 2.0f * (float) Math.sqrt(1.0f + r22 - r00 - r11);
            w = (r10 - r01) / s;
            x = (r02 + r20) / s;
            y = (r12 + r21) / s;
            z = 0.25f * s;
        }

        return new Quaternion(x, y, z, w);
    }

    public Vector3 scale() {
        var row0 = new Vector3(m00, m10, m20);
        var row1 = new Vector3(m01, m11, m21);
        var row2 = new Vector3(m02, m12, m22);
        return new Vector3(row0.length(), row1.length(), row2.length());
    }

    public Matrix4 transpose() {
        return new Matrix4(
            m00, m10, m20, m30,
            m01, m11, m21, m31,
            m02, m12, m22, m32,
            m03, m13, m23, m33
        );
    }

    public Matrix4 inverse() {
        float[] m = this.toArray();

        float[] inv = new float[16];

        inv[0] = m[5] * m[10] * m[15] -
            m[5] * m[11] * m[14] -
            m[9] * m[6] * m[15] +
            m[9] * m[7] * m[14] +
            m[13] * m[6] * m[11] -
            m[13] * m[7] * m[10];

        inv[1] = -m[1] * m[10] * m[15] +
            m[1] * m[11] * m[14] +
            m[9] * m[2] * m[15] -
            m[9] * m[3] * m[14] -
            m[13] * m[2] * m[11] +
            m[13] * m[3] * m[10];

        inv[2] = m[1] * m[6] * m[15] -
            m[1] * m[7] * m[14] -
            m[5] * m[2] * m[15] +
            m[5] * m[3] * m[14] +
            m[13] * m[2] * m[7] -
            m[13] * m[3] * m[6];

        inv[3] = -m[1] * m[6] * m[11] +
            m[1] * m[7] * m[10] +
            m[5] * m[2] * m[11] -
            m[5] * m[3] * m[10] -
            m[9] * m[2] * m[7] +
            m[9] * m[3] * m[6];

        inv[4] = -m[4] * m[10] * m[15] +
            m[4] * m[11] * m[14] +
            m[8] * m[6] * m[15] -
            m[8] * m[7] * m[14] -
            m[12] * m[6] * m[11] +
            m[12] * m[7] * m[10];

        inv[5] = m[0] * m[10] * m[15] -
            m[0] * m[11] * m[14] -
            m[8] * m[2] * m[15] +
            m[8] * m[3] * m[14] +
            m[12] * m[2] * m[11] -
            m[12] * m[3] * m[10];

        inv[6] = -m[0] * m[6] * m[15] +
            m[0] * m[7] * m[14] +
            m[4] * m[2] * m[15] -
            m[4] * m[3] * m[14] -
            m[12] * m[2] * m[7] +
            m[12] * m[3] * m[6];

        inv[7] = m[0] * m[6] * m[11] -
            m[0] * m[7] * m[10] -
            m[4] * m[2] * m[11] +
            m[4] * m[3] * m[10] +
            m[8] * m[2] * m[7] -
            m[8] * m[3] * m[6];

        inv[8] = m[4] * m[9] * m[15] -
            m[4] * m[11] * m[13] -
            m[8] * m[5] * m[15] +
            m[8] * m[7] * m[13] +
            m[12] * m[5] * m[11] -
            m[12] * m[7] * m[9];

        inv[9] = -m[0] * m[9] * m[15] +
            m[0] * m[11] * m[13] +
            m[8] * m[1] * m[15] -
            m[8] * m[3] * m[13] -
            m[12] * m[1] * m[11] +
            m[12] * m[3] * m[9];

        inv[10] = m[0] * m[5] * m[15] -
            m[0] * m[7] * m[13] -
            m[4] * m[1] * m[15] +
            m[4] * m[3] * m[13] +
            m[12] * m[1] * m[7] -
            m[12] * m[3] * m[5];

        inv[11] = -m[0] * m[5] * m[11] +
            m[0] * m[7] * m[9] +
            m[4] * m[1] * m[11] -
            m[4] * m[3] * m[9] -
            m[8] * m[1] * m[7] +
            m[8] * m[3] * m[5];

        inv[12] = -m[4] * m[9] * m[14] +
            m[4] * m[10] * m[13] +
            m[8] * m[5] * m[14] -
            m[8] * m[6] * m[13] -
            m[12] * m[5] * m[10] +
            m[12] * m[6] * m[9];

        inv[13] = m[0] * m[9] * m[14] -
            m[0] * m[10] * m[13] -
            m[8] * m[1] * m[14] +
            m[8] * m[2] * m[13] +
            m[12] * m[1] * m[10] -
            m[12] * m[2] * m[9];

        inv[14] = -m[0] * m[5] * m[14] +
            m[0] * m[6] * m[13] +
            m[4] * m[1] * m[14] -
            m[4] * m[2] * m[13] -
            m[12] * m[1] * m[6] +
            m[12] * m[2] * m[5];

        inv[15] = m[0] * m[5] * m[10] -
            m[0] * m[6] * m[9] -
            m[4] * m[1] * m[10] +
            m[4] * m[2] * m[9] +
            m[8] * m[1] * m[6] -
            m[8] * m[2] * m[5];

        float det = m[0] * inv[0] + m[1] * inv[4] + m[2] * inv[8] + m[3] * inv[12];

        if (Math.abs(det) < 1e-6f) {
            throw new ArithmeticException("Matrix is singular and cannot be inverted.");
        }

        det = 1.0f / det;

        float[] invOut = new float[16];
        for (int i = 0; i < 16; i++) {
            invOut[i] = inv[i] * det;
        }

        return new Matrix4(
            invOut[0], invOut[1], invOut[2], invOut[3],
            invOut[4], invOut[5], invOut[6], invOut[7],
            invOut[8], invOut[9], invOut[10], invOut[11],
            invOut[12], invOut[13], invOut[14], invOut[15]
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
