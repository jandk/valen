package be.twofold.valen.ui.component.settings;

import backbonefx.event.*;
import be.twofold.valen.core.game.*;
import be.twofold.valen.ui.common.*;
import jakarta.inject.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.*;
import java.util.stream.*;

@Singleton
public final class SettingsFXView implements SettingsView, FXView {

    private final VBox view = new VBox();
    private final VBox assetTypesVbox = new VBox(10);
    private final EventBus eventBus;

    private final Map<AssetType, CheckBox> assetTypes = new LinkedHashMap<>();
    private final ComboBox<Map.Entry<String, String>> textureFormat = createFormatComboBox();
    private final CheckBox reconstructZ = new CheckBox("(not for DDS)");
    private final TextField textureDirectory = new TextField("_textures");

    @Inject
    SettingsFXView(EventBus eventBus) {
        this.eventBus = eventBus;

        buildUI();
    }

    @Override
    public Parent getFXNode() {
        return view;
    }

    @Override
    public void setAssetTypeSelection(Set<AssetType> assetTypes) {
        this.assetTypes.clear();
        assetTypesVbox.getChildren().clear();

        assetTypes.stream()
            .sorted(Comparator.naturalOrder())
            .forEach(type -> {
                var checkBox = new CheckBox("Show " + type.getName() + " Files");
                this.assetTypes.put(type, checkBox);
                this.assetTypesVbox.getChildren().add(checkBox);
            });
    }

    @Override
    public Set<AssetType> getAssetTypes() {
        return assetTypes.entrySet().stream()
            .filter(e -> e.getValue().isSelected())
            .map(Map.Entry::getKey)
            .collect(Collectors.toCollection(() -> EnumSet.noneOf(AssetType.class)));
    }

    @Override
    public void setAssetTypes(Set<AssetType> assetTypes) {
        for (var entry : this.assetTypes.entrySet()) {
            entry.getValue().setSelected(assetTypes.contains(entry.getKey()));
        }
    }

    @Override
    public void setTextureExporterSelection(Set<Map.Entry<String, String>> exporters) {
        textureFormat.getItems().setAll(exporters);
    }

    @Override
    public String getTextureExporter() {
        return textureFormat.getValue().getKey();
    }

    @Override
    public void setTextureExporter(String exporter) {
        textureFormat.getItems().stream()
            .filter(e -> e.getKey().equals(exporter))
            .findFirst()
            .ifPresent(e -> textureFormat.getSelectionModel().select(e));
    }

    @Override
    public boolean getReconstructZ() {
        return reconstructZ.isSelected();
    }

    @Override
    public void setReconstructZ(boolean reconstructZ) {
        this.reconstructZ.setSelected(reconstructZ);
    }

    // region UI

    private void buildUI() {
        view.getChildren().add(buildGeneralSettings());
        view.getChildren().add(buildTextureSettings());
        // view.getChildren().add(buildModelSettings());
        view.getChildren().add(buildButtonBox());
    }

    private TitledPane buildGeneralSettings() {
        var generalLabel = new Label("Select which types of assets to show in the file browser");

        return createTitledPane("General Settings", List.of(generalLabel, assetTypesVbox));
    }

    private TitledPane buildTextureSettings() {
        var texturesLabel = new Label("Select which texture format to export as");

        var texturesPane = createGridPane();
        texturesPane.addRow(0, new Label("Format:"), textureFormat);
        texturesPane.addRow(1, new Label("Reconstruct Z:"), reconstructZ);

        return createTitledPane("Texture Export Settings", List.of(texturesLabel, texturesPane));
    }

    private TitledPane buildModelSettings() {
        var modelsLabel = new Label("Select which model format to export as");
        var modelsFormat = new ComboBox<>();
        modelsFormat.getItems().addAll("GLB", "GLTF, BIN and images");

        var modelsPane = createGridPane();
        modelsPane.addRow(0, new Label("Format:"), modelsFormat);
        modelsPane.addRow(1, new Label("Texture directory:"), textureDirectory);

        return createTitledPane("Model Export Settings", List.of(modelsLabel, modelsPane));
    }

    private Node buildButtonBox() {
        var applyButton = new Button("Apply");
        applyButton.setOnAction(event -> eventBus.publish(new SettingsViewEvent.Applied()));
        VBox.setMargin(applyButton, new Insets(10));
        return applyButton;
    }

    private GridPane createGridPane() {
        var constraint1 = new ColumnConstraints();
        constraint1.setPercentWidth(50);
        var constraint2 = new ColumnConstraints();
        constraint2.setPercentWidth(50);

        var pane = new GridPane(10, 10);
        pane.getColumnConstraints().addAll(constraint1, constraint2);
        return pane;
    }

    private ComboBox<Map.Entry<String, String>> createFormatComboBox() {
        var comboBox = new ComboBox<Map.Entry<String, String>>();
        comboBox.setConverter(new FunctionalStringConverter<>(Map.Entry::getValue));
        return comboBox;
    }

    private TitledPane createTitledPane(String title, List<? extends Node> nodes) {
        var vbox = new VBox(10);
        vbox.getChildren().addAll(nodes);

        var titledPane = new TitledPane();
        VBox.setMargin(titledPane, new Insets(10));

        titledPane.setText(title);
        titledPane.setCollapsible(false);
        titledPane.setContent(vbox);
        return titledPane;
    }

    // endregion

}
