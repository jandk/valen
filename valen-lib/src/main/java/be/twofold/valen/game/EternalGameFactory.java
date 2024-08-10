package be.twofold.valen.game;

import be.twofold.valen.core.game.*;

import java.io.*;
import java.nio.file.*;

public final class EternalGameFactory implements GameFactory<EternalGame> {
    public EternalGameFactory() {
    }

    @Override
    public String executableName() {
        return "DOOMEternalx64vk.exe";
    }

    @Override
    public EternalGame load(Path path) throws IOException {
        return new EternalGame(path.getParent());
    }
}
