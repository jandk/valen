package be.twofold.valen.ui.common;

import javafx.util.*;
import wtf.reversed.toolbox.util.*;

import java.util.function.*;

public final class FunctionalStringConverter<T> extends StringConverter<T> {
    private final Function<? super T, String> converter;

    public FunctionalStringConverter(Function<? super T, String> converter) {
        this.converter = Check.nonNull(converter, "converter");
    }

    @Override
    public String toString(T object) {
        return object == null ? null : converter.apply(object);
    }

    @Override
    public T fromString(String string) {
        throw new UnsupportedOperationException();
    }
}
