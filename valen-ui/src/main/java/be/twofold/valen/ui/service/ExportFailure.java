package be.twofold.valen.ui.service;

import be.twofold.valen.core.game.*;

public record ExportFailure(
    AssetID id,
    String message
) {
}
