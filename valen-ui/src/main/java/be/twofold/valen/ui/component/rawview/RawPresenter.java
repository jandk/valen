package be.twofold.valen.ui.component.rawview;

import be.twofold.valen.core.game.*;
import be.twofold.valen.ui.common.*;
import be.twofold.valen.ui.component.*;
import jakarta.inject.*;

import java.nio.*;

public final class RawPresenter extends AbstractFXPresenter<RawView> implements Viewer {
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
        return type == AssetType.RAW;
    }

    @Override
    public void setData(Object data) {
        if (data == null) {
            getView().setText(null);
            return;
        }
        if (!(data instanceof ByteBuffer buffer)) {
            throw new UnsupportedOperationException("Unsupported data type: " + data.getClass());
        }

        binaryToText
            .binaryToText(buffer)
            .ifPresentOrElse(
                text -> getView().setText(text),
                () -> getView().setBinary(buffer)
            );
    }
}
