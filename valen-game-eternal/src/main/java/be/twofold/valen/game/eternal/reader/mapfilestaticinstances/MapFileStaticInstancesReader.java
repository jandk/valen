package be.twofold.valen.game.eternal.reader.mapfilestaticinstances;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.core.scene.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.core.util.fi.*;
import be.twofold.valen.game.eternal.*;
import be.twofold.valen.game.eternal.reader.*;
import be.twofold.valen.game.eternal.resource.*;

import java.io.*;
import java.util.*;

public final class MapFileStaticInstancesReader implements ResourceReader<Scene> {
    private final EternalArchive archive;

    public MapFileStaticInstancesReader(EternalArchive archive) {
        this.archive = Check.notNull(archive, "archive");
    }

    @Override
    public boolean canRead(ResourceKey key) {
        return key.type() == ResourceType.StaticInstances;
    }

    @Override
    public Scene read(DataSource source, Asset asset) throws IOException {
        var staticInstances = MapFileStaticInstances.read(source);

        var instances = new ArrayList<Instance>();
        for (int i = 0; i < staticInstances.modelInstanceNames().size(); i++) {
            var geometry = staticInstances.modelInstanceGeometries().get(i);
            var instanceName = staticInstances.modelInstanceNames().get(i);
            var modelName = staticInstances.models().get(geometry.modelIndex());
            mapToInstance(modelName, instanceName, geometry)
                .ifPresent(instances::add);
        }
        return new Scene(instances);
    }

    private Optional<Instance> mapToInstance(String modelName, String instanceName, MapFileStaticInstancesModelGeometry geometry) {
        var resourceKey = ResourceKey.from(modelName, ResourceType.Model);
        if (!archive.exists(resourceKey)) {
            return Optional.empty();
        }

        var supplier = ThrowingSupplier.lazy(() -> archive.loadAsset(resourceKey, Model.class));
        var reference = new ModelReference(modelName, supplier);

        return Optional.of(new Instance(
            instanceName,
            reference,
            geometry.translation(),
            geometry.rotation().toRotation(),
            geometry.scale()
        ));
    }
}
