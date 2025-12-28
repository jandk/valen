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
import java.util.stream.*;

public final class SettingsPresenter extends AbstractFXPresenter<SettingsView> {
    private final EventBus eventBus;
    private final Settings settings;

    @Inject
    SettingsPresenter(SettingsView view, EventBus eventBus, Settings settings) {
        super(view);

        this.eventBus = eventBus;
        this.settings = settings;

        eventBus
            .subscribe(SettingsViewEvent.class, event -> {
                switch (event) {
                    case SettingsViewEvent.Applied _ -> {
                        applySettings();
                    }
                }
            });

        setupView();

        loadSettingsIntoView();
    }

    private void setupView() {
        getView().setAssetTypeSelection(Set.of(AssetType.values()));

        var textureExporters = Exporter.forType(Texture.class)
            .map(exporter -> Map.entry(exporter.getID(), exporter.getName()))
            .collect(Collectors.toUnmodifiableSet());

        getView().setTextureExporterSelection(textureExporters);
    }

    private void loadSettingsIntoView() {
        var assetTypes = settings.assetTypes().get()
            .orElseGet(() -> Arrays.stream(AssetType.values())
                .filter(type -> type != AssetType.RAW)
                .collect(Collectors.toUnmodifiableSet()));
        getView().setAssetTypes(assetTypes);

        var textureExporter = settings.textureExporter().get()
            .orElse("texture.png");
        getView().setTextureExporter(textureExporter);

        var reconstructZ = settings.reconstructZ().get().orElse(false);
        getView().setReconstructZ(reconstructZ);
    }

    private void applySettings() {
        settings.assetTypes().set(getView().getAssetTypes());
        settings.textureExporter().set(getView().getTextureExporter());
        settings.reconstructZ().set(getView().getReconstructZ());

        eventBus.publish(new SettingsApplied(settings));
    }
}
