package be.twofold.valen.geometry;

public record Mat4(
    float m00, float m01, float m02, float m03,
    float m10, float m11, float m12, float m13,
    float m20, float m21, float m22, float m23,
    float m30, float m31, float m32, float m33
) {
    public static Mat4 fromArray(float[] array) {
        return new Mat4(
            array[0], array[1], array[2], array[3],
            array[4], array[5], array[6], array[7],
            array[8], array[9], array[10], array[11],
            array[12], array[13], array[14], array[15]
        );
    }

    @Override
    public String toString() {
        String format = """
            %f %f %f %f
            %f %f %f %f
            %f %f %f %f
            %f %f %f %f
            """;

        return String.format(format,
            m00, m01, m02, m03,
            m10, m11, m12, m13,
            m20, m21, m22, m23,
            m30, m31, m32, m33
        );
    }
}
