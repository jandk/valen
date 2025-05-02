package be.twofold.valen.core.math;

public enum Angle {
    DEGREES {
        @Override
        public float toRadians(float angle) {
            return MathF.toRadians(angle);
        }
    },
    RADIANS {
        @Override
        public float toDegrees(float angle) {
            return MathF.toDegrees(angle);
        }
    },
    ;

    public float toDegrees(float angle) {
        return angle;
    }

    public float toRadians(float angle) {
        return angle;
    }
}
