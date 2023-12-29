package be.twofold.valen.ui.experiment;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;

public final class ConsumerSubscriber<T> implements Flow.Subscriber<T> {
    private final AtomicBoolean subscribed = new AtomicBoolean();
    private final Consumer<T> consumer;
    private Flow.Subscription subscription;

    public ConsumerSubscriber(Consumer<T> consumer) {
        this.consumer = Objects.requireNonNull(consumer);
    }

    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        Objects.requireNonNull(subscription);
        if (!subscribed.compareAndSet(false, true)) {
            subscription.cancel();
            return;
        }
        this.subscription = subscription;
        subscription.request(1);
    }

    @Override
    public void onNext(T item) {
        Objects.requireNonNull(item);
        consumer.accept(item);
        subscription.request(1);
    }

    @Override
    public void onError(Throwable throwable) {
        System.err.println("Error: " + throwable.getMessage());
    }

    @Override
    public void onComplete() {
        System.err.println("Done");
    }
}
