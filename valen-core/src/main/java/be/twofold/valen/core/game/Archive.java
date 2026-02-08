package be.twofold.valen.core.game;

import java.util.*;
import java.util.stream.*;

public interface Archive {

    Optional<Asset> get(AssetID id);

    Stream<Asset> all();

}
