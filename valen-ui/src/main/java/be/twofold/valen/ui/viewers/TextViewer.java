package be.twofold.valen.ui.viewers;

import be.twofold.valen.core.game.*;
import javafx.scene.*;
import javafx.scene.control.*;

import java.io.*;
import java.nio.*;
import java.nio.charset.*;

public class TextViewer extends TextArea implements Viewer {
    @Override
    public boolean canPreview(Asset asset) {
        return asset.tags().contains(AssetTypeTag.Text);
    }

    @Override
    public boolean setData(Archive archive, Asset asset) throws IOException {
        ByteBuffer data = archive.loadRawAsset(asset.id());
        byte[] bytes = new byte[data.remaining()];
        data.get(bytes);
        setText(new String(bytes, StandardCharsets.UTF_8));
        return true;
    }

    @Override
    public void reset() {
        setText("");
    }

    @Override
    public Node getNode() {
        return this;
    }

    @Override
    public String getName() {
        return "Text";
    }
}
