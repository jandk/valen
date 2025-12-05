package be.twofold.valen.game.eternal.reader.lightdb;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.core.util.collect.*;
import be.twofold.valen.game.eternal.reader.image.*;
import be.twofold.valen.game.eternal.reader.streamdb.*;

import java.io.*;
import java.util.*;

public record LightDb(
    LightDbHeader header,
    List<LightDbIndexEntry> indexEntries,
    Longs hashes,
    Ints hashIds,
    List<LightDbImageHeader> imageHeaders,
    List<Image> images,
    List<LightDbNameGroup> nameGroups,
    Ints unknownInts,
    List<LightDbPart1> parts1,
    List<LightDbPart2> parts2,
    StreamDb streamDb
) {
    public static LightDb read(BinaryReader reader) throws IOException {
        var header = LightDbHeader.read(reader);

        reader.expectPosition(header.indexOffset());
        var indexEntries = reader.readObjects(header.hashLength(), LightDbIndexEntry::read);

        reader.expectPosition(header.hashOffset());
        var hashes = reader.readLongs(header.hashLength());
        var hashIds = reader.readInts(header.hashLength());

        reader.expectPosition(header.imageOffset());
        var imageHeaders = new ArrayList<LightDbImageHeader>();
        var images = new ArrayList<Image>();
        for (var i = 0; i < header.imageCount(); i++) {
            imageHeaders.add(LightDbImageHeader.read(reader));
            images.add(Image.read(reader));
        }

        reader.expectPosition(header.nameOffset());
        var nameGroupMagic = reader.readInt();
        Check.state(nameGroupMagic == 0x758ac962 || nameGroupMagic == 0x758ac961, "Invalid name group magic");
        var nameGroups = reader.readObjects(reader.readInt(), LightDbNameGroup::read);

        var unknownInts = reader.readInts(6);
        var numParts1 = reader.readInt();
        var numParts2 = reader.readInt();
        reader.expectInt(0);

        List<LightDbPart1> parts1 = reader.readObjects(numParts1, LightDbPart1::read);
        List<LightDbPart2> parts2 = reader.readObjects(numParts2, LightDbPart2::read);

        StreamDb streamDb = StreamDb.read(reader);

        return new LightDb(
            header,
            indexEntries,
            hashes,
            hashIds,
            imageHeaders,
            images,
            nameGroups,
            unknownInts,
            parts1,
            parts2,
            streamDb
        );
    }
}
