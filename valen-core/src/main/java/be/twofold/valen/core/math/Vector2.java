package be.twofold.valen.core.math;

public record Vector2(
    float x,
    float y
) {
    public static Vector2 Zero = new Vector2(0.0f, 0.0f);
    public static Vector2 One = new Vector2(1.0f, 1.0f);
    public static Vector2 X = new Vector2(1.0f, 0.0f);
    public static Vector2 Y = new Vector2(0.0f, 1.0f);

    public Vector2 add(Vector2 other) {
        return new Vector2(x + other.x, y + other.y);
    }

    public Vector2 subtract(Vector2 other) {
        return add(other.negate());
    }

    public Vector2 multiply(float scalar) {
        return new Vector2(x * scalar, y * scalar);
    }

    public Vector2 multiply(Vector2 vec) {
        return new Vector2(x * vec.x(), y * vec.y());
    }

    public Vector2 divide(float scalar) {
        return multiply(1.0f / scalar);
    }

    public Vector2 negate() {
        return new Vector2(-x, -y);
    }

    public float dot(Vector2 other) {
        return x * other.x + y * other.y;
    }

    public float lengthSquared() {
        return dot(this);
    }

    public float length() {
        return MathF.sqrt(lengthSquared());
    }

    public Vector2 normalize() {
        return divide(length());
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || obj instanceof Vector2 other
            && MathF.equals(x, other.x)
            && MathF.equals(y, other.y);
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + MathF.hashCode(x);
        result = 31 * result + MathF.hashCode(y);
        return result;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
