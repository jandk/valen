package be.twofold.valen.game.doom.readers.md6anim;

import be.twofold.valen.game.doom.*;
import be.twofold.valen.game.idtech.game.*;
import org.junit.jupiter.api.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.util.*;

class Md6AnimReaderIT {

    // Broken animations
    private static final Set<String> BROKEN = Set.of(
        "md6/objects/props_set/kiosks/automap_hell/assets/mesh/automap_hell.md6skl/_default.md6anim",
        "md6/objects/weapons/explosives/mp_slicer/assets/mesh/mp_slicer.md6skl/_default.md6anim"
    );

    @Test
    void testCanReadAll() throws IOException {
        var reader = new Md6AnimReader();
        TestUtils.testAssets(reader::canRead, BROKEN, (asset, loader) -> {
            Md6Anim anim;
            try (var binaryReader = BinaryReader.wrap(loader.open(asset.location()))) {
                anim = Md6Anim.read(binaryReader);
            }

            // Some don't even have a skeleton...
            var skeletonId = new DoomAssetID(anim.header().skelName(), ResourceType.of("skeleton"));
            if (loader.exists(skeletonId)) {
                reader.read(asset, loader);
            }
        });
    }

}
