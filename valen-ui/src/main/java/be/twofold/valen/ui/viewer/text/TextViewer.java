package be.twofold.valen.ui.viewer.text;

import be.twofold.valen.core.game.*;
import be.twofold.valen.ui.viewer.*;
import javafx.scene.*;
import javafx.scene.control.*;

public final class TextViewer extends TextArea implements Viewer {
    @Override
    public boolean canPreview(AssetType type) {
        return type == AssetType.Text;
    }

    @Override
    public void setData(Object data) {
        if (data instanceof String) {
            setText((String) data);
        }
    }

    @Override
    public Node getNode() {
        return this;
    }

    @Override
    public String getName() {
        return "Text";
    }
}
