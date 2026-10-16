package be.twofold.valen.middleware.wwise.wem;

import be.twofold.valen.core.audio.*;
import be.twofold.valen.core.game.*;
import be.twofold.valen.middleware.wwise.shared.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;

public final class WemReader implements AssetReader.Binary<Audio, Asset> {
    @Override
    public boolean canRead(Asset asset) {
        return asset instanceof WwiseAsset;
    }

    @Override
    public Audio read(BinarySource source, Asset asset, LoadingContext context) throws IOException {
        var wem = Wem.read(source);
        source.position(wem.dataOffset());

        return new Audio(
            wem.toAudioCodec(),
            wem.format().samplesPerSec(),
            wem.toChannels(),
            wem.toFrameCount(),
            source.readBytes(wem.dataSize())
        );
    }
}
