package be.twofold.valen.ui.component.main;

import be.twofold.valen.core.game.*;

record ExportFailure(
    AssetID id,
    String message
) {
}
