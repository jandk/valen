package be.twofold.valen.game.doom.vmtr;

import be.twofold.valen.game.doom.*;
import be.twofold.valen.game.doom.Constants;
import be.twofold.valen.game.doom.megatexture.*;
import be.twofold.valen.game.doom.megatexture.vmtr.*;
import org.junit.jupiter.api.*;

import java.io.*;
import java.nio.file.*;

import static org.assertj.core.api.Assertions.*;

class VmtrReaderIT {

    @Test
    void testCanReadAll() throws IOException {
        var virtualTextures = Path.of(Constants.ExecutablePath).resolveSibling("virtualtextures");
        var entries = Vmtr.readAll(virtualTextures);

        try (var grid = Mega2Grid.open(virtualTextures)) {
            var reader = new VmtrReader(new PageStitcher(grid));

            System.out.println("Trying to read " + entries.size() + " entries");

            for (var entry : entries) {
                for (var layer : VmtrLayer.values()) {
                    var asset = new DoomAsset.Vmtr(entry, layer);
                    try {
                        reader.read(asset, null);
                    } catch (Exception e) {
                        fail(asset.id().fullName(), e);
                    }
                }
            }
        }
    }

}
