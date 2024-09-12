package be.twofold.valen.ui.viewer.model;

import javafx.beans.property.*;
import javafx.scene.*;
import javafx.scene.layout.*;

public final class SubSceneResizer extends Pane {
    private SubScene subScene;

    public SubSceneResizer(ObjectProperty<SubScene> subScene) {
        updateSubScene(subScene.get());
        subScene.addListener((observable, oldValue, newValue) -> {
            updateSubScene(newValue);
        });
        setMinSize(50, 50);
        setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
    }

    private void updateSubScene(SubScene newValue) {
        this.subScene = newValue;
        if (this.subScene != null) {
            setPrefSize(this.subScene.getWidth(), this.subScene.getHeight());
            getChildren().setAll(this.subScene);
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
