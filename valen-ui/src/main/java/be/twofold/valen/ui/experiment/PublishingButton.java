package be.twofold.valen.ui.experiment;

import javax.swing.*;
import java.awt.event.*;
import java.util.concurrent.*;
import java.util.function.*;

public class PublishingButton extends JButton {
    private final SubmissionPublisher<ButtonAction> publisher = new ButtonPublisher();

    public PublishingButton() {
    }

    public void subscribe(Consumer<ButtonAction> actionConsumer) {
        publisher.subscribe(new ConsumerSubscriber<>(actionConsumer));
    }

    private final class ButtonPublisher extends SubmissionPublisher<ButtonAction> {
        private final ActionListener listener;

        ButtonPublisher() {
            super(Executors.newThreadPerTaskExecutor(Executors.defaultThreadFactory()), Flow.defaultBufferSize());

            listener = e -> {
                System.out.println(Thread.currentThread() + ": Submitting");
                submit(new ButtonAction());
            };

            addActionListener(listener);
        }

        @Override
        public void close() {
            super.close();
            removeActionListener(listener);
        }
    }
}
