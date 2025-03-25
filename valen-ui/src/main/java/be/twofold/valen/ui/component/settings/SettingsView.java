package be.twofold.valen.ui.component.settings;

import be.twofold.valen.core.game.*;

import java.util.*;

public interface SettingsView {

    void setAssetTypeSelection(Set<AssetType> assetTypes);

    Set<AssetType> getAssetTypes();

    void setAssetTypes(Set<AssetType> assetTypes);

    void setTextureExporterSelection(Set<Map.Entry<String, String>> exporters);

    String getTextureExporter();

    void setTextureExporter(String exporter);

    boolean getReconstructZ();

    void setReconstructZ(boolean reconstructZ);

}
