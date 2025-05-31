package be.twofold.valen.format.cast;

public enum CastNodeID {
    ROOT(0x746F6F72),
    MODEL(0x6C646F6D),
    MESH(0x6873656D),
    HAIR(0x72696168),
    BLEND_SHAPE(0x68736C62),
    SKELETON(0x6C656B73),
    BONE(0x656E6F62),
    IK_HANDLE(0x64686B69),
    CONSTRAINT(0x74736E63),
    ANIMATION(0x6D696E61),
    CURVE(0x76727563),
    CURVE_MODE_OVERRIDE(0x564F4D43),
    NOTIFICATION_TRACK(0x6669746E),
    MATERIAL(0x6C74616D),
    FILE(0x656C6966),
    COLOR(0x726C6F63),
    INSTANCE(0x74736E69),
    METADATA(0x6174656D),
    ;

    private final int id;

    CastNodeID(int id) {
        this.id = id;
    }

    public int id() {
        return id;
    }

    public static CastNodeID fromValue(int value) {
        return switch (value) {
            case 0x746F6F72 -> ROOT;
            case 0x6C646F6D -> MODEL;
            case 0x6873656D -> MESH;
            case 0x72696168 -> HAIR;
            case 0x68736C62 -> BLEND_SHAPE;
            case 0x6C656B73 -> SKELETON;
            case 0x656E6F62 -> BONE;
            case 0x64686B69 -> IK_HANDLE;
            case 0x74736E63 -> CONSTRAINT;
            case 0x6D696E61 -> ANIMATION;
            case 0x76727563 -> CURVE;
            case 0x564F4D43 -> CURVE_MODE_OVERRIDE;
            case 0x6669746E -> NOTIFICATION_TRACK;
            case 0x6C74616D -> MATERIAL;
            case 0x656C6966 -> FILE;
            case 0x726C6F63 -> COLOR;
            case 0x74736E69 -> INSTANCE;
            case 0x6174656D -> METADATA;
            default -> throw new IllegalArgumentException("Unknown CastNodeID: " + value);
        };
    }
}
