package org.redeye.valen.game.source1.providers;

import be.twofold.valen.core.game.*;
import org.redeye.valen.game.source1.*;
import org.redeye.valen.game.source1.readers.*;
import org.redeye.valen.game.source1.readers.vtf.*;

import java.util.*;

public interface Provider extends Archive {
    AssetReaders<SourceAsset> readers = new AssetReaders<>(List.of(
        new KeyValueReader(),
        new VmtReader(),
        new VtfReader()
    ));

    String getName();

    Provider getParent();
}
