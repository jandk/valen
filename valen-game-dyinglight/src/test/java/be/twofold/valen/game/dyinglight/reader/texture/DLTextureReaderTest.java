package be.twofold.valen.game.dyinglight.reader.texture;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.core.util.collect.*;
import be.twofold.valen.game.dyinglight.*;
import org.junit.jupiter.api.*;

import java.io.*;
import java.nio.file.*;

class DLTextureReaderTest {
    @Test
    void testCanReadAllTextures() throws IOException {
        TestUtils.testReader(archive -> new DLTextureReader());
    }

    @Test
    void collectTextureInfo() throws IOException {
        var game = new DyingLightGameFactory().load(Path.of(Constants.ExecutablePath));
        var writer = new BufferedWriter(new FileWriter("texture_info.csv"));
        writer.write("name,unk0,unk1,width,height,layers,format,unk3,unk4,unk5\n");
        for (String archiveName : game.archiveNames()) {
            try (var archive = game.loadArchive(archiveName)) {
                var entries = archive.getAll()
                    .filter(asset -> asset.size() != 0 && asset.type() == AssetType.TEXTURE);
                for (DyingLightAsset asset : entries.toList()) {
                    try {
                        var buffer = archive.loadAsset(asset.id(), Bytes.class);
                        var reader = DLTextureHeader.read(BinaryReader.fromBytes(buffer));
                        writer.write(String.format(
                            "%s,%d,%d,%d,%d,%d,%s,%d,%d,%d\n",
                            asset.id().fullName(),
                            reader.unk0(),
                            reader.unk1(),
                            reader.width(),
                            reader.height(),
                            reader.layers(),
                            reader.format().name(),
                            reader.unk3(),
                            reader.unk4(),
                            reader.unk5()
                        ));
                    } catch (Exception e) {
                        System.err.println("Failed to read texture: " + asset.id().fullName());
                    }
                }
            }
        }
    }
}