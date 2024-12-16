package be.twofold.valen.ui.viewer.data;

import be.twofold.valen.core.util.*;

record PreviewItem(
    String name,
    Object value
) {
    PreviewItem {
        Check.notNull(name);
    }
}
