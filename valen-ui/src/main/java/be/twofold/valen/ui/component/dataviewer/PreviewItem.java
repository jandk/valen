package be.twofold.valen.ui.component.dataviewer;

import be.twofold.valen.core.util.*;

record PreviewItem(
    String name,
    Object value
) {
    PreviewItem {
        Check.notNull(name, "name");
    }
}
