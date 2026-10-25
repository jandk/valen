package be.twofold.valen.ui.common.settings;

import java.util.*;
import java.util.function.*;

/**
 * Describes a single setting for the settings view to render. Build one with the
 * short constructor and layer on the optional parts with {@link #withOptions} and
 * {@link #withDisabled}.
 */
public record SettingDescriptor<T, O>(
    SettingGroup group,
    SettingType type,
    String label,
    String helpText,
    Supplier<T> getter,
    Consumer<T> setter,
    List<O> options,
    Function<? super O, ? extends String> labeler,
    BooleanSupplier disabled
) {
    public SettingDescriptor(
        SettingGroup group,
        SettingType type,
        String label,
        String helpText,
        Supplier<T> getter,
        Consumer<T> setter
    ) {
        this(group, type, label, helpText, getter, setter, List.of(), Object::toString, () -> false);
    }

    /**
     * Adds options to pick from, together with the function to label them
     */
    public <P> SettingDescriptor<T, P> withOptions(List<P> options, Function<? super P, ? extends String> labeler) {
        return new SettingDescriptor<>(group, type, label, helpText, getter, setter, options, labeler, disabled);
    }

    /**
     * Adds a way to disable the setting when the disabled predicate returns true
     */
    public SettingDescriptor<T, O> withDisabled(BooleanSupplier disabled) {
        return new SettingDescriptor<>(group, type, label, helpText, getter, setter, options, labeler, disabled);
    }
}
