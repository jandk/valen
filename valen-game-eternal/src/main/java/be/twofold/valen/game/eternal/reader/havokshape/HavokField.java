package be.twofold.valen.game.eternal.reader.havokshape;

public record HavokField(
    String name,
    int flags,
    int offset,
    HavokType type
) {
    @Override
    public String toString() {
        return String.format("%d: %s %s", offset, type, name);
    }
}
