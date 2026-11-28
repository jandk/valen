package be.twofold.valen.game.doom.readers.md6skel;

import be.twofold.valen.game.doom.readers.*;
import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;
import wtf.reversed.toolbox.math.*;

import java.io.*;
import java.nio.*;
import java.util.*;

public record Md6Skel(
    Md6SkelHeader header,
    Md6SkelData data,
    List<Quaternion> rotations,
    List<Vector3> scales,
    List<Vector3> translations,
    Shorts parents,
    List<Matrix4> inverseBasePoses,
    List<String> names,
    List<String> userChannelNames,
    Floats userChannelValues
) {
    public static Md6Skel read(BinaryReader reader) throws IOException {
        reader.order(ByteOrder.BIG_ENDIAN);
        var header = Md6SkelHeader.read(reader);
        var data = Md6SkelData.read(reader);

        reader.position(Md6SkelHeader.BYTES + data.basePoseOffset());
        var rotations = reader.readObjects(data.numJoints8(), Quaternion::read);
        var scales = reader.readObjects(data.numJoints8(), Vector3::read);
        var translations = reader.readObjects(data.numJoints8(), Vector3::read);

        reader.position(Md6SkelHeader.BYTES + data.parentTblOffset());
        var parents = reader.readShorts(data.numJoints8());

        reader.position(Md6SkelHeader.BYTES + data.inverseBasePoseOffset());
        var inverseBasePoses = reader.readObjects(data.numJoints8(), Matrix4::read3x4);

        reader.position(Md6SkelHeader.BYTES + header.dataSize());
        var names = IdStrings.read(reader, data.numJoints8());
        var userChannelNames = IdStrings.read(reader, data.numUserChannels8());
        var userChannelValues = reader.readFloats(data.numUserChannels8());
        reader.expectInt(0x0E02_534D);

        return new Md6Skel(
            header,
            data,
            rotations,
            scales,
            translations,
            parents,
            inverseBasePoses,
            names,
            userChannelNames,
            userChannelValues
        );
    }
}
