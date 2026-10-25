package be.twofold.valen.ui.component.filelist;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.util.*;
import wtf.reversed.toolbox.util.*;

record PathCombo(
    AssetPath path,
    String name
) implements Comparable<PathCombo> {
    public PathCombo {
        Check.nonNull(path, "path");
        Check.nonNull(name, "name");
    }

    @Override
    public int compareTo(PathCombo o) {
        return AlphanumericComparator.instance()
            .compare(path.value(), o.path.value());
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof PathCombo pathCombo
            && path.equals(pathCombo.path);
    }

    @Override
    public int hashCode() {
        return path.hashCode();
    }

    @Override
    public String toString() {
        return name;
    }
}
