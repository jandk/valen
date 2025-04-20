package be.twofold.valen.ui.component.progress;

import backbonefx.mvvm.*;
import javafx.beans.property.*;

public class ProgressViewModel implements ViewModel {
    private final LongProperty workDone = new SimpleLongProperty();
    private final LongProperty workTotal = new SimpleLongProperty();
    private final DoubleProperty progress = new SimpleDoubleProperty();
    private final StringProperty message = new SimpleStringProperty();
    private final BooleanProperty cancelled = new SimpleBooleanProperty();

    public LongProperty workDoneProperty() {
        return workDone;
    }

    public LongProperty workTotalProperty() {
        return workTotal;
    }

    public DoubleProperty progressProperty() {
        return progress;
    }

    public StringProperty messageProperty() {
        return message;
    }

    public BooleanProperty cancelledProperty() {
        return cancelled;
    }
}
