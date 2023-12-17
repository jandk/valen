package be.twofold.valen.reader.lightdb;

import be.twofold.valen.core.util.*;
import be.twofold.valen.reader.image.*;
import be.twofold.valen.reader.streamdb.*;

import java.io.*;
import java.nio.channels.*;
import java.util.*;

public record LightDb(
    LightDbHeader header,
    List<LightDbIndexEntry> indexEntries,
    long[] hashes,
    int[] hashIds,
    ArrayList<LightDbImageHeader> imageHeaders,
    ArrayList<Image> images,
    List<LightDbNameGroup> nameGroups,
    int[] unknownInts,
    List<LightDbPart1> parts1,
    List<LightDbPart2> parts2,
    StreamDb streamDb
) {
    public static LightDb read(SeekableByteChannel channel) throws IOException {
        var header = IOUtils.readStruct(channel, LightDbHeader.BYTES, LightDbHeader::read);

        IOUtils.expectPosition(channel, header.indexOffset());
        var indexEntries = IOUtils.readStructs(channel, header.hashLength(), LightDbIndexEntry.BYTES, LightDbIndexEntry::read);

        IOUtils.expectPosition(channel, header.hashOffset());
        var hashes = IOUtils.readLongs(channel, header.hashLength());
        var hashIds = IOUtils.readInts(channel, header.hashLength());

        IOUtils.expectPosition(channel, header.imageOffset());
        var imageReader = new ImageReader(null);
        var imageBuffer = IOUtils.readBuffer(channel, header.nameOffset() - header.imageOffset());
        var imageHeaders = new ArrayList<LightDbImageHeader>();
        var images = new ArrayList<Image>();
        for (var i = 0; i < header.imageCount(); i++) {
            imageHeaders.add(LightDbImageHeader.read(imageBuffer));
            images.add(imageReader.read(imageBuffer, false, 0));
        }

        IOUtils.expectPosition(channel, header.nameOffset());
        var nameBuffer = IOUtils.readBuffer(channel, header.dbOffset16() * 16 - header.nameOffset());
        var nameGroupMagic = nameBuffer.getInt();
        Check.state(nameGroupMagic == 0x758ac962 || nameGroupMagic == 0x758ac961, "Invalid name group magic");
        var nameGroupCount = nameBuffer.getInt();
        var nameGroups = nameBuffer.getStructs(nameGroupCount, LightDbNameGroup::read);

        var unknownInts = nameBuffer.getInts(6);
        var numParts1 = nameBuffer.getInt();
        var numParts2 = nameBuffer.getInt();
        nameBuffer.expectInt(0);

        List<LightDbPart1> parts1 = nameBuffer.getStructs(numParts1, LightDbPart1::read);
        List<LightDbPart2> parts2 = nameBuffer.getStructs(numParts2, LightDbPart2::read);

        StreamDb streamDb = StreamDb.read(channel);

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
