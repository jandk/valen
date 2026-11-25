package be.twofold.valen.game.eternal.reader.mapfilestaticinstances;

import wtf.reversed.toolbox.io.*;

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
    public static MapFileStaticInstances read(BinaryReader reader) throws IOException {
        reader.expectInt(1);

        var materials = reader.readStrings(reader.readInt() - 1, StringFormat.INT_LENGTH);
        var declRenderParams = reader.readStrings(reader.readInt() - 1, StringFormat.INT_LENGTH);
        var renderParams = reader.readStrings(reader.readInt(), StringFormat.INT_LENGTH);
        var models = reader.readStrings(reader.readInt(), StringFormat.INT_LENGTH);
        var materialGroups = reader.readObjects(reader.readInt(), MapFileStaticInstancesMaterialGroup::read);
        var group2 = reader.readObjects(reader.readInt(), MapFileStaticInstancesGroup2::read);
        var group3 = reader.readObjects(reader.readInt(), MapFileStaticInstancesGroup3::read);
        var declLayers = reader.readStrings(reader.readInt(), StringFormat.INT_LENGTH);

        var modelInstanceCount = reader.readInt();
        var modelInstanceNames = reader.readStrings(modelInstanceCount, StringFormat.INT_LENGTH);
        var modelInstanceGeometries = reader.readObjects(modelInstanceCount, MapFileStaticInstancesModelGeometry::read);
        var modelInstanceExtras = reader.readObjects(reader.readInt(), MapFileStaticInstancesModelExtra::read);

        var decalInstanceCount = reader.readInt();
        var decalInstanceNames = reader.readStrings(decalInstanceCount, StringFormat.INT_LENGTH);
        var decalInstanceGeometries = reader.readObjects(decalInstanceCount, MapFileStaticInstancesDeclGeometry::read);
        var decalInstanceExtras = reader.readObjects(reader.readInt(), MapFileStaticInstancesDeclExtra::read);

        var playerStarts = reader.readObjects(reader.readInt(), MapFileStaticInstancesPlayerStart::read);
        var layerStateChanges = reader.readObjects(reader.readInt(), MapFileStaticInstancesLayerStateChange::read);

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
