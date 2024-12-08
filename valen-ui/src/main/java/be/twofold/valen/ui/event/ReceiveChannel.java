package be.twofold.valen.ui.event;

import be.twofold.valen.core.util.*;

import java.util.function.*;

@FunctionalInterface
public interface ReceiveChannel<E> {
    E receive();

    default void consume(Consumer<E> consumer) {
        Check.notNull(consumer);

        // TODO: Properly dispose of this
        Thread.startVirtualThread(() -> {
            while (true) {
                try {
                    consumer.accept(receive());
                } catch (Exception e) {
                    System.out.println("Exception: " + e);
                }
            }
        });
    }
}
