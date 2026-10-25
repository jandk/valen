package be.twofold.valen.game.eternal.reader.soundcontainer;

import be.twofold.valen.core.audio.*;
import be.twofold.valen.core.game.*;
import be.twofold.valen.game.eternal.*;
import be.twofold.valen.middleware.wwise.wem.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;

public final class SoundReader implements AssetReader.Binary<Audio, Asset> {
    @Override
    public boolean canRead(Asset asset) {
        return asset instanceof SoundAsset;
    }

    @Override
    public Audio read(BinarySource source, Asset asset, LoadingContext context) throws IOException {
        var soundAsset = (SoundAsset) asset;
        var wem = Wem.read(BinarySource.wrap(soundAsset.header()));
        var format = wem.format();

        return new Audio(
            AudioCodec.OGG_OPUS,
            format.samplesPerSec(),
            wem.toChannels(),
            wem.declaredDataSize() / format.blockAlign(),
            source.readBytes(Math.toIntExact(source.remaining()))
        );
    }
}
