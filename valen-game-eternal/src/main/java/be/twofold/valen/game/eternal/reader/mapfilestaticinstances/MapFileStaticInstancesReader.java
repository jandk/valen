package be.twofold.valen.game.eternal.reader.mapfilestaticinstances;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.scene.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.game.eternal.*;
import be.twofold.valen.game.eternal.resource.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.util.*;

public final class MapFileStaticInstancesReader implements AssetReader.Binary<Scene, EternalAsset> {
    @Override
    public boolean canRead(EternalAsset asset) {
        return asset.id().type() == ResourceType.StaticInstances;
    }

    @Override
    public Scene read(BinarySource source, EternalAsset asset, LoadingContext context) throws IOException {
        var staticInstances = MapFileStaticInstances.read(source);

        var instances = new ArrayList<Instance>();
        for (int i = 0; i < staticInstances.modelInstanceNames().size(); i++) {
            var geometry = staticInstances.modelInstanceGeometries().get(i);
            var instanceName = staticInstances.modelInstanceNames().get(i);
            var modelName = staticInstances.models().get(geometry.modelIndex());
            mapToInstance(modelName, instanceName, geometry, context)
                .ifPresent(instances::add);
        }
        return new Scene(instances);
    }

    private Optional<Instance> mapToInstance(String modelName, String instanceName, MapFileStaticInstancesModelGeometry geometry, LoadingContext context) {
        var resourceKey = EternalAssetID.from(modelName, ResourceType.Model);
        if (!context.exists(resourceKey)) {
            return Optional.empty();
        }

        var supplier = ThrowingSupplier.lazy(() -> context.load(resourceKey, Model.class));
        var reference = new ModelReference(modelName, modelName, supplier);

        return Optional.of(new Instance(
            reference,
            geometry.translation(),
            geometry.rotation().toRotation(),
            geometry.scale(),
            instanceName
        ));
    }
}
