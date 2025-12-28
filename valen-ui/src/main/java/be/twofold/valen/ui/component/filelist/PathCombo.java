package be.twofold.valen.ui.component.filelist;

import be.twofold.valen.core.util.*;
import wtf.reversed.toolbox.util.*;

record PathCombo(
    String full,
    String name
) implements Comparable<PathCombo> {
    public PathCombo {
        Check.nonNull(full, "full");
        Check.nonNull(name, "name");
    }

    @Override
    public int compareTo(PathCombo o) {
        return AlphanumericComparator.instance()
            .compare(full, o.full);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof PathCombo pathCombo
            && full.equals(pathCombo.full);
    }

    @Override
    public int hashCode() {
        return full.hashCode();
    }

    @Override
    public String toString() {
        return name;
    }
}
