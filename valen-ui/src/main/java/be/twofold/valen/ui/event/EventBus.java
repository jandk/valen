package be.twofold.valen.ui.event;

import jakarta.inject.*;

import java.lang.ref.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;

@Singleton
public final class EventBus {
    private final Map<Class<?>, Set<WeakReference<Channel<?>>>> channels;

    @Inject
    EventBus() {
        channels = new IdentityHashMap<>();
    }

    public <E> SendChannel<E> senderFor(Class<E> clazz) {
        return event -> {
            for (WeakReference<Channel<?>> channelRef : channels.getOrDefault(clazz, Set.of())) {
                @SuppressWarnings("unchecked") // covariant cast
                Channel<E> channel = (Channel<E>) channelRef.get();
                if (channel != null) {
                    channel.send(event);
                }
            }
        };
    }

    public <E> ReceiveChannel<E> receiverFor(Class<E> clazz) {
        Channel<E> channel = new Channel<>();
        channels
            .computeIfAbsent(clazz, _ -> new CopyOnWriteArraySet<>())
            .add(new WeakReference<>(channel));
        return channel;
    }
}
