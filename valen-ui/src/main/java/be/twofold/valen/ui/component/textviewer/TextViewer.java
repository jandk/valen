package be.twofold.valen.ui.component.textviewer;

import be.twofold.valen.core.game.*;
import be.twofold.valen.ui.component.*;
import jakarta.inject.*;
import javafx.scene.*;
import javafx.scene.control.*;

public final class TextViewer extends TextArea implements Viewer {
    @Inject
    public TextViewer() {
    }

    @Override
    public boolean canPreview(AssetType<?> type) {
        return type == AssetType.TEXT;
    }

    @Override
    public void setData(Object data) {
        if (data instanceof String) {
            setText((String) data);
        }
    }

    @Override
    public Parent getFXNode() {
        return this;
    }

    @Override
    public String getName() {
        return "Text";
    }
}
