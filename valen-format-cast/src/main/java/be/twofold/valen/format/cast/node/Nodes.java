package be.twofold.valen.format.cast.node;

import be.twofold.valen.format.cast.*;
import be.twofold.valen.format.cast.property.*;

import java.nio.*;
import java.util.*;
import java.util.concurrent.atomic.*;

public final class Nodes {
    public enum ConstraintType {
        SC,
        OR,
        PT,
        ;

        public static ConstraintType from(Object o) {
            return valueOf(o.toString().toUpperCase());
        }
    }

    public enum ColorSpace {
        LINEAR,
        SRGB,
        ;

        public static ColorSpace from(Object o) {
            return valueOf(o.toString().toUpperCase());
        }
    }

    public enum UpAxis {
        X,
        Y,
        Z,
        ;

        public static UpAxis from(Object o) {
            return valueOf(o.toString().toUpperCase());
        }
    }

    public enum KeyPropertyName {
        BS,
        TX,
        TY,
        SX,
        TZ,
        SY,
        SZ,
        VB,
        RQ,
        ;

        public static KeyPropertyName from(Object o) {
            return valueOf(o.toString().toUpperCase());
        }
    }

    public enum Mode {
        ABSOLUTE,
        ADDITIVE,
        RELATIVE,
        ;

        public static Mode from(Object o) {
            return valueOf(o.toString().toUpperCase());
        }
    }

    public enum Type {
        PBR,
        ;

        public static Type from(Object o) {
            return valueOf(o.toString().toUpperCase());
        }
    }

    public enum SkinningMethod {
        LINEAR,
        QUATERNION,
        ;

        public static SkinningMethod from(Object o) {
            return valueOf(o.toString().toUpperCase());
        }
    }

    public static final class RootNode extends CastNode {
        public RootNode(AtomicLong hasher) {
            super(CastNodeID.ROOT, hasher);
        }

        RootNode(long hash, Map<String, CastProperty> properties, List<CastNode> children) {
            super(CastNodeID.ROOT, hash, properties, children);
            // TODO: Validation
        }

        public List<ModelNode> getModelNodes() {
            return getChildrenOfType(ModelNode.class);
        }

        public ModelNode createModelNode() {
            return createChild(new ModelNode(hasher));
        }

        public List<AnimationNode> getAnimationNodes() {
            return getChildrenOfType(AnimationNode.class);
        }

        public AnimationNode createAnimationNode() {
            return createChild(new AnimationNode(hasher));
        }

        public List<InstanceNode> getInstanceNodes() {
            return getChildrenOfType(InstanceNode.class);
        }

        public InstanceNode createInstanceNode() {
            return createChild(new InstanceNode(hasher));
        }

        public List<MetadataNode> getMetadataNodes() {
            return getChildrenOfType(MetadataNode.class);
        }

        public MetadataNode createMetadataNode() {
            return createChild(new MetadataNode(hasher));
        }

    }


    public static final class ModelNode extends CastNode {
        ModelNode(AtomicLong hasher) {
            super(CastNodeID.MODEL, hasher);
        }

        ModelNode(long hash, Map<String, CastProperty> properties, List<CastNode> children) {
            super(CastNodeID.MODEL, hash, properties, children);
            // TODO: Validation
        }

        public Optional<SkeletonNode> getSkeletonNodes() {
            return getChildOfType(SkeletonNode.class);
        }

        public SkeletonNode createSkeletonNode() {
            return createChild(new SkeletonNode(hasher));
        }

        public List<MeshNode> getMeshNodes() {
            return getChildrenOfType(MeshNode.class);
        }

        public MeshNode createMeshNode() {
            return createChild(new MeshNode(hasher));
        }

        public List<HairNode> getHairNodes() {
            return getChildrenOfType(HairNode.class);
        }

        public HairNode createHairNode() {
            return createChild(new HairNode(hasher));
        }

        public List<BlendShapeNode> getBlendShapeNodes() {
            return getChildrenOfType(BlendShapeNode.class);
        }

        public BlendShapeNode createBlendShapeNode() {
            return createChild(new BlendShapeNode(hasher));
        }

        public List<MaterialNode> getMaterialNodes() {
            return getChildrenOfType(MaterialNode.class);
        }

        public MaterialNode createMaterialNode() {
            return createChild(new MaterialNode(hasher));
        }

        public Optional<String> getName() {
            return getProperty("n", String.class::cast);
        }

        public ModelNode setName(String name) {
            createProperty(CastPropertyID.STRING, "n", name);
            return this;
        }

        public Optional<Vec3> getPosition() {
            return getProperty("p", Vec3.class::cast);
        }

        public ModelNode setPosition(Vec3 position) {
            createProperty(CastPropertyID.VECTOR3, "p", position);
            return this;
        }

        public Optional<Vec4> getRotation() {
            return getProperty("r", Vec4.class::cast);
        }

        public ModelNode setRotation(Vec4 rotation) {
            createProperty(CastPropertyID.VECTOR4, "r", rotation);
            return this;
        }

        public Optional<Vec3> getScale() {
            return getProperty("s", Vec3.class::cast);
        }

        public ModelNode setScale(Vec3 scale) {
            createProperty(CastPropertyID.VECTOR3, "s", scale);
            return this;
        }

    }


    public static final class MeshNode extends CastNode {
        private int vertexColorBufferIndex;
        private int vertexUVBufferIndex;

        MeshNode(AtomicLong hasher) {
            super(CastNodeID.MESH, hasher);
        }

        MeshNode(long hash, Map<String, CastProperty> properties, List<CastNode> children) {
            super(CastNodeID.MESH, hash, properties, children);
            // TODO: Validation
        }

        public Optional<String> getName() {
            return getProperty("n", String.class::cast);
        }

        public MeshNode setName(String name) {
            createProperty(CastPropertyID.STRING, "n", name);
            return this;
        }

        public FloatBuffer getVertexPositionBuffer() {
            return getProperty("vp", FloatBuffer.class::cast).orElseThrow();
        }

        public MeshNode setVertexPositionBuffer(FloatBuffer vertexPositionBuffer) {
            createProperty(CastPropertyID.VECTOR3, "vp", vertexPositionBuffer);
            return this;
        }

        public Optional<FloatBuffer> getVertexNormalBuffer() {
            return getProperty("vn", FloatBuffer.class::cast);
        }

        public MeshNode setVertexNormalBuffer(FloatBuffer vertexNormalBuffer) {
            createProperty(CastPropertyID.VECTOR3, "vn", vertexNormalBuffer);
            return this;
        }

        public Optional<FloatBuffer> getVertexTangentBuffer() {
            return getProperty("vt", FloatBuffer.class::cast);
        }

        public MeshNode setVertexTangentBuffer(FloatBuffer vertexTangentBuffer) {
            createProperty(CastPropertyID.VECTOR3, "vt", vertexTangentBuffer);
            return this;
        }

        public Optional<Buffer> getVertexColorBuffer(int index) {
            return getProperty("c" + index, Buffer.class::cast);
        }

        public MeshNode addVertexColorBuffer(Buffer vertexColorBuffer) {
            if (vertexColorBuffer instanceof IntBuffer) {
                createProperty(CastPropertyID.INT, "c" + vertexColorBufferIndex++, vertexColorBuffer);
            } else if (vertexColorBuffer instanceof FloatBuffer) {
                createProperty(CastPropertyID.VECTOR4, "c" + vertexColorBufferIndex++, vertexColorBuffer);
            } else {
                throw new IllegalArgumentException("Invalid type for property vertexColorBuffer");
            }
            return this;
        }

        public Optional<FloatBuffer> getVertexUVBuffer(int index) {
            return getProperty("u" + index, FloatBuffer.class::cast);
        }

        public MeshNode addVertexUVBuffer(FloatBuffer vertexUVBuffer) {
            createProperty(CastPropertyID.VECTOR2, "u" + vertexUVBufferIndex++, vertexUVBuffer);
            return this;
        }

        public Optional<Buffer> getVertexWeightBoneBuffer() {
            return getProperty("wb", Buffer.class::cast);
        }

        public MeshNode setVertexWeightBoneBuffer(Buffer vertexWeightBoneBuffer) {
            createIntBufferProperty("wb", vertexWeightBoneBuffer);
            return this;
        }

        public Optional<FloatBuffer> getVertexWeightValueBuffer() {
            return getProperty("wv", FloatBuffer.class::cast);
        }

        public MeshNode setVertexWeightValueBuffer(FloatBuffer vertexWeightValueBuffer) {
            createProperty(CastPropertyID.FLOAT, "wv", vertexWeightValueBuffer);
            return this;
        }

        public Buffer getFaceBuffer() {
            return getProperty("f", Buffer.class::cast).orElseThrow();
        }

        public MeshNode setFaceBuffer(Buffer faceBuffer) {
            createIntBufferProperty("f", faceBuffer);
            return this;
        }

        public Optional<Integer> getColorLayerCount() {
            return getProperty("cl", Integer.class::cast);
        }

        public MeshNode setColorLayerCount(Integer colorLayerCount) {
            createIntProperty("cl", colorLayerCount);
            return this;
        }

        public Optional<Integer> getUVLayerCount() {
            return getProperty("ul", Integer.class::cast);
        }

        public MeshNode setUVLayerCount(Integer uVLayerCount) {
            createIntProperty("ul", uVLayerCount);
            return this;
        }

        public Optional<Integer> getMaximumWeightInfluence() {
            return getProperty("mi", Integer.class::cast);
        }

        public MeshNode setMaximumWeightInfluence(Integer maximumWeightInfluence) {
            createIntProperty("mi", maximumWeightInfluence);
            return this;
        }

        public Optional<SkinningMethod> getSkinningMethod() {
            return getProperty("sm", SkinningMethod::from);
        }

        public MeshNode setSkinningMethod(SkinningMethod skinningMethod) {
            createProperty(CastPropertyID.STRING, "sm", skinningMethod.toString().toLowerCase());
            return this;
        }

        public Optional<Long> getMaterial() {
            return getProperty("m", Long.class::cast);
        }

        public MeshNode setMaterial(Long material) {
            createProperty(CastPropertyID.LONG, "m", material);
            return this;
        }

    }


    public static final class HairNode extends CastNode {
        HairNode(AtomicLong hasher) {
            super(CastNodeID.HAIR, hasher);
        }

        HairNode(long hash, Map<String, CastProperty> properties, List<CastNode> children) {
            super(CastNodeID.HAIR, hash, properties, children);
            // TODO: Validation
        }

        public Optional<String> getName() {
            return getProperty("n", String.class::cast);
        }

        public HairNode setName(String name) {
            createProperty(CastPropertyID.STRING, "n", name);
            return this;
        }

        public Buffer getSegmentsBuffer() {
            return getProperty("se", Buffer.class::cast).orElseThrow();
        }

        public HairNode setSegmentsBuffer(Buffer segmentsBuffer) {
            createIntBufferProperty("se", segmentsBuffer);
            return this;
        }

        public FloatBuffer getParticleBuffer() {
            return getProperty("pt", FloatBuffer.class::cast).orElseThrow();
        }

        public HairNode setParticleBuffer(FloatBuffer particleBuffer) {
            createProperty(CastPropertyID.VECTOR3, "pt", particleBuffer);
            return this;
        }

        public Optional<Long> getMaterial() {
            return getProperty("m", Long.class::cast);
        }

        public HairNode setMaterial(Long material) {
            createProperty(CastPropertyID.LONG, "m", material);
            return this;
        }

    }


    public static final class BlendShapeNode extends CastNode {
        BlendShapeNode(AtomicLong hasher) {
            super(CastNodeID.BLEND_SHAPE, hasher);
        }

        BlendShapeNode(long hash, Map<String, CastProperty> properties, List<CastNode> children) {
            super(CastNodeID.BLEND_SHAPE, hash, properties, children);
            // TODO: Validation
        }

        public String getName() {
            return getProperty("n", String.class::cast).orElseThrow();
        }

        public BlendShapeNode setName(String name) {
            createProperty(CastPropertyID.STRING, "n", name);
            return this;
        }

        public long getBaseShape() {
            return getProperty("b", Long.class::cast).orElseThrow();
        }

        public BlendShapeNode setBaseShape(Long baseShape) {
            createProperty(CastPropertyID.LONG, "b", baseShape);
            return this;
        }

        public Buffer getTargetShapeVertexIndices() {
            return getProperty("vi", Buffer.class::cast).orElseThrow();
        }

        public BlendShapeNode setTargetShapeVertexIndices(Buffer targetShapeVertexIndices) {
            createIntBufferProperty("vi", targetShapeVertexIndices);
            return this;
        }

        public FloatBuffer getTargetShapeVertexPositions() {
            return getProperty("vp", FloatBuffer.class::cast).orElseThrow();
        }

        public BlendShapeNode setTargetShapeVertexPositions(FloatBuffer targetShapeVertexPositions) {
            createProperty(CastPropertyID.VECTOR3, "vp", targetShapeVertexPositions);
            return this;
        }

        public Optional<FloatBuffer> getTargetWeightScale() {
            return getProperty("ts", FloatBuffer.class::cast);
        }

        public BlendShapeNode setTargetWeightScale(FloatBuffer targetWeightScale) {
            createProperty(CastPropertyID.FLOAT, "ts", targetWeightScale);
            return this;
        }

    }


    public static final class SkeletonNode extends CastNode {
        SkeletonNode(AtomicLong hasher) {
            super(CastNodeID.SKELETON, hasher);
        }

        SkeletonNode(long hash, Map<String, CastProperty> properties, List<CastNode> children) {
            super(CastNodeID.SKELETON, hash, properties, children);
            // TODO: Validation
        }

        public List<BoneNode> getBoneNodes() {
            return getChildrenOfType(BoneNode.class);
        }

        public BoneNode createBoneNode() {
            return createChild(new BoneNode(hasher));
        }

        public List<IkHandleNode> getIkHandleNodes() {
            return getChildrenOfType(IkHandleNode.class);
        }

        public IkHandleNode createIkHandleNode() {
            return createChild(new IkHandleNode(hasher));
        }

        public List<ConstraintNode> getConstraintNodes() {
            return getChildrenOfType(ConstraintNode.class);
        }

        public ConstraintNode createConstraintNode() {
            return createChild(new ConstraintNode(hasher));
        }

    }


    public static final class BoneNode extends CastNode {
        BoneNode(AtomicLong hasher) {
            super(CastNodeID.BONE, hasher);
        }

        BoneNode(long hash, Map<String, CastProperty> properties, List<CastNode> children) {
            super(CastNodeID.BONE, hash, properties, children);
            // TODO: Validation
        }

        public String getName() {
            return getProperty("n", String.class::cast).orElseThrow();
        }

        public BoneNode setName(String name) {
            createProperty(CastPropertyID.STRING, "n", name);
            return this;
        }

        public Optional<Integer> getParentIndex() {
            return getProperty("p", Integer.class::cast);
        }

        public BoneNode setParentIndex(Integer parentIndex) {
            createProperty(CastPropertyID.INT, "p", parentIndex);
            return this;
        }

        public Optional<Boolean> getSegmentScaleCompensate() {
            return getProperty("ssc", this::parseBoolean);
        }

        public BoneNode setSegmentScaleCompensate(Boolean segmentScaleCompensate) {
            createProperty(CastPropertyID.BYTE, "ssc", segmentScaleCompensate ? 1 : 0);
            return this;
        }

        public Optional<Vec3> getLocalPosition() {
            return getProperty("lp", Vec3.class::cast);
        }

        public BoneNode setLocalPosition(Vec3 localPosition) {
            createProperty(CastPropertyID.VECTOR3, "lp", localPosition);
            return this;
        }

        public Optional<Vec4> getLocalRotation() {
            return getProperty("lr", Vec4.class::cast);
        }

        public BoneNode setLocalRotation(Vec4 localRotation) {
            createProperty(CastPropertyID.VECTOR4, "lr", localRotation);
            return this;
        }

        public Optional<Vec3> getWorldPosition() {
            return getProperty("wp", Vec3.class::cast);
        }

        public BoneNode setWorldPosition(Vec3 worldPosition) {
            createProperty(CastPropertyID.VECTOR3, "wp", worldPosition);
            return this;
        }

        public Optional<Vec4> getWorldRotation() {
            return getProperty("wr", Vec4.class::cast);
        }

        public BoneNode setWorldRotation(Vec4 worldRotation) {
            createProperty(CastPropertyID.VECTOR4, "wr", worldRotation);
            return this;
        }

        public Optional<Vec3> getScale() {
            return getProperty("s", Vec3.class::cast);
        }

        public BoneNode setScale(Vec3 scale) {
            createProperty(CastPropertyID.VECTOR3, "s", scale);
            return this;
        }

    }


    public static final class IkHandleNode extends CastNode {
        IkHandleNode(AtomicLong hasher) {
            super(CastNodeID.IK_HANDLE, hasher);
        }

        IkHandleNode(long hash, Map<String, CastProperty> properties, List<CastNode> children) {
            super(CastNodeID.IK_HANDLE, hash, properties, children);
            // TODO: Validation
        }

        public Optional<String> getName() {
            return getProperty("n", String.class::cast);
        }

        public IkHandleNode setName(String name) {
            createProperty(CastPropertyID.STRING, "n", name);
            return this;
        }

        public long getStartBoneHash() {
            return getProperty("sb", Long.class::cast).orElseThrow();
        }

        public IkHandleNode setStartBoneHash(Long startBoneHash) {
            createProperty(CastPropertyID.LONG, "sb", startBoneHash);
            return this;
        }

        public long getEndBoneHash() {
            return getProperty("eb", Long.class::cast).orElseThrow();
        }

        public IkHandleNode setEndBoneHash(Long endBoneHash) {
            createProperty(CastPropertyID.LONG, "eb", endBoneHash);
            return this;
        }

        public Optional<Long> getTargetBoneHash() {
            return getProperty("tb", Long.class::cast);
        }

        public IkHandleNode setTargetBoneHash(Long targetBoneHash) {
            createProperty(CastPropertyID.LONG, "tb", targetBoneHash);
            return this;
        }

        public Optional<Long> getPoleVectorBoneHash() {
            return getProperty("pv", Long.class::cast);
        }

        public IkHandleNode setPoleVectorBoneHash(Long poleVectorBoneHash) {
            createProperty(CastPropertyID.LONG, "pv", poleVectorBoneHash);
            return this;
        }

        public Optional<Long> getPoleBoneHash() {
            return getProperty("pb", Long.class::cast);
        }

        public IkHandleNode setPoleBoneHash(Long poleBoneHash) {
            createProperty(CastPropertyID.LONG, "pb", poleBoneHash);
            return this;
        }

        public Optional<Boolean> getUseTargetRotation() {
            return getProperty("tr", this::parseBoolean);
        }

        public IkHandleNode setUseTargetRotation(Boolean useTargetRotation) {
            createProperty(CastPropertyID.BYTE, "tr", useTargetRotation ? 1 : 0);
            return this;
        }

    }


    public static final class ConstraintNode extends CastNode {
        ConstraintNode(AtomicLong hasher) {
            super(CastNodeID.CONSTRAINT, hasher);
        }

        ConstraintNode(long hash, Map<String, CastProperty> properties, List<CastNode> children) {
            super(CastNodeID.CONSTRAINT, hash, properties, children);
            // TODO: Validation
        }

        public Optional<String> getName() {
            return getProperty("n", String.class::cast);
        }

        public ConstraintNode setName(String name) {
            createProperty(CastPropertyID.STRING, "n", name);
            return this;
        }

        public ConstraintType getConstraintType() {
            return getProperty("ct", ConstraintType::from).orElseThrow();
        }

        public ConstraintNode setConstraintType(ConstraintType constraintType) {
            createProperty(CastPropertyID.STRING, "ct", constraintType.toString().toLowerCase());
            return this;
        }

        public long getConstraintBoneHash() {
            return getProperty("cb", Long.class::cast).orElseThrow();
        }

        public ConstraintNode setConstraintBoneHash(Long constraintBoneHash) {
            createProperty(CastPropertyID.LONG, "cb", constraintBoneHash);
            return this;
        }

        public long getTargetBoneHash() {
            return getProperty("tb", Long.class::cast).orElseThrow();
        }

        public ConstraintNode setTargetBoneHash(Long targetBoneHash) {
            createProperty(CastPropertyID.LONG, "tb", targetBoneHash);
            return this;
        }

        public Optional<Boolean> getMaintainOffset() {
            return getProperty("mo", this::parseBoolean);
        }

        public ConstraintNode setMaintainOffset(Boolean maintainOffset) {
            createProperty(CastPropertyID.BYTE, "mo", maintainOffset ? 1 : 0);
            return this;
        }

        public Optional<Object> getCustomOffset() {
            return getProperty("co", Object.class::cast);
        }

        public ConstraintNode setCustomOffset(Object customOffset) {
            if (customOffset instanceof Vec3) {
                createProperty(CastPropertyID.VECTOR3, "co", customOffset);
            } else if (customOffset instanceof Vec4) {
                createProperty(CastPropertyID.VECTOR4, "co", customOffset);
            } else {
                throw new IllegalArgumentException("Invalid type for property customOffset");
            }
            return this;
        }

        public Optional<Float> getWeight() {
            return getProperty("wt", Float.class::cast);
        }

        public ConstraintNode setWeight(Float weight) {
            createProperty(CastPropertyID.FLOAT, "wt", weight);
            return this;
        }

        public Optional<Boolean> getSkipX() {
            return getProperty("sx", this::parseBoolean);
        }

        public ConstraintNode setSkipX(Boolean skipX) {
            createProperty(CastPropertyID.BYTE, "sx", skipX ? 1 : 0);
            return this;
        }

        public Optional<Boolean> getSkipY() {
            return getProperty("sy", this::parseBoolean);
        }

        public ConstraintNode setSkipY(Boolean skipY) {
            createProperty(CastPropertyID.BYTE, "sy", skipY ? 1 : 0);
            return this;
        }

        public Optional<Boolean> getSkipZ() {
            return getProperty("sz", this::parseBoolean);
        }

        public ConstraintNode setSkipZ(Boolean skipZ) {
            createProperty(CastPropertyID.BYTE, "sz", skipZ ? 1 : 0);
            return this;
        }

    }


    public static final class AnimationNode extends CastNode {
        AnimationNode(AtomicLong hasher) {
            super(CastNodeID.ANIMATION, hasher);
        }

        AnimationNode(long hash, Map<String, CastProperty> properties, List<CastNode> children) {
            super(CastNodeID.ANIMATION, hash, properties, children);
            // TODO: Validation
        }

        public Optional<SkeletonNode> getSkeletonNodes() {
            return getChildOfType(SkeletonNode.class);
        }

        public SkeletonNode createSkeletonNode() {
            return createChild(new SkeletonNode(hasher));
        }

        public List<CurveNode> getCurveNodes() {
            return getChildrenOfType(CurveNode.class);
        }

        public CurveNode createCurveNode() {
            return createChild(new CurveNode(hasher));
        }

        public List<CurveModeOverrideNode> getCurveModeOverrideNodes() {
            return getChildrenOfType(CurveModeOverrideNode.class);
        }

        public CurveModeOverrideNode createCurveModeOverrideNode() {
            return createChild(new CurveModeOverrideNode(hasher));
        }

        public List<NotificationTrackNode> getNotificationTrackNodes() {
            return getChildrenOfType(NotificationTrackNode.class);
        }

        public NotificationTrackNode createNotificationTrackNode() {
            return createChild(new NotificationTrackNode(hasher));
        }

        public Optional<String> getName() {
            return getProperty("n", String.class::cast);
        }

        public AnimationNode setName(String name) {
            createProperty(CastPropertyID.STRING, "n", name);
            return this;
        }

        public float getFramerate() {
            return getProperty("fr", Float.class::cast).orElseThrow();
        }

        public AnimationNode setFramerate(Float framerate) {
            createProperty(CastPropertyID.FLOAT, "fr", framerate);
            return this;
        }

        public Optional<Boolean> getLooping() {
            return getProperty("lo", this::parseBoolean);
        }

        public AnimationNode setLooping(Boolean looping) {
            createProperty(CastPropertyID.BYTE, "lo", looping ? 1 : 0);
            return this;
        }

    }


    public static final class CurveNode extends CastNode {
        CurveNode(AtomicLong hasher) {
            super(CastNodeID.CURVE, hasher);
        }

        CurveNode(long hash, Map<String, CastProperty> properties, List<CastNode> children) {
            super(CastNodeID.CURVE, hash, properties, children);
            // TODO: Validation
        }

        public String getNodeName() {
            return getProperty("nn", String.class::cast).orElseThrow();
        }

        public CurveNode setNodeName(String nodeName) {
            createProperty(CastPropertyID.STRING, "nn", nodeName);
            return this;
        }

        public KeyPropertyName getKeyPropertyName() {
            return getProperty("kp", KeyPropertyName::from).orElseThrow();
        }

        public CurveNode setKeyPropertyName(KeyPropertyName keyPropertyName) {
            createProperty(CastPropertyID.STRING, "kp", keyPropertyName.toString().toLowerCase());
            return this;
        }

        public Buffer getKeyFrameBuffer() {
            return getProperty("kb", Buffer.class::cast).orElseThrow();
        }

        public CurveNode setKeyFrameBuffer(Buffer keyFrameBuffer) {
            createIntBufferProperty("kb", keyFrameBuffer);
            return this;
        }

        public Buffer getKeyValueBuffer() {
            return getProperty("kv", Buffer.class::cast).orElseThrow();
        }

        public CurveNode setKeyValueBuffer(Buffer keyValueBuffer) {
            if (keyValueBuffer instanceof ByteBuffer) {
                createProperty(CastPropertyID.BYTE, "kv", keyValueBuffer);
            } else if (keyValueBuffer instanceof ShortBuffer) {
                createProperty(CastPropertyID.SHORT, "kv", keyValueBuffer);
            } else if (keyValueBuffer instanceof IntBuffer) {
                createProperty(CastPropertyID.INT, "kv", keyValueBuffer);
            } else if (keyValueBuffer instanceof FloatBuffer) {
                createProperty(CastPropertyID.FLOAT, "kv", keyValueBuffer);
            } else if (keyValueBuffer instanceof FloatBuffer) {
                createProperty(CastPropertyID.VECTOR4, "kv", keyValueBuffer);
            } else {
                throw new IllegalArgumentException("Invalid type for property keyValueBuffer");
            }
            return this;
        }

        public Mode getMode() {
            return getProperty("m", Mode::from).orElseThrow();
        }

        public CurveNode setMode(Mode mode) {
            createProperty(CastPropertyID.STRING, "m", mode.toString().toLowerCase());
            return this;
        }

        public Optional<Float> getAdditiveBlendWeight() {
            return getProperty("ab", Float.class::cast);
        }

        public CurveNode setAdditiveBlendWeight(Float additiveBlendWeight) {
            createProperty(CastPropertyID.FLOAT, "ab", additiveBlendWeight);
            return this;
        }

    }


    public static final class CurveModeOverrideNode extends CastNode {
        CurveModeOverrideNode(AtomicLong hasher) {
            super(CastNodeID.CURVE_MODE_OVERRIDE, hasher);
        }

        CurveModeOverrideNode(long hash, Map<String, CastProperty> properties, List<CastNode> children) {
            super(CastNodeID.CURVE_MODE_OVERRIDE, hash, properties, children);
            // TODO: Validation
        }

        public String getNodeName() {
            return getProperty("nn", String.class::cast).orElseThrow();
        }

        public CurveModeOverrideNode setNodeName(String nodeName) {
            createProperty(CastPropertyID.STRING, "nn", nodeName);
            return this;
        }

        public Mode getMode() {
            return getProperty("m", Mode::from).orElseThrow();
        }

        public CurveModeOverrideNode setMode(Mode mode) {
            createProperty(CastPropertyID.STRING, "m", mode.toString().toLowerCase());
            return this;
        }

        public Optional<Boolean> getOverrideTranslationCurves() {
            return getProperty("ot", this::parseBoolean);
        }

        public CurveModeOverrideNode setOverrideTranslationCurves(Boolean overrideTranslationCurves) {
            createProperty(CastPropertyID.BYTE, "ot", overrideTranslationCurves ? 1 : 0);
            return this;
        }

        public Optional<Boolean> getOverrideRotationCurves() {
            return getProperty("or", this::parseBoolean);
        }

        public CurveModeOverrideNode setOverrideRotationCurves(Boolean overrideRotationCurves) {
            createProperty(CastPropertyID.BYTE, "or", overrideRotationCurves ? 1 : 0);
            return this;
        }

        public Optional<Boolean> getOverrideScaleCurves() {
            return getProperty("os", this::parseBoolean);
        }

        public CurveModeOverrideNode setOverrideScaleCurves(Boolean overrideScaleCurves) {
            createProperty(CastPropertyID.BYTE, "os", overrideScaleCurves ? 1 : 0);
            return this;
        }

    }


    public static final class NotificationTrackNode extends CastNode {
        NotificationTrackNode(AtomicLong hasher) {
            super(CastNodeID.NOTIFICATION_TRACK, hasher);
        }

        NotificationTrackNode(long hash, Map<String, CastProperty> properties, List<CastNode> children) {
            super(CastNodeID.NOTIFICATION_TRACK, hash, properties, children);
            // TODO: Validation
        }

        public String getName() {
            return getProperty("n", String.class::cast).orElseThrow();
        }

        public NotificationTrackNode setName(String name) {
            createProperty(CastPropertyID.STRING, "n", name);
            return this;
        }

        public Buffer getKeyFrameBuffer() {
            return getProperty("kb", Buffer.class::cast).orElseThrow();
        }

        public NotificationTrackNode setKeyFrameBuffer(Buffer keyFrameBuffer) {
            createIntBufferProperty("kb", keyFrameBuffer);
            return this;
        }

    }


    public static final class MaterialNode extends CastNode {
        private int extraIndex;

        MaterialNode(AtomicLong hasher) {
            super(CastNodeID.MATERIAL, hasher);
        }

        MaterialNode(long hash, Map<String, CastProperty> properties, List<CastNode> children) {
            super(CastNodeID.MATERIAL, hash, properties, children);
            // TODO: Validation
        }

        public List<FileNode> getFileNodes() {
            return getChildrenOfType(FileNode.class);
        }

        public FileNode createFileNode() {
            return createChild(new FileNode(hasher));
        }

        public List<ColorNode> getColorNodes() {
            return getChildrenOfType(ColorNode.class);
        }

        public ColorNode createColorNode() {
            return createChild(new ColorNode(hasher));
        }

        public String getName() {
            return getProperty("n", String.class::cast).orElseThrow();
        }

        public MaterialNode setName(String name) {
            createProperty(CastPropertyID.STRING, "n", name);
            return this;
        }

        public Type getType() {
            return getProperty("t", Type::from).orElseThrow();
        }

        public MaterialNode setType(Type type) {
            createProperty(CastPropertyID.STRING, "t", type.toString().toLowerCase());
            return this;
        }

        public Optional<Long> getAlbedoHash() {
            return getProperty("albedo", Long.class::cast);
        }

        public MaterialNode setAlbedoHash(Long albedoHash) {
            createProperty(CastPropertyID.LONG, "albedo", albedoHash);
            return this;
        }

        public Optional<Long> getDiffuseHash() {
            return getProperty("diffuse", Long.class::cast);
        }

        public MaterialNode setDiffuseHash(Long diffuseHash) {
            createProperty(CastPropertyID.LONG, "diffuse", diffuseHash);
            return this;
        }

        public Optional<Long> getNormalHash() {
            return getProperty("normal", Long.class::cast);
        }

        public MaterialNode setNormalHash(Long normalHash) {
            createProperty(CastPropertyID.LONG, "normal", normalHash);
            return this;
        }

        public Optional<Long> getSpecularHash() {
            return getProperty("specular", Long.class::cast);
        }

        public MaterialNode setSpecularHash(Long specularHash) {
            createProperty(CastPropertyID.LONG, "specular", specularHash);
            return this;
        }

        public Optional<Long> getGlossHash() {
            return getProperty("gloss", Long.class::cast);
        }

        public MaterialNode setGlossHash(Long glossHash) {
            createProperty(CastPropertyID.LONG, "gloss", glossHash);
            return this;
        }

        public Optional<Long> getRoughnessHash() {
            return getProperty("roughness", Long.class::cast);
        }

        public MaterialNode setRoughnessHash(Long roughnessHash) {
            createProperty(CastPropertyID.LONG, "roughness", roughnessHash);
            return this;
        }

        public Optional<Long> getEmissiveHash() {
            return getProperty("emissive", Long.class::cast);
        }

        public MaterialNode setEmissiveHash(Long emissiveHash) {
            createProperty(CastPropertyID.LONG, "emissive", emissiveHash);
            return this;
        }

        public Optional<Long> getEmissiveMaskHash() {
            return getProperty("emask", Long.class::cast);
        }

        public MaterialNode setEmissiveMaskHash(Long emissiveMaskHash) {
            createProperty(CastPropertyID.LONG, "emask", emissiveMaskHash);
            return this;
        }

        public Optional<Long> getAmbientOcclusionHash() {
            return getProperty("ao", Long.class::cast);
        }

        public MaterialNode setAmbientOcclusionHash(Long ambientOcclusionHash) {
            createProperty(CastPropertyID.LONG, "ao", ambientOcclusionHash);
            return this;
        }

        public Optional<Long> getCavityHash() {
            return getProperty("cavity", Long.class::cast);
        }

        public MaterialNode setCavityHash(Long cavityHash) {
            createProperty(CastPropertyID.LONG, "cavity", cavityHash);
            return this;
        }

        public Optional<Long> getAnisotropyHash() {
            return getProperty("aniso", Long.class::cast);
        }

        public MaterialNode setAnisotropyHash(Long anisotropyHash) {
            createProperty(CastPropertyID.LONG, "aniso", anisotropyHash);
            return this;
        }

        public Optional<Long> getExtra(int index) {
            return getProperty("extra" + index, Long.class::cast);
        }

        public MaterialNode addExtra(Long extra) {
            createProperty(CastPropertyID.LONG, "extra" + extraIndex++, extra);
            return this;
        }

    }


    public static final class FileNode extends CastNode {
        FileNode(AtomicLong hasher) {
            super(CastNodeID.FILE, hasher);
        }

        FileNode(long hash, Map<String, CastProperty> properties, List<CastNode> children) {
            super(CastNodeID.FILE, hash, properties, children);
            // TODO: Validation
        }

        public String getPath() {
            return getProperty("p", String.class::cast).orElseThrow();
        }

        public FileNode setPath(String path) {
            createProperty(CastPropertyID.STRING, "p", path);
            return this;
        }

    }


    public static final class ColorNode extends CastNode {
        ColorNode(AtomicLong hasher) {
            super(CastNodeID.COLOR, hasher);
        }

        ColorNode(long hash, Map<String, CastProperty> properties, List<CastNode> children) {
            super(CastNodeID.COLOR, hash, properties, children);
            // TODO: Validation
        }

        public Optional<String> getName() {
            return getProperty("n", String.class::cast);
        }

        public ColorNode setName(String name) {
            createProperty(CastPropertyID.STRING, "n", name);
            return this;
        }

        public Optional<ColorSpace> getColorSpace() {
            return getProperty("cs", ColorSpace::from);
        }

        public ColorNode setColorSpace(ColorSpace colorSpace) {
            createProperty(CastPropertyID.STRING, "cs", colorSpace.toString().toLowerCase());
            return this;
        }

        public Vec4 getRgbaColor() {
            return getProperty("rgba", Vec4.class::cast).orElseThrow();
        }

        public ColorNode setRgbaColor(Vec4 rgbaColor) {
            createProperty(CastPropertyID.VECTOR4, "rgba", rgbaColor);
            return this;
        }

    }


    public static final class InstanceNode extends CastNode {
        InstanceNode(AtomicLong hasher) {
            super(CastNodeID.INSTANCE, hasher);
        }

        InstanceNode(long hash, Map<String, CastProperty> properties, List<CastNode> children) {
            super(CastNodeID.INSTANCE, hash, properties, children);
            // TODO: Validation
        }

        public List<FileNode> getFileNodes() {
            return getChildrenOfType(FileNode.class);
        }

        public FileNode createFileNode() {
            return createChild(new FileNode(hasher));
        }

        public Optional<String> getName() {
            return getProperty("n", String.class::cast);
        }

        public InstanceNode setName(String name) {
            createProperty(CastPropertyID.STRING, "n", name);
            return this;
        }

        public long getReferenceFile() {
            return getProperty("rf", Long.class::cast).orElseThrow();
        }

        public InstanceNode setReferenceFile(Long referenceFile) {
            createProperty(CastPropertyID.LONG, "rf", referenceFile);
            return this;
        }

        public Vec3 getPosition() {
            return getProperty("p", Vec3.class::cast).orElseThrow();
        }

        public InstanceNode setPosition(Vec3 position) {
            createProperty(CastPropertyID.VECTOR3, "p", position);
            return this;
        }

        public Vec4 getRotation() {
            return getProperty("r", Vec4.class::cast).orElseThrow();
        }

        public InstanceNode setRotation(Vec4 rotation) {
            createProperty(CastPropertyID.VECTOR4, "r", rotation);
            return this;
        }

        public Vec3 getScale() {
            return getProperty("s", Vec3.class::cast).orElseThrow();
        }

        public InstanceNode setScale(Vec3 scale) {
            createProperty(CastPropertyID.VECTOR3, "s", scale);
            return this;
        }

    }


    public static final class MetadataNode extends CastNode {
        MetadataNode(AtomicLong hasher) {
            super(CastNodeID.METADATA, hasher);
        }

        MetadataNode(long hash, Map<String, CastProperty> properties, List<CastNode> children) {
            super(CastNodeID.METADATA, hash, properties, children);
            // TODO: Validation
        }

        public Optional<String> getAuthor() {
            return getProperty("a", String.class::cast);
        }

        public MetadataNode setAuthor(String author) {
            createProperty(CastPropertyID.STRING, "a", author);
            return this;
        }

        public Optional<String> getSoftware() {
            return getProperty("s", String.class::cast);
        }

        public MetadataNode setSoftware(String software) {
            createProperty(CastPropertyID.STRING, "s", software);
            return this;
        }

        public Optional<UpAxis> getUpAxis() {
            return getProperty("up", UpAxis::from);
        }

        public MetadataNode setUpAxis(UpAxis upAxis) {
            createProperty(CastPropertyID.STRING, "up", upAxis.toString().toLowerCase());
            return this;
        }

    }
}
