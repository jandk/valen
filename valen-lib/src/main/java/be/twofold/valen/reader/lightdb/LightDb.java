package be.twofold.valen.reader.lightdb;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.reader.image.*;
import be.twofold.valen.reader.streamdb.*;

import java.io.*;
import java.util.*;

public record LightDb(
    LightDbHeader header,
    List<LightDbIndexEntry> indexEntries,
    long[] hashes,
    int[] hashIds,
    List<LightDbImageHeader> imageHeaders,
    List<Image> images,
    List<LightDbNameGroup> nameGroups,
    int[] unknownInts,
    List<LightDbPart1> parts1,
    List<LightDbPart2> parts2,
    StreamDb streamDb
) {
    public static LightDb read(DataSource source) throws IOException {
        var header = LightDbHeader.read(source);

        source.expectPosition(header.indexOffset());
        var indexEntries = source.readStructs(header.hashLength(), LightDbIndexEntry::read);

        source.expectPosition(header.hashOffset());
        var hashes = source.readLongs(header.hashLength());
        var hashIds = source.readInts(header.hashLength());

        source.expectPosition(header.imageOffset());
        var imageHeaders = new ArrayList<LightDbImageHeader>();
        var images = new ArrayList<Image>();
        for (var i = 0; i < header.imageCount(); i++) {
            imageHeaders.add(LightDbImageHeader.read(source));
            images.add(Image.read(source));
        }

        source.expectPosition(header.nameOffset());
        var nameGroupMagic = source.readInt();
        Check.state(nameGroupMagic == 0x758ac962 || nameGroupMagic == 0x758ac961, "Invalid name group magic");
        var nameGroups = source.readStructs(source.readInt(), LightDbNameGroup::read);

        var unknownInts = source.readInts(6);
        var numParts1 = source.readInt();
        var numParts2 = source.readInt();
        source.expectInt(0);

        List<LightDbPart1> parts1 = source.readStructs(numParts1, LightDbPart1::read);
        List<LightDbPart2> parts2 = source.readStructs(numParts2, LightDbPart2::read);

        StreamDb streamDb = StreamDb.read(source);

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
