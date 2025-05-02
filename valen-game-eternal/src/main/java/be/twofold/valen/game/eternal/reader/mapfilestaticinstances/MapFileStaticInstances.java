package be.twofold.valen.game.eternal.reader.mapfilestaticinstances;

import be.twofold.valen.core.io.*;

import java.io.*;
import java.util.*;

public record MapFileStaticInstances(
    List<String> materials,
    List<String> declRenderParams,
    List<String> renderParams,
    List<String> models,
    List<MapFileStaticInstancesMaterialGroup> materialGroups,
    List<MapFileStaticInstancesGroup2> group2,
    List<MapFileStaticInstancesGroup3> group3,
    List<String> declLayers,
    List<String> modelInstanceNames,
    List<MapFileStaticInstancesModelGeometry> modelInstanceGeometries,
    List<MapFileStaticInstancesModelExtra> modelInstanceExtras,
    List<String> decalInstanceNames,
    List<MapFileStaticInstancesDeclGeometry> decalInstanceGeometries,
    List<MapFileStaticInstancesDeclExtra> decalInstanceExtras,
    List<MapFileStaticInstancesPlayerStart> playerStarts,
    List<MapFileStaticInstancesLayerStateChange> layerStateChanges
) {
    public static MapFileStaticInstances read(DataSource source) throws IOException {
        source.expectInt(1);

        var materials = source.readObjects(source.readInt() - 1, DataSource::readPString);
        var declRenderParams = source.readObjects(source.readInt() - 1, DataSource::readPString);
        var renderParams = source.readObjects(source.readInt(), DataSource::readPString);
        var models = source.readObjects(source.readInt(), DataSource::readPString);
        var materialGroups = source.readObjects(source.readInt(), MapFileStaticInstancesMaterialGroup::read);
        var group2 = source.readObjects(source.readInt(), MapFileStaticInstancesGroup2::read);
        var group3 = source.readObjects(source.readInt(), MapFileStaticInstancesGroup3::read);
        var declLayers = source.readObjects(source.readInt(), DataSource::readPString);

        var modelInstanceCount = source.readInt();
        var modelInstanceNames = source.readObjects(modelInstanceCount, DataSource::readPString);
        var modelInstanceGeometries = source.readObjects(modelInstanceCount, MapFileStaticInstancesModelGeometry::read);
        var modelInstanceExtras = source.readObjects(source.readInt(), MapFileStaticInstancesModelExtra::read);

        var decalInstanceCount = source.readInt();
        var decalInstanceNames = source.readObjects(decalInstanceCount, DataSource::readPString);
        var decalInstanceGeometries = source.readObjects(decalInstanceCount, MapFileStaticInstancesDeclGeometry::read);
        var decalInstanceExtras = source.readObjects(source.readInt(), MapFileStaticInstancesDeclExtra::read);

        var playerStarts = source.readObjects(source.readInt(), MapFileStaticInstancesPlayerStart::read);
        var layerStateChanges = source.readObjects(source.readInt(), MapFileStaticInstancesLayerStateChange::read);

        return new MapFileStaticInstances(
            materials,
            declRenderParams,
            renderParams,
            models,
            materialGroups,
            group2,
            group3,
            declLayers,
            modelInstanceNames,
            modelInstanceGeometries,
            modelInstanceExtras,
            decalInstanceNames,
            decalInstanceGeometries,
            decalInstanceExtras,
            playerStarts,
            layerStateChanges
        );
    }

    @Override
    public String toString() {
        return "StaticInstances(" +
            "materials=[" + materials.size() + " values], " +
            "declRenderParams=[" + declRenderParams.size() + " values], " +
            "renderParams=[" + renderParams.size() + " values], " +
            "models=[" + models.size() + " values], " +
            "materialGroups=[" + materialGroups.size() + " values], " +
            "group2=[" + group2.size() + " values], " +
            "group3=[" + group3.size() + " values], " +
            "declLayers=[" + declLayers.size() + " values], " +
            "modelInstanceNames=[" + modelInstanceNames.size() + " values], " +
            "modelInstanceGeometries=[" + modelInstanceGeometries.size() + " values], " +
            "modelInstanceExtras=[" + modelInstanceExtras.size() + " values], " +
            "decalInstanceNames=[" + decalInstanceNames.size() + " values], " +
            "decalInstanceGeometries=[" + decalInstanceGeometries.size() + " values], " +
            "decalInstanceExtras=[" + decalInstanceExtras.size() + " values], " +
            "playerStarts=[" + playerStarts.size() + " values], " +
            "layerStateChanges=[" + layerStateChanges.size() + " values]" +
            ")";
    }
}
