package be.twofold.valen.game.idtech.decoder;

import be.twofold.valen.core.texture.*;
import wtf.reversed.toolbox.collect.*;

import java.io.*;

@FunctionalInterface
public interface PageDecoder {
    Surface decode(Bytes bytes, int width, int height) throws IOException;
}
