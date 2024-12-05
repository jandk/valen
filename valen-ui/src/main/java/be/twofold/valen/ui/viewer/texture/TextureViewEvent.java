package be.twofold.valen.ui.viewer.texture;

public sealed interface TextureViewEvent {
    record ColorsToggled(boolean red, boolean green, boolean blue, boolean alpha) implements TextureViewEvent {
    }
}
