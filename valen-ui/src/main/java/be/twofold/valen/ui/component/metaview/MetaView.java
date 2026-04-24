package be.twofold.valen.ui.component.metaview;

import be.twofold.valen.core.util.*;
import be.twofold.valen.ui.common.*;

public interface MetaView extends View<View.Listener> {

    void setRoot(Meta.Node root);

    void clear();

}
