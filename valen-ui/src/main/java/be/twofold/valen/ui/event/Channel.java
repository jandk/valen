package be.twofold.valen.ui.event;

import org.slf4j.*;

import java.util.concurrent.*;

final class Channel<T> implements SendChannel<T>, ReceiveChannel<T> {
    private static final Logger log = LoggerFactory.getLogger(Channel.class);

    private final BlockingQueue<T> queue = new LinkedBlockingQueue<>();

    @Override
    public void send(T event) {
        try {
            queue.put(event);
        } catch (InterruptedException e) {
            log.info("Thread '{}' was interrupted in send", Thread.currentThread().getName());
            // abort
        }
    }

    @Override
    public T receive() {
        try {
            return queue.take();
        } catch (InterruptedException e) {
            log.info("Thread '{}' was interrupted in receive", Thread.currentThread().getName());
            return null;
        }
    }
}
