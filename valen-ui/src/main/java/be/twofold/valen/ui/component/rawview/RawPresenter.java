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
    public void setData(Object data) {
        if (data == null) {
            getView().clear();
            return;
        }
        if (!(data instanceof Bytes bytes)) {
            throw new UnsupportedOperationException("Unsupported data type: " + data.getClass());
        }

        binaryToText
            .binaryToText(bytes)
            .ifPresentOrElse(
                text -> getView().setText(text),
                () -> getView().setBinary(bytes)
            );
    }
}
