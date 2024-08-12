package be.twofold.valen.reader.havokshape;

public record HavokItem(
    HavokType type,
    int flags,
    int offset,
    int count
) {
}
