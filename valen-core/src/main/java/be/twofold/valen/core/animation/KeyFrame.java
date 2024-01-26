package be.twofold.valen.core.animation;

public record KeyFrame<T>(
    int frame,
    T value
) {
}
