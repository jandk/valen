package be.twofold.valen.ui.component.settings;

import be.twofold.valen.ui.common.*;
import be.twofold.valen.ui.common.settings.*;

public interface SettingsView extends View<SettingsView.Listener> {
    void setDescriptors(SettingDescriptor<?, ?>... descriptors);

    interface Listener extends View.Listener {
        void onSave();
    }
}
