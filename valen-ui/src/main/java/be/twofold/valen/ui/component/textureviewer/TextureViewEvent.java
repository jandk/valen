package be.twofold.valen.ui.component.textureviewer;

public sealed interface TextureViewEvent {
    record ChannelSelected(Channel channel) implements TextureViewEvent {
    }
}
