package be.twofold.valen.game.idtech.megatexture;

/**
 * A rectangle of a mega texture, in texels of its finest level.
 */
public record Rect(
    int x,
    int y,
    int width,
    int height
) {
}
