package be.twofold.valen.format.cast;

public final class CastFactory {
    private CastFactory() {
    }

    public static CastProperty string(String name, String value) {
        return CastProperty.string(name, value);
    }
}
