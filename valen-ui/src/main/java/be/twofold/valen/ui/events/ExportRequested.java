package be.twofold.valen.ui.events;

import backbonefx.event.*;

public record ExportRequested(
    String path,
    boolean recursive
) implements Event {
}
