package be.twofold.valen.ui.common.event;

import be.twofold.valen.core.util.*;
import org.slf4j.*;

import java.util.function.*;

@FunctionalInterface
public interface ReceiveChannel<E> {
    Logger log = LoggerFactory.getLogger(ReceiveChannel.class);

    E receive();

    default void consume(Consumer<E> consumer) {
        Check.notNull(consumer, "consumer");

        // TODO: Properly dispose of this
        Thread.startVirtualThread(() -> {
            while (true) {
                try {
                    consumer.accept(receive());
                } catch (Exception e) {
                    log.error("Error while executing listener", e);
                }
            }
        });
    }
}
