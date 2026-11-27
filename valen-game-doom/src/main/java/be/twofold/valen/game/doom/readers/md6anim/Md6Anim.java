package be.twofold.valen.game.doom.readers.md6anim;

import be.twofold.valen.game.idtech.game.*;
import be.twofold.valen.game.idtech.md6anim.*;
import org.slf4j.*;
import wtf.reversed.toolbox.io.*;
import wtf.reversed.toolbox.math.*;

import java.io.*;
import java.nio.*;
import java.util.*;

public record Md6Anim(
    Md6AnimHeader header,
    Md6AnimData data,
    List<Md6AnimMap> animMaps,
    List<FrameSet> frameSets,
    List<Quaternion> constR,
    List<Vector3> constS,
    List<Vector3> constT
) {
    private static final Logger log = LoggerFactory.getLogger(Md6Anim.class);

    public static Md6Anim read(BinaryReader reader) throws IOException {
        reader.order(ByteOrder.BIG_ENDIAN);
        var header = Md6AnimHeader.read(reader);

        var start = Math.toIntExact(reader.position());
        var data = Md6AnimData.read(reader);
        var animMaps = readAnimMaps(reader);
        if (animMaps.isEmpty()) {
            // The default anim of a skeleton has no frames and no anim maps
            return new Md6Anim(header, data, animMaps, List.of(), List.of(), List.of(), List.of());
        }
        if (animMaps.size() > 1) {
            log.warn("Multiple animMaps found, using the first one");
        }

        var animMap = animMaps.getFirst();
        var constR = reader.position(start + data.constROffset()).readObjects(animMap.constR().length(), Md6AnimDecoder::decodeQuat);
        var constS = reader.position(start + data.constSOffset()).readObjects(animMap.constS().length(), Vector3::read);
        var constT = reader.position(start + data.constTOffset()).readObjects(animMap.constT().length(), Vector3::read);

        var frameSetTable = reader.position(start + data.frameSetTblOffset()).readBytes(data.numFrames());
        var frameSetOffsetTable = reader.position(start + data.frameSetOffsetTblOffset()).readShorts(data.numFrameSets() + 1);

        var frameSets = new ArrayList<FrameSet>();
        for (var i = 0; i < data.numFrameSets(); i++) {
            var frameSetOffset = start + Short.toUnsignedInt(frameSetOffsetTable.get(i)) * 16;
            var frameSet = FrameSet.read(reader, frameSetOffset, animMap.animR().length(), animMap.animS().length(), animMap.animT().length(), false);
            frameSets.add(frameSet);
        }

        return new Md6Anim(header, data, animMaps, frameSets, constR, constS, constT);
    }

    private static List<Md6AnimMap> readAnimMaps(BinaryReader reader) throws IOException {
        var start = Math.toIntExact(reader.position());
        var numAnimMaps = reader.readUnsignedShort();
        reader.skip(numAnimMaps * 2L); // tableCRCs
        var offsets = reader.readObjects(numAnimMaps, Md6AnimMapOffsets::read);

        var animMaps = new ArrayList<Md6AnimMap>();
        for (var offset : offsets) {
            animMaps.add(Md6AnimMap.read(reader, start, offset, IdTechGame.DOOM));
        }
        return animMaps;
    }
}
