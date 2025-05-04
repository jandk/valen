package be.twofold.valen.format.cast;

public enum CastNodeID {
    Root('r' | 'o' << 8 | 'o' << 16 | 't' << 24),
    Model('m' | 'o' << 8 | 'd' << 16 | 'l' << 24),
    Mesh('m' | 'e' << 8 | 's' << 16 | 'h' << 24),
    Hair('h' | 'a' << 8 | 'i' << 16 | 'r' << 24),
    BlendShape('b' | 'l' << 8 | 's' << 16 | 'h' << 24),
    Skeleton('s' | 'k' << 8 | 'e' << 16 | 'l' << 24),
    Bone('b' | 'o' << 8 | 'n' << 16 | 'e' << 24),
    IKHandle('i' | 'k' << 8 | 'h' << 16 | 'd' << 24),
    Constraint('c' | 'n' << 8 | 's' << 16 | 't' << 24),
    Animation('a' | 'n' << 8 | 'i' << 16 | 'm' << 24),
    Curve('c' | 'u' << 8 | 'r' << 16 | 'v' << 24),
    CurveModeOverride('C' | 'M' << 8 | 'O' << 16 | 'V' << 24),
    NotificationTrack('n' | 't' << 8 | 'i' << 16 | 'f' << 24),
    Material('m' | 'a' << 8 | 't' << 16 | 'l' << 24),
    File('f' | 'i' << 8 | 'l' << 16 | 'e' << 24),
    Color('c' | 'o' << 8 | 'l' << 16 | 'r' << 24),
    Instance('i' | 'n' << 8 | 's' << 16 | 't' << 24),
    Metadata('m' | 'e' << 8 | 't' << 16 | 'a' << 24),
    ;

    private final int value;

    CastNodeID(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }

    public static CastNodeID fromValue(int value) {
        return switch (value) {
            case 'r' | 'o' << 8 | 'o' << 16 | 't' << 24 -> Root;
            case 'm' | 'o' << 8 | 'd' << 16 | 'l' << 24 -> Model;
            case 'm' | 'e' << 8 | 's' << 16 | 'h' << 24 -> Mesh;
            case 'h' | 'a' << 8 | 'i' << 16 | 'r' << 24 -> Hair;
            case 'b' | 'l' << 8 | 's' << 16 | 'h' << 24 -> BlendShape;
            case 's' | 'k' << 8 | 'e' << 16 | 'l' << 24 -> Skeleton;
            case 'b' | 'o' << 8 | 'n' << 16 | 'e' << 24 -> Bone;
            case 'i' | 'k' << 8 | 'h' << 16 | 'd' << 24 -> IKHandle;
            case 'c' | 'n' << 8 | 's' << 16 | 't' << 24 -> Constraint;
            case 'a' | 'n' << 8 | 'i' << 16 | 'm' << 24 -> Animation;
            case 'c' | 'u' << 8 | 'r' << 16 | 'v' << 24 -> Curve;
            case 'C' | 'M' << 8 | 'O' << 16 | 'V' << 24 -> CurveModeOverride;
            case 'n' | 't' << 8 | 'i' << 16 | 'f' << 24 -> NotificationTrack;
            case 'm' | 'a' << 8 | 't' << 16 | 'l' << 24 -> Material;
            case 'f' | 'i' << 8 | 'l' << 16 | 'e' << 24 -> File;
            case 'c' | 'o' << 8 | 'l' << 16 | 'r' << 24 -> Color;
            case 'i' | 'n' << 8 | 's' << 16 | 't' << 24 -> Instance;
            case 'm' | 'e' << 8 | 't' << 16 | 'a' << 24 -> Metadata;
            default -> throw new IllegalArgumentException("Unknown CastID: " + value);
        };
    }
}
