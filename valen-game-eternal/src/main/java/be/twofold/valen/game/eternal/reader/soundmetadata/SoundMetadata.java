package be.twofold.valen.game.eternal.reader.soundmetadata;

import be.twofold.valen.game.idtech.reader.soundmetadata.*;
import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public record SoundMetadata(
    List<NameId> packages,
    List<Container> containers,
    List<NameId> banks,
    List<NameId> auxBuses,
    List<NameId> rtpcs,
    List<Group> switchGroups,
    List<Group> stateGroups,
    StringPool stringPool,
    List<Event> events,
    Ints prefetches
) {
    public static SoundMetadata load(Path path) throws IOException {
        try (var reader = BinaryReader.open(path)) {
            return read(reader);
        }
    }

    public static SoundMetadata read(BinaryReader reader) throws IOException {
        reader.expectInt(23); // version
        var packages = reader.readObjects(reader.readInt(), NameId::read);
        var containers = reader.readObjects(reader.readInt(), Container::read);
        var banks = reader.readObjects(reader.readInt(), NameId::read);
        var auxBuses = reader.readObjects(reader.readInt(), NameId::readReversed);
        var rtpcs = reader.readObjects(reader.readInt(), NameId::readReversed);
        var switchGroups = reader.readObjects(reader.readInt(), Group::read);
        var stateGroups = reader.readObjects(reader.readInt(), Group::read);
        var stringPool = StringPool.read(reader);
        var events = reader.readObjects(reader.readInt(), Event::read);
        var prefetches = reader.readInts(reader.readInt());

        return new SoundMetadata(
            packages,
            containers,
            banks,
            auxBuses,
            rtpcs,
            switchGroups,
            stateGroups,
            stringPool,
            events,
            prefetches
        );
    }
}
