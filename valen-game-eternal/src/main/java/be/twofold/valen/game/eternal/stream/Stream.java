package be.twofold.valen.game.eternal.stream;

public record Stream(
    long identity,
    long offset,
    int length
) {
}
