package be.twofold.valen.stream;

public record Stream(
    long identity,
    long offset,
    int length
) {
}
