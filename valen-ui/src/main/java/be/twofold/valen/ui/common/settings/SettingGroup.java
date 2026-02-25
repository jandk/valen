package be.twofold.valen.ui.common.settings;

public enum SettingGroup {
    GENERAL("General"),
    TEXTURES("Textures"),
    MODELS("Models"),
    EXPORT("Export"),
    ;

    private final String displayName;

    SettingGroup(String displayName) {
        this.displayName = displayName;
    }

    public String displayName() {
        return displayName;
    }
}
