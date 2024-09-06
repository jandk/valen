package be.twofold.valen.ui.viewers;

import be.twofold.valen.core.game.*;
import javafx.scene.*;

import java.io.*;

public interface Viewer {

    boolean canPreview(Asset asset);

    boolean setData(Asset asset, Archive archive) throws IOException;

    Node getNode();

    String getName();
}
