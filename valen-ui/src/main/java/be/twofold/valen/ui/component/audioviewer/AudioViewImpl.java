package be.twofold.valen.ui.component.audioviewer;

import be.twofold.valen.ui.common.*;
import javafx.scene.*;
import javafx.scene.layout.*;

public final class AudioViewImpl extends AbstractView<AudioView.Listener> implements AudioView {
    private final VBox root = new VBox();

    @Override
    public Parent getFXNode() {
        return root;
    }
}
