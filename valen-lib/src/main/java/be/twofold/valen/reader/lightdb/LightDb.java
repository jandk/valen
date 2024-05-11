package be.twofold.valen.reader.lightdb;

import be.twofold.valen.core.io.*;
import be.twofold.valen.reader.image.*;
import be.twofold.valen.reader.streamdb.*;

import java.io.*;
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
    public static LightDb read(DataSource source) throws IOException {
//        var header = source.readStruct(LightDbHeader::read);
//
//        // source.expectPosition(header.indexOffset());
//        var indexEntries = source.readStructs(LightDbIndexEntry::read, header.hashLength());
//
//        // source.expectPosition(header.hashOffset());
//        var hashes = source.readLongs(header.hashLength());
//        var hashIds = source.readInts(header.hashLength());
//
//        // source.expectPosition(header.imageOffset());
//        var imageReader = new ImageReader(null);
//        var imageBuffer = IOUtils.readBuffer(source, header.nameOffset() - header.imageOffset());
//        var imageHeaders = new ArrayList<LightDbImageHeader>();
//        var images = new ArrayList<Image>();
//        for (var i = 0; i < header.imageCount(); i++) {
//            imageHeaders.add(LightDbImageHeader.read(imageBuffer));
//            images.add(imageReader.read(imageBuffer, false, 0));
//        }
//
//        IOUtils.expectPosition(source, header.nameOffset());
//        var nameBuffer = IOUtils.readBuffer(source, header.dbOffset16() * 16 - header.nameOffset());
//        var nameGroupMagic = nameBuffer.getInt();
//        Check.state(nameGroupMagic == 0x758ac962 || nameGroupMagic == 0x758ac961, "Invalid name group magic");
//        var nameGroupCount = nameBuffer.getInt();
//        var nameGroups = nameBuffer.getStructs(nameGroupCount, LightDbNameGroup::read);
//
//        var unknownInts = nameBuffer.getInts(6);
//        var numParts1 = nameBuffer.getInt();
//        var numParts2 = nameBuffer.getInt();
//        nameBuffer.expectInt(0);
//
//        List<LightDbPart1> parts1 = nameBuffer.getStructs(numParts1, LightDbPart1::read);
//        List<LightDbPart2> parts2 = nameBuffer.getStructs(numParts2, LightDbPart2::read);
//
//        StreamDb streamDb = StreamDb.read(source);
//
//        return new LightDb(
//            header,
//            indexEntries,
//            hashes,
//            hashIds,
//            imageHeaders,
//            images,
//            nameGroups,
//            unknownInts,
//            parts1,
//            parts2,
//            streamDb
//        );

        throw new UnsupportedOperationException("Not implemented");
    }
}
