package be.twofold.valen.game.neworder;

import be.twofold.valen.core.game.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public final class NewOrderGameFactory implements GameFactory<NewOrderGame> {
    @Override
    public Set<String> executableNames() {
        return Set.of("WolfNewOrder_x64.exe");
    }

    @Override
    public NewOrderGame load(Path path) throws IOException {
        return new NewOrderGame(path.getParent());
    }
}
