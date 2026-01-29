package be.twofold.valen.ui.component.settings;

import backbonefx.event.*;
import be.twofold.valen.core.export.*;
import be.twofold.valen.core.game.*;
import be.twofold.valen.core.texture.*;
import be.twofold.valen.ui.common.*;
import be.twofold.valen.ui.common.settings.*;
import be.twofold.valen.ui.events.*;
import jakarta.inject.*;

import java.util.*;

public final class SettingsPresenter extends AbstractPresenter<SettingsView> implements SettingsView.Listener {

    private final EventBus eventBus;
    private final Settings settings;

    @Inject
    public SettingsPresenter(SettingsView view, EventBus eventBus, Settings settings) {
        super(view);
        this.eventBus = eventBus;
        this.settings = settings;

        view.setListener(this);
        initialize();
    }

    private void initialize() {
        getView().setDescriptors(
            new SettingDescriptor<>(
                SettingGroup.GENERAL,
                SettingType.MULTI_MULTIPLE,
                "Show Asset Types",
                "Select which types of assets to show in the file browser",
                settings::getAssetTypes,
                settings::setAssetTypes,
                List.of(AssetType.values()),
                AssetType::displayName
            ),
            new SettingDescriptor<>(
                SettingGroup.GENERAL,
                SettingType.PATH,
                "Export Path",
                "Choose a path where exported files will be saved",
                settings::getExportPath,
                settings::setExportPath
            ),
            new SettingDescriptor<>(
                SettingGroup.TEXTURES,
                SettingType.MULTI_SINGLE,
                "Texture Format",
                "Select which texture format to export as",
                settings::getTextureExporter,
                settings::setTextureExporter,
                Exporter.forType(Texture.class)
                    .map(e -> Map.entry(e.getID(), e.getName()))
                    .sorted(Map.Entry.comparingByKey())
                    .toList(),
                Map.Entry::getValue
            ),
            new SettingDescriptor<>(
                SettingGroup.TEXTURES,
                SettingType.BOOLEAN,
                "Reconstruct Z",
                "Reconstruct the blue channel from the red and green channels of the texture",
                settings::isReconstructZ,
                settings::setReconstructZ
            ),
            new SettingDescriptor<>(
                SettingGroup.MODELS,
                SettingType.MULTI_SINGLE,
                "Model Format",
                "Select which model format to export as",
                settings::getModelExporter,
                settings::setModelExporter,
                List.of(
                    Map.entry("gltf", "GLTF, BIN and images"),
                    Map.entry("glb", "GLB (single file)"),
                    Map.entry("cast", "Cast (by Porter)")
                ),
                Map.Entry::getValue
            )
        );
    }

    @Override
    public void onSave() {
        eventBus.publish(new SettingsApplied(settings));
    }

}
