package be.twofold.valen.game.darkages.reader.soundmetadata;

import be.twofold.valen.game.idtech.reader.soundmetadata.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public record SoundMetadata(
    List<Bank> banks,
    List<NameId> auxBuses,
    List<NameId> rtpcs,
    List<Group> switchGroups,
    List<Group> stateGroups,
    List<Event> events,
    List<Container> containers
) {
    public static SoundMetadata load(Path path) throws IOException {
        try (var source = BinarySource.open(path)) {
            return read(source);
        }
    }

    public static SoundMetadata read(BinarySource source) throws IOException {
        var banks = source.readObjects(source.readInt(), Bank::read);
        var auxBuses = source.readObjects(source.readInt(), NameId::readReversed);
        var rtpcs = source.readObjects(source.readInt(), NameId::read);
        var switchGroups = source.readObjects(source.readInt(), Group::read);
        var stateGroups = source.readObjects(source.readInt(), Group::read);
        var events = source.readObjects(source.readInt(), Event::read);
        var containers = source.readObjects(source.readInt(), Container::read);

        return new SoundMetadata(
            banks,
            auxBuses,
            rtpcs,
            switchGroups,
            stateGroups,
            events,
            containers
        );
    }
}
