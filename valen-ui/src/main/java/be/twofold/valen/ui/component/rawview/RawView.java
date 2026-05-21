package be.twofold.valen.ui.component.rawview;

import wtf.reversed.toolbox.collect.*;

// TODO: Turn this into a single union type
public interface RawView {

    void setBinary(Bytes bytes);

    void setText(String text);

    void clear();

}
