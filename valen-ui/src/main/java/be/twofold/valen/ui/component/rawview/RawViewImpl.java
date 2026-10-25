package be.twofold.valen.ui.component.rawview;

import be.twofold.valen.ui.common.*;
import be.twofold.valen.ui.component.rawview.binary.*;
import be.twofold.valen.ui.component.rawview.text.*;
import jakarta.inject.*;
import javafx.scene.*;
import javafx.scene.layout.*;

public final class RawViewImpl extends AbstractView<View.Listener> implements RawView {
    private final StackPane view = new StackPane();
    private final BinaryView binaryView = new BinaryView();
    private final TextView textView = new TextView();

    @Inject
    public RawViewImpl() {
        clear();
    }

    @Override
    public Parent getFXNode() {
        return view;
    }

    @Override
    public void setContent(RawPayload payload) {
        switch (payload) {
            case RawPayload.Binary(var binary) -> {
                textView.clear();
                binaryView.setBinary(binary);
                show(binaryView.getFXNode());
            }
            case RawPayload.Text(var text) -> {
                binaryView.clear();
                textView.setText(text);
                show(textView.getFXNode());
            }
        }
    }

    @Override
    public void clear() {
        binaryView.clear();
        textView.clear();
        show(textView.getFXNode());
    }

    private void show(Node node) {
        view.getChildren().setAll(node);
    }
}
