package be.twofold.valen.ui.viewer.texture;

import be.twofold.valen.ui.*;
import javafx.scene.image.*;

public interface TextureView extends View<TextureViewListener> {
    void setImage(Image image);
}