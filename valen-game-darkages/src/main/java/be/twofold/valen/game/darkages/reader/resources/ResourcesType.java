package be.twofold.valen.game.darkages.reader.resources;

import wtf.reversed.toolbox.util.*;

public enum ResourcesType implements ValueEnum<String> {
    AiNav("ainav"),
    Anim("anim"),
    AnimCompressProfileResource("animcompressprofileresource"),
    AsSampleSet("assampleset"),
    AutomapEntityCache("automapEntityCache"),
    BakeClothHash("bakeclothhash"),
    BaseModel("baseModel"),
    BinaryFile("binaryFile"),
    BinaryGoreContainer("binaryGoreContainer"),
    BinaryMd6def("binarymd6def"),
    Bink("bink"),
    CollisionShape("collisionShape"),
    ColorLUT("colorLUT"),
    CompositeSkelRegistry("compositeSkelRegistry"),
    DiscreteAnimation2("discreteanimation2"),
    EntityDef("entityDef"),
    File("file"),
    Font("font"),
    Font_glyph_ranges("font_glyph_ranges"),
    GeomCache("geomcache"),
    HavokCloth("havokcloth"),
    HkNavVolumeIp("hknavvolumeip"),
    Image("image"),
    IsGSnapshot("isgsnapshot"),
    LogicClass("logicClass"),
    LogicEntity("logicEntity"),
    LogicFX("logicFX"),
    LogicLibrary("logicLibrary"),
    LogicObjectDescriptor("logicObjectDescriptor"),
    LogicUIWidget("logicUIWidget"),
    MapBrushes("mapbrushes"),
    MapDevCheckpoints("mapdevcheckpoints"),
    MapEntities("mapentities"),
    MapEntityCollisionResources("mapentitycollisionresources"),
    MapFileSectorVolumesOverlap("mapfilesectorvolumesoverlap"),
    MapStaticStreamTree("mapstaticstreamtree"),
    Model("model"),
    ModelStream("modelstream"),
    OpacityMicroMapData("opacitymicromapdata"),
    PhysicsRagdollResource("physicsRagdollResource"),
    PhysicsWorldCollision("physicsworldcollision"),
    RenderProgDatabase("renderProgDatabase"),
    RenderProgDatabaseGlobalViewParms("renderProgDatabaseGlobalViewParms"),
    RenderProgDatabaseParmSet("renderProgDatabaseParmSet"),
    RenderProgDatabaseRTParms("renderProgDatabaseRTParms"),
    RenderProgResource("renderProgResource"),
    RsEmbSFile("rs_emb_sfile"),
    RsEmbSFileM("rs_emb_sfilem"),
    RsStreamFile("rs_streamfile"),
    RwKdTree("rwkdtree"),
    SectorRemeshModel("sectorremeshmodel"),
    SkelMap("skelMap"),
    Skeleton("skeleton"),
    SkeletonModifiedHash("skeletonmodifiedhash"),
    SlugFont("slug_font"),
    SndVolTree("sndvoltree"),
    StaticParticleModel("staticParticleModel"),
    StrandsHair("strandsHair"),
    StrandsHairCg("strandsHaircg"),
    Triton_acoustics("triton_acoustics"),
    Ui_shape_pool_data("ui_shape_pool_data"),
    Ui_type_data("ui_type_data"),
    Vegetation("vegetation"),
    ;

    private final String value;

    ResourcesType(String value) {
        this.value = value;
    }

    public static ResourcesType fromName(String name) {
        return ValueEnum.fromValue(ResourcesType.class, name);
    }

    @Override
    public String value() {
        return value;
    }
}
