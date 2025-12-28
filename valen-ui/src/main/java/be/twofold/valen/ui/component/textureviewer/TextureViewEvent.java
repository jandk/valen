package be.twofold.valen.ui.component.textureviewer;

import backbonefx.event.*;

public sealed interface TextureViewEvent extends Event {
    record ChannelSelected(Channel channel) implements TextureViewEvent {
    }
}
