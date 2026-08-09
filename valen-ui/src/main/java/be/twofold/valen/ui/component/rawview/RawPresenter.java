package be.twofold.valen.ui.component.rawview;

import be.twofold.valen.core.game.*;
import be.twofold.valen.ui.common.*;
import be.twofold.valen.ui.component.*;
import jakarta.inject.*;
import wtf.reversed.toolbox.collect.*;

public final class RawPresenter extends AbstractPresenter<RawView> implements Viewer {
    private final BinaryToText binaryToText = new BinaryToText();

    @Inject
    public RawPresenter(RawView view) {
        super(view);
    }

    @Override
    public String getName() {
        return "Raw Data";
    }

    @Override
    public boolean canPreview(AssetType type) {
        return type != AssetType.MODEL && type != AssetType.TEXTURE;
    }

    @Override
    public Object decode(Object data) {
        if (!(data instanceof Bytes bytes)) {
            return null;
        }

        return binaryToText.binaryToText(bytes)
            .<RawPayload>map(RawPayload.Text::new)
            .orElseGet(() -> new RawPayload.Binary(bytes));
    }

    @Override
    public void display(Object data) {
        if (!(data instanceof RawPayload payload)) {
            getView().clear();
            return;
        }

        getView().setContent(payload);
    }
}
