package be.twofold.valen.export.cast;

import be.twofold.valen.core.export.*;

public abstract class CastExporter<T> implements Exporter<T> {
    @Override
    public String getName() {
        return "Cast";
    }

    @Override
    public String getExtension() {
        return "cast";
    }
}
