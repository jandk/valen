package be.twofold.valen.ui.component.textureviewer;

import be.twofold.valen.ui.common.*;
import javafx.scene.image.*;

public interface TextureView extends View<TextureView.Listener> {

    void setImage(Image image, boolean resetZoom);

    void adjustScale(double factor);

    void setStatus(String status);

    void setSliceCount(int count);

    void setMipCount(int count);

    interface Listener extends View.Listener {

        void onChannelSelected(Channel channel);

        void onSliceSelected(int slice);

        void onMipSelected(int mip);

    }
}
