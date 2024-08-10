package be.twofold.valen.core.game;

import java.io.*;
import java.util.*;

public interface Archive<T extends AssetIdentifier> {

    List<Asset<T>> assets();

    Object loadAsset(Asset<T> asset) throws IOException;

}
