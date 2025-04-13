package be.twofold.valen.ui.component.settings;

import be.twofold.valen.core.export.*;
import be.twofold.valen.core.game.*;
import be.twofold.valen.core.texture.*;
import be.twofold.valen.ui.common.*;
import be.twofold.valen.ui.common.event.*;
import be.twofold.valen.ui.common.settings.*;
import be.twofold.valen.ui.component.*;
import be.twofold.valen.ui.events.*;
import jakarta.inject.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;

import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

public final class SettingsController implements Controller {
    private static final List<Map.Entry<String, String>> MODEL_FORMATS = List.of(
        Map.entry("gltf", "GLTF, BIN and images"),
        Map.entry("glb", "GLB (single file)")
    );

    private @FXML CheckBox typeTexture;
    private @FXML CheckBox typeModel;
    private @FXML CheckBox typeMaterial;
    private @FXML CheckBox typeAnimation;
    private @FXML CheckBox typeRaw;
    private @FXML VBox assetTypes;
    private @FXML ComboBox<Map.Entry<String, String>> textureFormat;
    private @FXML CheckBox textureReconstructZ;
    private @FXML ComboBox<Map.Entry<String, String>> modelFormat;
    private @FXML TextField modelImageDirectory;
    private @FXML TextField exportPath;
    private @FXML Button chooseExportPath;

    private final SendChannel<SettingsApplied> channel;
    private final Settings settings;

    @Inject
    public SettingsController(EventBus eventBus) {
        this.settings = SettingsManager.get();
        this.channel = eventBus.senderFor(SettingsApplied.class);
    }

    public void initialize() {
        var textureFormats = Exporter.forType(Texture.class)
            .map(exporter -> Map.entry(exporter.getID(), exporter.getName()))
            .sorted(Map.Entry.comparingByKey())
            .collect(Collectors.toList());

        textureFormat.getItems().setAll(textureFormats);
        textureFormat.setConverter(new FunctionalStringConverter<>(Map.Entry::getValue));

        modelFormat.getItems().setAll(MODEL_FORMATS);
        modelFormat.setConverter(new FunctionalStringConverter<>(Map.Entry::getValue));

        var assetTypes = settings.assetTypes().whenEmpty(() ->
            Arrays.stream(AssetType.values())
                .filter(type -> type != AssetType.RAW)
                .collect(Collectors.toUnmodifiableSet())
        );

        typeTexture.setSelected(assetTypes.contains(AssetType.TEXTURE));
        typeModel.setSelected(assetTypes.contains(AssetType.MODEL));
        typeMaterial.setSelected(assetTypes.contains(AssetType.MATERIAL));
        typeAnimation.setSelected(assetTypes.contains(AssetType.ANIMATION));
        typeRaw.setSelected(assetTypes.contains(AssetType.RAW));

        var textureExporter = settings.textureExporter().whenEmpty(() -> "texture.png");
        settings.textureExporter().set(textureExporter);

        textureFormat.getItems().stream()
            .filter(e -> e.getKey().equals(textureExporter))
            .findFirst()
            .ifPresent(e -> textureFormat.getSelectionModel().select(e));

        var modelExporter = settings.modelExporter().whenEmpty(() -> "gltf");
        settings.modelExporter().set(modelExporter);

        modelFormat.getItems().stream()
            .filter(e -> e.getKey().equals(modelExporter))
            .findFirst()
            .ifPresent(e -> modelFormat.getSelectionModel().select(e));

        var reconstructZ = settings.reconstructZ().whenEmpty(() -> false);
        settings.reconstructZ().set(reconstructZ);
        textureReconstructZ.setSelected(reconstructZ);

        var exportPath = settings.exportPath().whenEmpty(() -> Path.of("exported"));
        settings.exportPath().set(exportPath);
        this.exportPath.setText(exportPath.toString());

        chooseExportPath.setOnAction(event -> {
            var directoryChooser = new DirectoryChooser();
            var path = settings.exportPath().get().orElseThrow();
            if (path.isAbsolute()) {
                directoryChooser.setInitialDirectory(path.toAbsolutePath().toFile());
            }
            var newPath = directoryChooser.showDialog(chooseExportPath.getScene().getWindow());
            this.exportPath.setText(newPath.toString());
        });
    }

    @FXML
    public void handleSave() {
        var assetTypes = EnumSet.noneOf(AssetType.class);
        if (typeTexture.isSelected()) {
            assetTypes.add(AssetType.TEXTURE);
        }
        if (typeModel.isSelected()) {
            assetTypes.add(AssetType.MODEL);
        }
        if (typeMaterial.isSelected()) {
            assetTypes.add(AssetType.MATERIAL);
        }
        if (typeAnimation.isSelected()) {
            assetTypes.add(AssetType.ANIMATION);
        }
        if (typeRaw.isSelected()) {
            assetTypes.add(AssetType.RAW);
        }
        settings.assetTypes().set(assetTypes);
        settings.textureExporter().set(textureFormat.getValue().getKey());
        settings.reconstructZ().set(textureReconstructZ.isSelected());
        settings.exportPath().set(Path.of(exportPath.getText()));
        settings.modelExporter().set(modelFormat.getValue().getKey());

        channel.send(new SettingsApplied(settings));
    }
}
