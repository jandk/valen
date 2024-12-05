package be.twofold.valen.core.texture.op;

public final class RGBA {
    private RGBA() {
    }

    public static int r(int rgba) {
        return rgba & 0xFF;
    }

    public static int g(int rgba) {
        return rgba >> 8 & 0xFF;
    }

    public static int b(int rgba) {
        return rgba >> 16 & 0xFF;
    }

    public static int a(int rgba) {
        return rgba >> 24 & 0xFF;
    }

    public static int rgb(int r, int g, int b) {
        return rgba(r, g, b, 0xFF);
    }

    public static int rgba(int r, int g, int b, int a) {
        return r | g << 8 | b << 16 | a << 24;
    }

    public static long rgba16(int r, int g, int b, int a) {
        return (long) r | (long) g << 16 | (long) b << 32 | (long) a << 48;
    }
}
