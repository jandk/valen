package be.twofold.valen.game.eternal.reader.havokshape;

public record HavokItem(
    HavokType type,
    int flags,
    int offset,
    int count
) {
}
