package be.twofold.valen.ui.component.rawview;

import wtf.reversed.toolbox.collect.*;

public sealed interface RawPayload {
    record Binary(Bytes binary) implements RawPayload {
    }

    record Text(String text) implements RawPayload {
    }
}
