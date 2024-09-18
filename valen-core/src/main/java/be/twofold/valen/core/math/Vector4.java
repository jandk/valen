package be.twofold.valen.core.math;

public record Vector4(
    float x,
    float y,
    float z,
    float w
) {
    public Vector4 add(Vector4 other) {
        return new Vector4(x + other.x, y + other.y, z + other.z, w + other.w);
    }

    public Vector4 subtract(Vector4 other) {
        return add(other.negate());
    }

    public Vector4 multiply(float scalar) {
        return new Vector4(x * scalar, y * scalar, z * scalar, w * scalar);
    }

    public Vector4 divide(float scalar) {
        return multiply(1.0f / scalar);
    }

    public Vector4 negate() {
        return new Vector4(-x, -y, -z, -w);
    }

    public float dot(Vector4 other) {
        return x * other.x + y * other.y + z * other.z + w * other.w;
    }

    public float lengthSquared() {
        return dot(this);
    }

    public float length() {
        return MathF.sqrt(lengthSquared());
    }

    public Vector4 normalize() {
        return divide(length());
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || obj instanceof Vector4 other
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
