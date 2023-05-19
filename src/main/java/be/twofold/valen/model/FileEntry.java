package be.twofold.valen.model;

public record FileEntry(
    Name name,
    String type,
    long identity,
    int offset,
    int size,
    int sizeUncompressed,
    int version
) {
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof FileEntry other)) return false;
        return name.equals(other.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
