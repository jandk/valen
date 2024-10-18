package org.redeye.valen.game.spacemarines2.fio;

import be.twofold.valen.core.io.*;
import org.redeye.valen.game.spacemarines2.psSection.*;

import java.io.*;

public class FioPsSectionSerializer implements FioSerializer<PsSectionValue.PsSectionObject> {
    @Override
    public PsSectionValue.PsSectionObject load(DataSource source) throws IOException {
        String val = source.readPString();
        return PsSectionAscii.parseFromString(val);
    }

    @Override
    public int flags() {
        return 9;
    }
}
