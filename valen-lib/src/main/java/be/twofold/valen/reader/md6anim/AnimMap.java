package be.twofold.valen.reader.md6anim;

public record AnimMap(
    short tableCRC,
    byte[] constR,
    byte[] constS,
    byte[] constT,
    byte[] constU,
    byte[] animR,
    byte[] animS,
    byte[] animT,
    byte[] animU
) {
}
