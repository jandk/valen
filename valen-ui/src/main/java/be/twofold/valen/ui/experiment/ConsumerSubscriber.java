package be.twofold.valen.ui.experiment;

import java.util.concurrent.*;
import java.util.function.*;

final class ConsumerSubscriber<T> implements Flow.Subscriber<T> {
    final Consumer<? super T> consumer;
    Flow.Subscription subscription;

    ConsumerSubscriber(Consumer<? super T> consumer) {
        this.consumer = consumer;
    }

    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        this.subscription = subscription;
        subscription.request(Long.MAX_VALUE);
    }

    @Override
    public void onError(Throwable ex) {
        ex.printStackTrace();
    }

    @Override
    public void onComplete() {
        System.out.println("Completed");
    }

    @Override
    public void onNext(T item) {
        try {
            consumer.accept(item);
        } catch (Throwable ex) {
            subscription.cancel();
        }
    }
}
