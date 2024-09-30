package org.redeye.valen.game.spacemarines2.serializers.template;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.math.*;
import org.redeye.valen.game.spacemarines2.fio.*;
import org.redeye.valen.game.spacemarines2.types.template.*;

import java.io.*;
import java.util.*;

public class TplSkinSerializer implements FioSerializer<TplSkin> {

    @Override
    public TplSkin load(DataSource source) throws IOException {
        var skin = new TplSkin();
        skin.nBones = source.readInt();
        skin.state = new FioBitSetFlagsSerializer().load(source);
        if (skin.state.get(0)) {
            skin.boneInvBindMatrList = new ArrayList<>(skin.nBones);
            for (int i = 0; i < skin.nBones; i++) {
                skin.boneInvBindMatrList.add(Matrix4.identity());
            }
        }
        Chunk chunk = Chunk.read(source);
        while (!chunk.isTerminator()) {
            switch (chunk.id()) {
                case 0 -> {
                    skin.boneInvBindMatrList = new ArrayList<>();
                    for (int i = 0; i < skin.nBones; i++) {
                        skin.boneInvBindMatrList.add(Matrix4.fromArray(source.readFloats(16)));
                    }
                }
                case 1 -> {
                    var count = source.readInt();
                    skin.lodBonesCount = new ArrayList<>();
                    for (int i = 0; i < count; i++) {
                        skin.lodBonesCount.add(source.readShort());
                    }
                }
                default -> throw new IllegalArgumentException("Unrecognized chunk id: " + chunk.id());
            }
            if (chunk.endOffset() != source.tell()) {
                System.err.printf("TplSkinSerializer: Under/over read of chunk. Expected %d, got %d%n%n", chunk.endOffset(), source.tell());
                source.seek(chunk.endOffset());
            }
            chunk = Chunk.read(source);
        }
        return skin;
    }

    @Override
    public int flags() {
        return 0;
    }

}
