package be.twofold.valen.core.math;

import be.twofold.valen.core.util.*;

public record Matrix3(
    float m00, float m01, float m02,
    float m10, float m11, float m12,
    float m20, float m21, float m22
) {
    public static Matrix3 read(BetterBuffer buffer) {
        float m00 = buffer.getFloat();
        float m01 = buffer.getFloat();
        float m02 = buffer.getFloat();
        float m10 = buffer.getFloat();
        float m11 = buffer.getFloat();
        float m12 = buffer.getFloat();
        float m20 = buffer.getFloat();
        float m21 = buffer.getFloat();
        float m22 = buffer.getFloat();
        return new Matrix3(
            m00, m01, m02,
            m10, m11, m12,
            m20, m21, m22
        );
    }

    public Quaternion rotation() {
        float trace = m00 + m11 + m22;
        if (trace >= 0.0f) {
            float t = MathF.sqrt(trace + 1.0f);
            float w = t * 0.5f;
            t = 0.5f / t;
            float x = (m12 - m21) * t;
            float y = (m20 - m02) * t;
            float z = (m01 - m10) * t;
            return new Quaternion(x, y, z, w);
        } else if (m00 >= m11 && m00 >= m22) {
            float t = MathF.sqrt(m00 - (m11 + m22) + 1.0f);
            float x = t * 0.5f;
            t = 0.5f / t;
            float y = (m10 + m01) * t;
            float z = (m02 + m20) * t;
            float w = (m12 - m21) * t;
            return new Quaternion(x, y, z, w);
        } else if (m11 > m22) {
            float t = MathF.sqrt(m11 - (m22 + m00) + 1.0f);
            float y = t * 0.5f;
            t = 0.5f / t;
            float z = (m21 + m12) * t;
            float x = (m10 + m01) * t;
            float w = (m20 - m02) * t;
            return new Quaternion(x, y, z, w);
        } else {
            float t = MathF.sqrt(m22 - (m00 + m11) + 1.0f);
            float z = t * 0.5f;
            t = 0.5f / t;
            float x = (m02 + m20) * t;
            float y = (m21 + m12) * t;
            float w = (m01 - m10) * t;
            return new Quaternion(x, y, z, w);
        }
    }

    @Override
    public String toString() {
        return "(" +
            m00 + ", " + m01 + ", " + m02 + ", " +
            m10 + ", " + m11 + ", " + m12 + ", " +
            m20 + ", " + m21 + ", " + m22 +
            ")";
    }
}
