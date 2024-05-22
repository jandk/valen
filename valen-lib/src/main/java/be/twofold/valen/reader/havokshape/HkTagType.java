package be.twofold.valen.reader.havokshape;

public enum HkTagType {
    TAG0(0x54414730),
    SDKV(0x53444b56),
    DATA(0x44415441),
    TYPE(0x54595045),
    TPTR(0x54505452),
    TSTR(0x54535452),
    TNA1(0x544e4131),
    FSTR(0x46535452),
    TBDY(0x54424459),
    THSH(0x54485348),
    TPAD(0x54504144),
    INDX(0x494e4458),
    ITEM(0x4954454d),
    PTCH(0x50544348);

    private final int code;

    HkTagType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static HkTagType from(int code) {
        for (HkTagType value : values()) {
            if (value.code == code) {
                return value;
            }
        }

        char c0 = (char) (code & 0xff);
        char c1 = (char) ((code >> 8) & 0xff);
        char c2 = (char) ((code >> 16) & 0xff);
        char c3 = (char) ((code >> 24) & 0xff);
        throw new IllegalArgumentException("Unknown code: " + c0 + c1 + c2 + c3 + " (0x" + Integer.toHexString(code) + ")");
    }
}
