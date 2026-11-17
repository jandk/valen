package be.twofold.valen.game.darkages;

import be.twofold.valen.core.game.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public final class DarkAgesGameFactory implements GameFactory<DarkAgesGame> {
    @Override
    public String displayName() {
        return "DOOM: The Dark Ages";
    }

    @Override
    public int steamAppId() {
        return 3017860;
    }

    @Override
    public Set<String> executableNames() {
        return Set.of("DOOMTheDarkAges.exe");
    }

    @Override
    public DarkAgesGame load(Path path) throws IOException {
        return new DarkAgesGame(path.getParent());
    }
}
