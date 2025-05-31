package be.twofold.valen.ui.component;

import backbonefx.di.*;
import backbonefx.event.*;
import jakarta.inject.*;

public final class EventBusModule {
    @Provides
    @Singleton
    public EventBus provideEventBus(DefaultEventBus eventBus) {
        return eventBus;
    }
}
