package be.twofold.valen.ui.common.settings;

import java.util.*;
import java.util.function.*;

public record SettingDescriptor<T, O>(
    SettingGroup group,
    SettingType type,
    String label,
    String helpText,
    Supplier<T> getter,
    Consumer<T> setter,
    List<O> options,
    Function<? super O, ? extends String> labeler
) {
    public SettingDescriptor(
        SettingGroup group,
        SettingType type,
        String label,
        String helpText,
        Supplier<T> getter,
        Consumer<T> setter
    ) {
        this(group, type, label, helpText, getter, setter, List.of(), Object::toString);
    }
}
