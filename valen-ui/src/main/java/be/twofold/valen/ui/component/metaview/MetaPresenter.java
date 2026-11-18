package be.twofold.valen.ui.component.metaview;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.ui.common.*;
import be.twofold.valen.ui.component.*;

public final class MetaPresenter extends AbstractPresenter<MetaView> implements Viewer {
    public MetaPresenter(MetaView view) {
        super(view);
    }

    @Override
    public boolean canPreview(AssetType type) {
        return true;
    }

    @Override
    public Object decode(Object data) {
        if (!(data instanceof Meta.Node)) {
            return null;
        }
        return data;
    }

    @Override
    public void display(Object data) {
        if (data instanceof Meta.Node node) {
            getView().setRoot(node);
        } else {
            getView().clear();
        }
    }
}
