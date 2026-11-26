package be.twofold.valen.game.eternal.reader.md6anim;

import be.twofold.valen.game.idtech.md6anim.*;
import org.slf4j.*;
import wtf.reversed.toolbox.io.*;
import wtf.reversed.toolbox.math.*;

import java.io.*;
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
        var header = Md6AnimHeader.read(reader);

        var start = Math.toIntExact(reader.position());
        var data = Md6AnimData.read(reader);
        var animMaps = readAnimMaps(reader);
        if (animMaps.size() > 1) {
            log.warn("Multiple animMaps found, using the first one");
        }

        var animMap = animMaps.getFirst();
        var constR = reader.position(start + data.constROffset()).readObjects(animMap.constR().length(), Md6AnimDecoder::decodeQuat);
        var constS = reader.position(start + data.constSOffset()).readObjects(animMap.constS().length(), Vector3::read);
        var constT = reader.position(start + data.constTOffset()).readObjects(animMap.constT().length(), Vector3::read);

        var frameSetTable = reader.position(start + data.frameSetTblOffset()).readBytes(data.numFrames());
        var frameSetOffsetTable = reader.position(start + data.frameSetOffsetTblOffset()).readInts(data.numFrameSets() + 1);

        var frameSets = new ArrayList<FrameSet>();
        for (var i = 0; i < data.numFrameSets(); i++) {
            var frameSetOffset = start + frameSetOffsetTable.get(i) * 16;
            var frameSet = FrameSet.read(reader, frameSetOffset, animMap.animR().length(), animMap.animS().length(), animMap.animT().length());
            frameSets.add(frameSet);
        }

        return new Md6Anim(header, data, animMaps, frameSets, constR, constS, constT);
    }

    private static List<Md6AnimMap> readAnimMaps(BinaryReader reader) throws IOException {
        var start = Math.toIntExact(reader.position());
        var numAnimMaps = reader.readShort();
        var tableCRCs = reader.readShorts(numAnimMaps);

        List<Md6AnimMapOffsets> offsets = new ArrayList<>();
        for (var i = 0; i < numAnimMaps; i++) {
            offsets.add(Md6AnimMapOffsets.read(reader));
        }

        List<Md6AnimMap> animMaps = new ArrayList<>();
        for (var i = 0; i < numAnimMaps; i++) {
            var offset = offsets.get(i);

            var constR = reader.position(start + offset.constRRLEOffset()).readObject(s -> Md6AnimDecoder.decodeRle(s, false));
            var constS = reader.position(start + offset.constSRLEOffset()).readObject(s -> Md6AnimDecoder.decodeRle(s, false));
            var constT = reader.position(start + offset.constTRLEOffset()).readObject(s -> Md6AnimDecoder.decodeRle(s, false));
            var constU = reader.position(start + offset.constURLEOffset()).readObject(s -> Md6AnimDecoder.decodeRle(s, false));

            var animR = reader.position(start + offset.animRRLEOffset()).readObject(s -> Md6AnimDecoder.decodeRle(s, false));
            var animS = reader.position(start + offset.animSRLEOffset()).readObject(s -> Md6AnimDecoder.decodeRle(s, false));
            var animT = reader.position(start + offset.animTRLEOffset()).readObject(s -> Md6AnimDecoder.decodeRle(s, false));
            var animU = reader.position(start + offset.animURLEOffset()).readObject(s -> Md6AnimDecoder.decodeRle(s, false));

            animMaps.add(new Md6AnimMap(tableCRCs.get(i), constR, constS, constT, constU, animR, animS, animT, animU));
        }
        return animMaps;
    }
}
