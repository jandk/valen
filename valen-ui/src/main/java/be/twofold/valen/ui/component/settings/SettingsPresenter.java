package be.twofold.valen.ui.component.settings;

import be.twofold.valen.core.export.*;
import be.twofold.valen.core.game.*;
import be.twofold.valen.ui.common.*;
import be.twofold.valen.ui.common.settings.*;

import java.util.*;

public final class SettingsPresenter extends AbstractPresenter<SettingsView> implements SettingsView.Listener {

    private final Settings settings;
    private Runnable onApplied;

    public SettingsPresenter(SettingsView view, Settings settings) {
        super(view);
        this.settings = settings;

        view.setListener(this);
        initialize();
    }

    private void initialize() {
        var descriptors = new ArrayList<SettingDescriptor<?, ?>>(List.of(
            new SettingDescriptor<>(
                SettingGroup.GENERAL,
                SettingType.MULTI_MULTIPLE,
                "Show Asset Types",
                "Select which types of assets to show in the file browser",
                settings::getAssetTypes,
                settings::setAssetTypes
            )
                .withOptions(List.of(AssetType.values()), AssetType::displayName)
                .withDisabled(settings::isTreatAsRaw),
            new SettingDescriptor<>(
                SettingGroup.GENERAL,
                SettingType.BOOLEAN,
                "Treat as Raw",
                "Preview and export every asset as raw bytes, without decoding it",
                settings::isTreatAsRaw,
                settings::setTreatAsRaw
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
                SettingType.BOOLEAN,
                "Reconstruct Z",
                "Reconstruct the blue channel from the red and green channels of the texture",
                settings::isReconstructZ,
                settings::setReconstructZ
            ),
            new SettingDescriptor<>(
                SettingGroup.TEXTURES,
                SettingType.MULTI_SINGLE,
                "PNG Compression",
                "Faster exports make bigger files, Balanced and Best are much slower",
                settings::getPngCompression,
                settings::setPngCompression
            ).withOptions(List.of(
                Map.entry("FASTEST", "Fastest"),
                Map.entry("FAST", "Fast"),
                Map.entry("BALANCED", "Balanced"),
                Map.entry("BEST", "Best")
            ), Map.Entry::getValue)
        ));

        for (var type : AssetType.values()) {
            var options = Exporters.forType(type.type())
                .map(e -> Map.entry(e.getID(), e.getName()))
                .sorted(Map.Entry.comparingByKey())
                .toList();
            if (options.size() < 2) {
                continue;
            }
            descriptors.add(new SettingDescriptor<>(
                SettingGroup.EXPORT,
                SettingType.MULTI_SINGLE,
                type.displayName() + " Format",
                null,
                () -> settings.getExporter(type),
                id -> settings.setExporter(type, id))
                .withOptions(options, Map.Entry::getValue)
            );
        }

        getView().setDescriptors(descriptors);
    }

    public void setOnApplied(Runnable onApplied) {
        this.onApplied = onApplied;
    }

    @Override
    public void onSave() {
        onApplied.run();
    }
}
