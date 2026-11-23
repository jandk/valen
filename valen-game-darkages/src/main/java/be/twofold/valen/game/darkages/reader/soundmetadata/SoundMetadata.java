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
        try (var reader = BinaryReader.open(path)) {
            return read(reader);
        }
    }

    public static SoundMetadata read(BinaryReader reader) throws IOException {
        var banks = reader.readObjects(reader.readInt(), Bank::read);
        var auxBuses = reader.readObjects(reader.readInt(), NameId::readReversed);
        var rtpcs = reader.readObjects(reader.readInt(), NameId::read);
        var switchGroups = reader.readObjects(reader.readInt(), Group::read);
        var stateGroups = reader.readObjects(reader.readInt(), Group::read);
        var events = reader.readObjects(reader.readInt(), Event::read);
        var containers = reader.readObjects(reader.readInt(), Container::read);

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
