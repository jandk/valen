package be.twofold.valen.ui.viewers;

import be.twofold.valen.core.game.*;
import javafx.scene.*;
import javafx.scene.control.*;

public class TextViewer extends TextArea implements Viewer {
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
