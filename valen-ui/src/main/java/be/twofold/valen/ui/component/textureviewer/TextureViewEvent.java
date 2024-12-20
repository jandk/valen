package be.twofold.valen.ui.component.textureviewer;

public sealed interface TextureViewEvent {
    record ColorsToggled(boolean red, boolean green, boolean blue, boolean alpha) implements TextureViewEvent {
    }
}
