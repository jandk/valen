package be.twofold.valen.ui.component.textureviewer;

import be.twofold.valen.ui.common.*;
import be.twofold.valen.ui.component.*;

public interface TextureView extends View<TextureView.Listener> {

    void setImage(DecodedImage image, boolean resetZoom);

    void clearImage();

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
