package be.twofold.valen.ui.event;

import java.util.concurrent.*;

final class Channel<T> implements SendChannel<T>, ReceiveChannel<T> {
    private final BlockingQueue<T> queue = new LinkedBlockingQueue<>();

    @Override
    public void send(T event) {
        try {
            queue.put(event);
        } catch (InterruptedException e) {
            System.out.println("Thread was interrupted in send");
            // abort
        }
    }

    @Override
    public T receive() {
        try {
            return queue.take();
        } catch (InterruptedException e) {
            System.out.println("Thread was interrupted in receive");
            return null;
        }
    }
}
