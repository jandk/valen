package be.twofold.valen.ui.experiment;

import javax.swing.*;
import java.util.concurrent.*;
import java.util.function.*;

public class PublishingButton extends JButton {
    private final SubmissionPublisher<ButtonAction> publisher
        = new SubmissionPublisher<>(Executors.newVirtualThreadPerTaskExecutor(), Flow.defaultBufferSize());

    public PublishingButton() {
        addActionListener(e -> {
            System.out.println(Thread.currentThread() + ": Submitting");
            publisher.submit(new ButtonAction());
        });
    }

    public void subscribe(Consumer<ButtonAction> actionConsumer) {
        publisher.subscribe(new ConsumerSubscriber<>(actionConsumer));
    }
}
