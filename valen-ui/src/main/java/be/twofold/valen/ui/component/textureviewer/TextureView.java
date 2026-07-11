package be.twofold.valen.ui.component.textureviewer;

import be.twofold.valen.ui.common.*;
import wtf.reversed.toolbox.collect.*;

public interface TextureView extends View<TextureView.Listener> {

    /**
     * Displays a decoded image. The pixels are BGRA with premultiplied alpha,
     * laid out top-to-bottom with a stride of {@code width * 4} bytes.
     */
    void setImage(int width, int height, Bytes.Mutable pixels, boolean resetZoom);

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
