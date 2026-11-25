package be.twofold.valen.ui.component.preview;

import be.twofold.valen.ui.common.*;
import be.twofold.valen.ui.component.*;
import javafx.animation.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.*;

public final class PreviewViewImpl extends AbstractView<PreviewView.Listener> implements PreviewView {
    private final StackPane root = new StackPane();
    private final TabPane tabPane = new TabPane();
    private final Tab previewTab = new Tab("Preview");
    private final Tab metaTab = new Tab("Metadata");
    private final Tab rawTab = new Tab("Raw");

    private final ProgressIndicator spinner = new ProgressIndicator();
    private final StackPane veil = new StackPane(spinner);
    private final PauseTransition spinnerDelay = new PauseTransition(Duration.millis(200));

    public PreviewViewImpl() {
        buildUI();
    }

    @Override
    public Parent getFXNode() {
        return root;
    }

    @Override
    public void setMetaView(View<?> view) {
        FX.run(() -> metaTab.setContent(view.getFXNode()));
    }

    @Override
    public void setRawView(View<?> view) {
        FX.run(() -> rawTab.setContent(view.getFXNode()));
    }

    @Override
    public void show(View<?> renderer, boolean hasMetadata, boolean hasRaw) {
        FX.run(() -> {
            previewTab.setContent(renderer != null ? renderer.getFXNode() : null);
            previewTab.setDisable(renderer == null);
            metaTab.setDisable(!hasMetadata);
            rawTab.setDisable(!hasRaw);
            selectFirstEnabled();
        });
    }

    @Override
    public void setLoading(boolean loading) {
        FX.run(() -> {
            if (loading) {
                spinnerDelay.playFromStart();
            } else {
                spinnerDelay.stop();
                veil.setVisible(false);
            }
        });
    }

    private void selectFirstEnabled() {
        var selected = tabPane.getSelectionModel().getSelectedItem();
        if (selected != null && !selected.isDisable()) {
            return;
        }
        for (var tab : tabPane.getTabs()) {
            if (!tab.isDisable()) {
                tabPane.getSelectionModel().select(tab);
                return;
            }
        }
    }

    private void buildUI() {
        for (var tab : new Tab[]{previewTab, metaTab, rawTab}) {
            tab.setClosable(false);
            tab.setDisable(true);
        }
        tabPane.getTabs().setAll(previewTab, metaTab, rawTab);

        spinner.setMaxSize(60, 60);
        veil.setVisible(false);
        veil.setPickOnBounds(true);
        spinnerDelay.setOnFinished(_ -> veil.setVisible(true));

        root.getChildren().setAll(tabPane, veil);
    }
}
