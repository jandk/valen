package be.twofold.valen.core.math;

public enum Axis {
    X,
    Y,
    Z,
    ;

    public Quaternion rotateTo(Axis axis) {
        return switch (this) {
            case X -> throw new UnsupportedOperationException("X axis not supported yet");
            case Y -> switch (axis) {
                case X -> throw new UnsupportedOperationException("X axis not supported yet");
                case Y -> Quaternion.Identity;
                case Z -> Quaternion.fromAxisAngle(Vector3.X, +90.0f, Angle.DEGREES);
            };
            case Z -> switch (axis) {
                case X -> throw new UnsupportedOperationException("X axis not supported yet");
                case Y -> Quaternion.fromAxisAngle(Vector3.X, -90.0f, Angle.DEGREES);
                case Z -> Quaternion.Identity;
            };
        };
    }
}
