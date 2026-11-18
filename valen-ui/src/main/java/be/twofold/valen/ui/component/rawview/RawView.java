package be.twofold.valen.ui.component.rawview;

import be.twofold.valen.ui.common.*;

public interface RawView extends View<View.Listener> {

    void setContent(RawPayload payload);

    void clear();

}
