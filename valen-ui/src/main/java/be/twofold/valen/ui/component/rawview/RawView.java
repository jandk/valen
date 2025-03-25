package be.twofold.valen.ui.component.rawview;

import java.nio.*;

// TODO: Turn this into a single union type
public interface RawView {

    void setBinary(ByteBuffer buffer);

    void setText(String text);

    void clear();

}
