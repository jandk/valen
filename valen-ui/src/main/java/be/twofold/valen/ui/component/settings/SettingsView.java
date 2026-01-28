package be.twofold.valen.ui.component.settings;

import be.twofold.valen.ui.common.*;
import be.twofold.valen.ui.common.settings.*;

public interface SettingsView extends View<SettingsViewListener> {
    void setDescriptors(SettingDescriptor<?, ?>... descriptors);
}
