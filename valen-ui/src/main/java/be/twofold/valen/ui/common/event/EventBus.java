package be.twofold.valen.ui.common.event;

import jakarta.inject.*;

import java.lang.ref.*;
import java.util.*;
import java.util.concurrent.*;

@Singleton
public final class EventBus {
    private static final Map<Class<?>, Set<WeakReference<Channel<?>>>> channels = new IdentityHashMap<>();

    @Inject
    EventBus() {
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
