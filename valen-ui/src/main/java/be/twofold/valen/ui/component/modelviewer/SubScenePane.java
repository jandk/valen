package be.twofold.valen.ui.component.modelviewer;

import javafx.scene.*;
import javafx.scene.layout.*;

public final class SubScenePane extends Pane {
    private SubScene subScene;

    public SubScenePane(SubScene subScene) {
        updateSubScene(subScene);
        setMinSize(50, 50);
        setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
    }

    private void updateSubScene(SubScene newValue) {
        this.subScene = newValue;
        if (subScene != null) {
            setPrefSize(subScene.getWidth(), subScene.getHeight());
            getChildren().setAll(subScene);
        }
    }

    @Override
    protected void layoutChildren() {
        if (subScene != null) {
            subScene.setWidth(getWidth());
            subScene.setHeight(getHeight());
        }
    }
}
