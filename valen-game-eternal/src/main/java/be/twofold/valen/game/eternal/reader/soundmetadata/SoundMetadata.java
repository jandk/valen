package be.twofold.valen.game.eternal.reader.soundmetadata;

import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
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
    public static SoundMetadata read(BinarySource source) throws IOException {
        source.expectInt(23); // version
        var packages = source.readObjects(source.readInt(), NameId::read);
        var containers = source.readObjects(source.readInt(), Container::read);
        var banks = source.readObjects(source.readInt(), NameId::read);
        var auxBuses = source.readObjects(source.readInt(), NameId::readReversed);
        var rtpcs = source.readObjects(source.readInt(), NameId::readReversed);
        var switchGroups = source.readObjects(source.readInt(), Group::read);
        var stateGroups = source.readObjects(source.readInt(), Group::read);
        var stringPool = StringPool.read(source);
        var events = source.readObjects(source.readInt(), Event::read);
        var prefetches = source.readInts(source.readInt());

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
