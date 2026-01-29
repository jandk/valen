package be.twofold.valen.ui.component.textureviewer;

import be.twofold.valen.ui.common.*;
import javafx.scene.image.*;

public interface TextureView extends View<TextureView.Listener> {

    void setImage(Image image);

    void setStatus(String status);

    interface Listener extends View.Listener {
        void onChannelSelected(Channel channel);
    }
}
