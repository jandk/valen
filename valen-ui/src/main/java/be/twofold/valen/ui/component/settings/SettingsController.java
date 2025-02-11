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

import java.util.*;
import java.util.stream.*;

public final class SettingsController implements Controller {
    private @FXML CheckBox typeTexture;
    private @FXML CheckBox typeModel;
    private @FXML CheckBox typeRaw;
    private @FXML VBox assetTypes;
    private @FXML ComboBox<Map.Entry<String, String>> textureFormat;
    private @FXML CheckBox textureReconstructZ;
    private @FXML ComboBox<String> modelFormat;
    private @FXML TextField modelImageDirectory;

    private final SendChannel<SettingsApplied> channel;
    private final Settings settings;

    @Inject
    public SettingsController(EventBus eventBus) {
        this.settings = SettingsManager.get();
        this.channel = eventBus.senderFor(SettingsApplied.class);
    }

    public void initialize() {
        var textureFormats = Exporter.forType(Texture.class).stream()
            .map(exporter -> Map.entry(exporter.getID(), exporter.getName()))
            .sorted(Comparator.comparing(Map.Entry::getKey))
            .collect(Collectors.toList());

        textureFormat.getItems().setAll(textureFormats);
        textureFormat.setConverter(new FunctionalStringConverter<>(Map.Entry::getValue));
        // modelFormat.getItems().addAll("GLB", "GLTF, BIN and images");

        var assetTypes = settings.assetTypes().get()
            .orElseGet(() -> Arrays.stream(AssetType.values())
                .filter(type -> type != AssetType.BINARY)
                .collect(Collectors.toUnmodifiableSet()));
        typeTexture.setSelected(assetTypes.contains(AssetType.TEXTURE));
        typeModel.setSelected(assetTypes.contains(AssetType.MODEL));
        typeRaw.setSelected(assetTypes.contains(AssetType.BINARY));

        var textureExporter = settings.textureExporter().get()
            .orElse("texture.png");
        textureFormat.getItems().stream()
            .filter(e -> e.getKey().equals(textureExporter))
            .findFirst()
            .ifPresent(e -> textureFormat.getSelectionModel().select(e));

        var reconstructZ = settings.reconstructZ().get().orElse(false);
        textureReconstructZ.setSelected(reconstructZ);
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
        if (typeRaw.isSelected()) {
            assetTypes.add(AssetType.BINARY);
        }
        settings.assetTypes().set(assetTypes);
        settings.textureExporter().set(textureFormat.getValue().getKey());
        settings.reconstructZ().set(textureReconstructZ.isSelected());

        channel.send(new SettingsApplied(settings));
    }
}
