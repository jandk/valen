package be.twofold.valen.core.geometry;

import wtf.reversed.toolbox.math.*;

public enum Axis {
    X,
    Y,
    Z,
    ;

    /**
     * Returns the rotation that maps this up-axis onto the given up-axis.
     * <p>
     * The rotation is the minimal +90&deg; turn about the axis perpendicular
     * to both (their cross product {@code this x axis}), so that the source
     * up-vector is carried exactly onto the destination up-vector.
     */
    public Quaternion rotateTo(Axis axis) {
        return switch (this) {
            case X -> switch (axis) {
                case X -> Quaternion.IDENTITY;
                case Y -> Quaternion.fromAxisAngle(Vector3.Z, +90.0f, Angle.DEGREES);
                case Z -> Quaternion.fromAxisAngle(Vector3.Y, -90.0f, Angle.DEGREES);
            };
            case Y -> switch (axis) {
                case X -> Quaternion.fromAxisAngle(Vector3.Z, -90.0f, Angle.DEGREES);
                case Y -> Quaternion.IDENTITY;
                case Z -> Quaternion.fromAxisAngle(Vector3.X, +90.0f, Angle.DEGREES);
            };
            case Z -> switch (axis) {
                case X -> Quaternion.fromAxisAngle(Vector3.Y, +90.0f, Angle.DEGREES);
                case Y -> Quaternion.fromAxisAngle(Vector3.X, -90.0f, Angle.DEGREES);
                case Z -> Quaternion.IDENTITY;
            };
        };
    }
}
