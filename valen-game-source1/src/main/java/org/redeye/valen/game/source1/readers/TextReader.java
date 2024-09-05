package org.redeye.valen.game.source1.readers;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.io.*;

import java.io.*;
import java.nio.charset.*;

public class TextReader implements Reader<String> {
    @Override
    public String read(Archive archive, Asset asset, DataSource source) throws IOException {
        Charset charset = detectCharset(source);
        return new String(source.readBytes(Math.toIntExact(source.size() - source.tell())), charset);
    }

    @Override
    public boolean canRead(Asset asset) {
        return asset.type() == AssetType.Text;
    }

    private Charset detectCharset(DataSource source) throws IOException {
        if (source.size() >= 2) {
            int b0 = Byte.toUnsignedInt(source.readByte());
            int b1 = Byte.toUnsignedInt(source.readByte());
            if (b0 == 0xFE && b1 == 0xFF) {
                return StandardCharsets.UTF_16BE;
            }
            if (b0 == 0xFF && b1 == 0xFE) {
                return StandardCharsets.UTF_16LE;
            }

            if (source.size() >= 3) {
                int b2 = Byte.toUnsignedInt(source.readByte());
                if (b0 == 0xEF && b1 == 0xBB && b2 == 0xBF) {
                    return StandardCharsets.UTF_8;
                }

                if (source.size() >= 4) {
                    int b3 = Byte.toUnsignedInt(source.readByte());
                    if (b0 == 0x00 && b1 == 0x00 && b2 == 0xFF && b3 == 0xFF) {
                        return Charset.forName("UTF-32BE");
                    }
                    if (b0 == 0xFF && b1 == 0xFF && b2 == 0x00 && b3 == 0x00) {
                        return Charset.forName("UTF-32LE");
                    }
                }
            }
        }

        source.seek(0);
        return StandardCharsets.UTF_8;
    }
}
