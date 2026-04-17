package be.twofold.valen.ui.component.metaview;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.ui.common.*;
import be.twofold.valen.ui.component.*;
import jakarta.inject.*;

public final class MetaPresenter extends AbstractPresenter<MetaView> implements Viewer {
    @Inject
    public MetaPresenter(MetaView view) {
        super(view);
    }

    @Override
    public String getName() {
        return "Metadata";
    }

    @Override
    public boolean canPreview(AssetType type) {
        return true;
    }

    @Override
    public void setData(Object data) {
        if (data instanceof Meta.Node node) {
            getView().setRoot(node);
        } else {
            getView().clear();
        }
    }
}
