package be.twofold.valen.ui.component.main;

import be.twofold.valen.ui.common.*;

public interface MainViewListener extends ViewListener {

    void onArchiveSelected(String name);

    void onPreviewVisibilityChanged(boolean visible);

    void onSettingsVisibilityChanged(boolean visible);

    void onLoadGameClicked();

    void onExportClicked();

    void onSearchChanged(String query);

}
