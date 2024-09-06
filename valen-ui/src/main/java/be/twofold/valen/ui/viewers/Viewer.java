package be.twofold.valen.ui.viewers;

import be.twofold.valen.core.game.*;
import javafx.scene.*;

import java.io.*;

public interface Viewer {

    boolean canPreview(Asset asset);

    boolean setData(Archive archive, Asset asset) throws IOException;

    void reset();

    Node getNode();

    String getName();
}
