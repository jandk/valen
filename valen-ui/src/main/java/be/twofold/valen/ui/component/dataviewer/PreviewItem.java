package be.twofold.valen.ui.component.dataviewer;

import wtf.reversed.toolbox.util.*;

record PreviewItem(
    String name,
    Object value
) {
    PreviewItem {
        Check.nonNull(name, "name");
    }
}
