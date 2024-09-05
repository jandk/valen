package be.twofold.valen.game.neworder.master;

import be.twofold.valen.core.io.*;

import java.io.*;

public record MasterContainer(String indexName, String resourceName) {
    public static MasterContainer read(DataSource source) throws IOException {
        var indexName = source.readPString();
        var resourceName = source.readPString();
        return new MasterContainer(indexName, resourceName);
    }
}
