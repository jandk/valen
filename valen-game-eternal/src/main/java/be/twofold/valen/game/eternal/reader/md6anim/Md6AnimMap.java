package be.twofold.valen.game.eternal.reader.md6anim;

public record Md6AnimMap(
    short tableCRC,
    int[] constR,
    int[] constS,
    int[] constT,
    int[] constU,
    int[] animR,
    int[] animS,
    int[] animT,
    int[] animU
) {
}
