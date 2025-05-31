package be.twofold.valen.format.cast;

import java.nio.*;
import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;

public abstract class CastNode {
    private final CastNodeID identifier;
    private final long hash;
    final AtomicLong hasher;
    final Map<String, CastProperty> properties;
    final List<CastNode> children;

    CastNode(CastNodeID identifier, AtomicLong hasher) {
        this(identifier, hasher.getAndIncrement(), hasher, new LinkedHashMap<>(), new ArrayList<>());
    }

    CastNode(CastNodeID identifier, long hash, Map<String, CastProperty> properties, List<CastNode> children) {
        this(identifier, hash, null, properties, children);
    }

    private CastNode(CastNodeID identifier, long hash, AtomicLong hasher, Map<String, CastProperty> properties, List<CastNode> children) {
        this.identifier = Objects.requireNonNull(identifier);
        this.hash = hash;
        this.hasher = hasher;
        this.properties = properties;
        this.children = children;
    }

    public final CastNodeID identifier() {
        return identifier;
    }

    public final long hash() {
        return hash;
    }

    public int length() {
        int result = 0x18;
        for (CastProperty property : properties.values()) {
            result += property.length();
        }
        for (CastNode child : children) {
            result += child.length();
        }
        return result;
    }

    public <T extends CastNode> Optional<T> getChildOfType(Class<T> type) {
        return children.stream().filter(type::isInstance).map(type::cast).findFirst();
    }

    public <T extends CastNode> List<T> getChildrenOfType(Class<T> type) {
        return children.stream().filter(type::isInstance).map(type::cast).toList();
    }

    public <T> Optional<T> getProperty(String name, Function<Object, ? extends T> mapper) {
        return Optional.ofNullable(properties.get(name))
            .map(p -> mapper.apply(p.value()));
    }

    <T extends CastNode> T createChild(T child) {
        children.add(child);
        return child;
    }

    void createProperty(CastPropertyID identifier, String name, Object value) {
        properties.put(name, new CastProperty(identifier, name, value));
    }

    void createIntProperty(String name, int value) {
        long l = Integer.toUnsignedLong(value);
        if (l <= 0xFF) {
            createProperty(CastPropertyID.BYTE, name, (byte) l);
        } else if (l <= 0xFFFF) {
            createProperty(CastPropertyID.SHORT, name, (short) l);
        } else {
            createProperty(CastPropertyID.INT, name, (int) l);
        }
    }

    void createIntBufferProperty(String name, Buffer value) {
        Buffer buffer = Buffers.shrink(value);
        switch (buffer) {
            case ByteBuffer _ -> createProperty(CastPropertyID.BYTE, name, buffer);
            case ShortBuffer _ -> createProperty(CastPropertyID.SHORT, name, buffer);
            case IntBuffer _ -> createProperty(CastPropertyID.INT, name, buffer);
            default -> throw new IllegalArgumentException("Only integral buffers supported");
        }
    }

    boolean parseBoolean(Object value) {
        int i = ((Number) value).intValue();
        return switch (i) {
            case 0 -> true;
            case 1 -> false;
            default -> throw new IllegalArgumentException("Expected 0 or 1 but got " + i);
        };
    }

    // region Enums

    public enum ConstraintType {
        SC, OR, PT;

        public static ConstraintType from(Object o) {
            return valueOf(o.toString().toUpperCase());
        }
    }

    public enum ColorSpace {
        LINEAR, SRGB;

        public static ColorSpace from(Object o) {
            return valueOf(o.toString().toUpperCase());
        }
    }

    public enum UpAxis {
        X, Y, Z;

        public static UpAxis from(Object o) {
            return valueOf(o.toString().toUpperCase());
        }
    }

    public enum KeyPropertyName {
        BS, TX, TY, SX, TZ, SY, SZ, VB, RQ;

        public static KeyPropertyName from(Object o) {
            return valueOf(o.toString().toUpperCase());
        }
    }

    public enum Mode {
        ABSOLUTE, ADDITIVE, RELATIVE;

        public static Mode from(Object o) {
            return valueOf(o.toString().toUpperCase());
        }
    }

    public enum Type {
        PBR;

        public static Type from(Object o) {
            return valueOf(o.toString().toUpperCase());
        }
    }

    public enum SkinningMethod {
        LINEAR, QUATERNION;

        public static SkinningMethod from(Object o) {
            return valueOf(o.toString().toUpperCase());
        }
    }

    // endregion

    // region Nodes

    public static final class Root extends CastNode {
        Root(AtomicLong hasher) {
            super(CastNodeID.ROOT, hasher);
        }

        Root(long hash, Map<String, CastProperty> properties, List<CastNode> children) {
            super(CastNodeID.ROOT, hash, properties, children);
        }

        public List<Model> getModels() {
            return getChildrenOfType(Model.class);
        }

        public Model createModel() {
            return createChild(new Model(hasher));
        }

        public List<Animation> getAnimations() {
            return getChildrenOfType(Animation.class);
        }

        public Animation createAnimation() {
            return createChild(new Animation(hasher));
        }

        public List<Instance> getInstances() {
            return getChildrenOfType(Instance.class);
        }

        public Instance createInstance() {
            return createChild(new Instance(hasher));
        }

        public List<Metadata> getMetadatas() {
            return getChildrenOfType(Metadata.class);
        }

        public Metadata createMetadata() {
            return createChild(new Metadata(hasher));
        }
    }

    public static final class Model extends CastNode {
        Model(AtomicLong hasher) {
            super(CastNodeID.MODEL, hasher);
        }

        Model(long hash, Map<String, CastProperty> properties, List<CastNode> children) {
            super(CastNodeID.MODEL, hash, properties, children);
        }

        public Optional<Skeleton> getSkeletons() {
            return getChildOfType(Skeleton.class);
        }

        public Skeleton createSkeleton() {
            return createChild(new Skeleton(hasher));
        }

        public List<Mesh> getMeshes() {
            return getChildrenOfType(Mesh.class);
        }

        public Mesh createMesh() {
            return createChild(new Mesh(hasher));
        }

        public List<Hair> getHairs() {
            return getChildrenOfType(Hair.class);
        }

        public Hair createHair() {
            return createChild(new Hair(hasher));
        }

        public List<BlendShape> getBlendShapes() {
            return getChildrenOfType(BlendShape.class);
        }

        public BlendShape createBlendShape() {
            return createChild(new BlendShape(hasher));
        }

        public List<Material> getMaterials() {
            return getChildrenOfType(Material.class);
        }

        public Material createMaterial() {
            return createChild(new Material(hasher));
        }

        public Optional<String> getName() {
            return getProperty("n", String.class::cast);
        }

        public Model setName(String name) {
            createProperty(CastPropertyID.STRING, "n", name);
            return this;
        }

        public Optional<Vec3> getPosition() {
            return getProperty("p", Vec3.class::cast);
        }

        public Model setPosition(Vec3 position) {
            createProperty(CastPropertyID.VECTOR3, "p", position);
            return this;
        }

        public Optional<Vec4> getRotation() {
            return getProperty("r", Vec4.class::cast);
        }

        public Model setRotation(Vec4 rotation) {
            createProperty(CastPropertyID.VECTOR4, "r", rotation);
            return this;
        }

        public Optional<Vec3> getScale() {
            return getProperty("s", Vec3.class::cast);
        }

        public Model setScale(Vec3 scale) {
            createProperty(CastPropertyID.VECTOR3, "s", scale);
            return this;
        }
    }

    public static final class Mesh extends CastNode {
        private int vertexColorBufferIndex, vertexUVBufferIndex;

        Mesh(AtomicLong hasher) {
            super(CastNodeID.MESH, hasher);
        }

        Mesh(long hash, Map<String, CastProperty> properties, List<CastNode> children) {
            super(CastNodeID.MESH, hash, properties, children);
        }

        public Optional<String> getName() {
            return getProperty("n", String.class::cast);
        }

        public Mesh setName(String name) {
            createProperty(CastPropertyID.STRING, "n", name);
            return this;
        }

        public FloatBuffer getVertexPositionBuffer() {
            return getProperty("vp", FloatBuffer.class::cast).orElseThrow();
        }

        public Mesh setVertexPositionBuffer(FloatBuffer vertexPositionBuffer) {
            createProperty(CastPropertyID.VECTOR3, "vp", vertexPositionBuffer);
            return this;
        }

        public Optional<FloatBuffer> getVertexNormalBuffer() {
            return getProperty("vn", FloatBuffer.class::cast);
        }

        public Mesh setVertexNormalBuffer(FloatBuffer vertexNormalBuffer) {
            createProperty(CastPropertyID.VECTOR3, "vn", vertexNormalBuffer);
            return this;
        }

        public Optional<FloatBuffer> getVertexTangentBuffer() {
            return getProperty("vt", FloatBuffer.class::cast);
        }

        public Mesh setVertexTangentBuffer(FloatBuffer vertexTangentBuffer) {
            createProperty(CastPropertyID.VECTOR3, "vt", vertexTangentBuffer);
            return this;
        }

        public Optional<Buffer> getVertexColorBuffer(int index) {
            return getProperty("c" + index, Buffer.class::cast);
        }

        public Mesh addVertexColorBuffer(Buffer vertexColorBuffer) {
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

        public Mesh addVertexUVBuffer(FloatBuffer vertexUVBuffer) {
            createProperty(CastPropertyID.VECTOR2, "u" + vertexUVBufferIndex++, vertexUVBuffer);
            return this;
        }

        public Optional<Buffer> getVertexWeightBoneBuffer() {
            return getProperty("wb", Buffer.class::cast);
        }

        public Mesh setVertexWeightBoneBuffer(Buffer vertexWeightBoneBuffer) {
            createIntBufferProperty("wb", vertexWeightBoneBuffer);
            return this;
        }

        public Optional<FloatBuffer> getVertexWeightValueBuffer() {
            return getProperty("wv", FloatBuffer.class::cast);
        }

        public Mesh setVertexWeightValueBuffer(FloatBuffer vertexWeightValueBuffer) {
            createProperty(CastPropertyID.FLOAT, "wv", vertexWeightValueBuffer);
            return this;
        }

        public Buffer getFaceBuffer() {
            return getProperty("f", Buffer.class::cast).orElseThrow();
        }

        public Mesh setFaceBuffer(Buffer faceBuffer) {
            createIntBufferProperty("f", faceBuffer);
            return this;
        }

        public Optional<Integer> getColorLayerCount() {
            return getProperty("cl", Integer.class::cast);
        }

        public Mesh setColorLayerCount(Integer colorLayerCount) {
            createIntProperty("cl", colorLayerCount);
            return this;
        }

        public Optional<Integer> getUVLayerCount() {
            return getProperty("ul", Integer.class::cast);
        }

        public Mesh setUVLayerCount(Integer uVLayerCount) {
            createIntProperty("ul", uVLayerCount);
            return this;
        }

        public Optional<Integer> getMaximumWeightInfluence() {
            return getProperty("mi", Integer.class::cast);
        }

        public Mesh setMaximumWeightInfluence(Integer maximumWeightInfluence) {
            createIntProperty("mi", maximumWeightInfluence);
            return this;
        }

        public Optional<SkinningMethod> getSkinningMethod() {
            return getProperty("sm", SkinningMethod::from);
        }

        public Mesh setSkinningMethod(SkinningMethod skinningMethod) {
            createProperty(CastPropertyID.STRING, "sm", skinningMethod.toString().toLowerCase());
            return this;
        }

        public Optional<Long> getMaterial() {
            return getProperty("m", Long.class::cast);
        }

        public Mesh setMaterial(Long material) {
            createProperty(CastPropertyID.LONG, "m", material);
            return this;
        }
    }

    public static final class Hair extends CastNode {
        Hair(AtomicLong hasher) {
            super(CastNodeID.HAIR, hasher);
        }

        Hair(long hash, Map<String, CastProperty> properties, List<CastNode> children) {
            super(CastNodeID.HAIR, hash, properties, children);
        }

        public Optional<String> getName() {
            return getProperty("n", String.class::cast);
        }

        public Hair setName(String name) {
            createProperty(CastPropertyID.STRING, "n", name);
            return this;
        }

        public Buffer getSegmentsBuffer() {
            return getProperty("se", Buffer.class::cast).orElseThrow();
        }

        public Hair setSegmentsBuffer(Buffer segmentsBuffer) {
            createIntBufferProperty("se", segmentsBuffer);
            return this;
        }

        public FloatBuffer getParticleBuffer() {
            return getProperty("pt", FloatBuffer.class::cast).orElseThrow();
        }

        public Hair setParticleBuffer(FloatBuffer particleBuffer) {
            createProperty(CastPropertyID.VECTOR3, "pt", particleBuffer);
            return this;
        }

        public Optional<Long> getMaterial() {
            return getProperty("m", Long.class::cast);
        }

        public Hair setMaterial(Long material) {
            createProperty(CastPropertyID.LONG, "m", material);
            return this;
        }
    }

    public static final class BlendShape extends CastNode {
        BlendShape(AtomicLong hasher) {
            super(CastNodeID.BLEND_SHAPE, hasher);
        }

        BlendShape(long hash, Map<String, CastProperty> properties, List<CastNode> children) {
            super(CastNodeID.BLEND_SHAPE, hash, properties, children);
        }

        public String getName() {
            return getProperty("n", String.class::cast).orElseThrow();
        }

        public BlendShape setName(String name) {
            createProperty(CastPropertyID.STRING, "n", name);
            return this;
        }

        public long getBaseShape() {
            return getProperty("b", Long.class::cast).orElseThrow();
        }

        public BlendShape setBaseShape(Long baseShape) {
            createProperty(CastPropertyID.LONG, "b", baseShape);
            return this;
        }

        public Buffer getTargetShapeVertexIndices() {
            return getProperty("vi", Buffer.class::cast).orElseThrow();
        }

        public BlendShape setTargetShapeVertexIndices(Buffer targetShapeVertexIndices) {
            createIntBufferProperty("vi", targetShapeVertexIndices);
            return this;
        }

        public FloatBuffer getTargetShapeVertexPositions() {
            return getProperty("vp", FloatBuffer.class::cast).orElseThrow();
        }

        public BlendShape setTargetShapeVertexPositions(FloatBuffer targetShapeVertexPositions) {
            createProperty(CastPropertyID.VECTOR3, "vp", targetShapeVertexPositions);
            return this;
        }

        public Optional<FloatBuffer> getTargetWeightScale() {
            return getProperty("ts", FloatBuffer.class::cast);
        }

        public BlendShape setTargetWeightScale(FloatBuffer targetWeightScale) {
            createProperty(CastPropertyID.FLOAT, "ts", targetWeightScale);
            return this;
        }
    }

    public static final class Skeleton extends CastNode {
        Skeleton(AtomicLong hasher) {
            super(CastNodeID.SKELETON, hasher);
        }

        Skeleton(long hash, Map<String, CastProperty> properties, List<CastNode> children) {
            super(CastNodeID.SKELETON, hash, properties, children);
        }

        public List<Bone> getBones() {
            return getChildrenOfType(Bone.class);
        }

        public Bone createBone() {
            return createChild(new Bone(hasher));
        }

        public List<IkHandle> getIkHandles() {
            return getChildrenOfType(IkHandle.class);
        }

        public IkHandle createIkHandle() {
            return createChild(new IkHandle(hasher));
        }

        public List<Constraint> getConstraints() {
            return getChildrenOfType(Constraint.class);
        }

        public Constraint createConstraint() {
            return createChild(new Constraint(hasher));
        }
    }

    public static final class Bone extends CastNode {
        Bone(AtomicLong hasher) {
            super(CastNodeID.BONE, hasher);
        }

        Bone(long hash, Map<String, CastProperty> properties, List<CastNode> children) {
            super(CastNodeID.BONE, hash, properties, children);
        }

        public String getName() {
            return getProperty("n", String.class::cast).orElseThrow();
        }

        public Bone setName(String name) {
            createProperty(CastPropertyID.STRING, "n", name);
            return this;
        }

        public Optional<Integer> getParentIndex() {
            return getProperty("p", Integer.class::cast);
        }

        public Bone setParentIndex(Integer parentIndex) {
            createProperty(CastPropertyID.INT, "p", parentIndex);
            return this;
        }

        public Optional<Boolean> getSegmentScaleCompensate() {
            return getProperty("ssc", this::parseBoolean);
        }

        public Bone setSegmentScaleCompensate(Boolean segmentScaleCompensate) {
            createProperty(CastPropertyID.BYTE, "ssc", segmentScaleCompensate ? 1 : 0);
            return this;
        }

        public Optional<Vec3> getLocalPosition() {
            return getProperty("lp", Vec3.class::cast);
        }

        public Bone setLocalPosition(Vec3 localPosition) {
            createProperty(CastPropertyID.VECTOR3, "lp", localPosition);
            return this;
        }

        public Optional<Vec4> getLocalRotation() {
            return getProperty("lr", Vec4.class::cast);
        }

        public Bone setLocalRotation(Vec4 localRotation) {
            createProperty(CastPropertyID.VECTOR4, "lr", localRotation);
            return this;
        }

        public Optional<Vec3> getWorldPosition() {
            return getProperty("wp", Vec3.class::cast);
        }

        public Bone setWorldPosition(Vec3 worldPosition) {
            createProperty(CastPropertyID.VECTOR3, "wp", worldPosition);
            return this;
        }

        public Optional<Vec4> getWorldRotation() {
            return getProperty("wr", Vec4.class::cast);
        }

        public Bone setWorldRotation(Vec4 worldRotation) {
            createProperty(CastPropertyID.VECTOR4, "wr", worldRotation);
            return this;
        }

        public Optional<Vec3> getScale() {
            return getProperty("s", Vec3.class::cast);
        }

        public Bone setScale(Vec3 scale) {
            createProperty(CastPropertyID.VECTOR3, "s", scale);
            return this;
        }
    }

    public static final class IkHandle extends CastNode {
        IkHandle(AtomicLong hasher) {
            super(CastNodeID.IK_HANDLE, hasher);
        }

        IkHandle(long hash, Map<String, CastProperty> properties, List<CastNode> children) {
            super(CastNodeID.IK_HANDLE, hash, properties, children);
        }

        public Optional<String> getName() {
            return getProperty("n", String.class::cast);
        }

        public IkHandle setName(String name) {
            createProperty(CastPropertyID.STRING, "n", name);
            return this;
        }

        public long getStartBoneHash() {
            return getProperty("sb", Long.class::cast).orElseThrow();
        }

        public IkHandle setStartBoneHash(Long startBoneHash) {
            createProperty(CastPropertyID.LONG, "sb", startBoneHash);
            return this;
        }

        public long getEndBoneHash() {
            return getProperty("eb", Long.class::cast).orElseThrow();
        }

        public IkHandle setEndBoneHash(Long endBoneHash) {
            createProperty(CastPropertyID.LONG, "eb", endBoneHash);
            return this;
        }

        public Optional<Long> getTargetBoneHash() {
            return getProperty("tb", Long.class::cast);
        }

        public IkHandle setTargetBoneHash(Long targetBoneHash) {
            createProperty(CastPropertyID.LONG, "tb", targetBoneHash);
            return this;
        }

        public Optional<Long> getPoleVectorBoneHash() {
            return getProperty("pv", Long.class::cast);
        }

        public IkHandle setPoleVectorBoneHash(Long poleVectorBoneHash) {
            createProperty(CastPropertyID.LONG, "pv", poleVectorBoneHash);
            return this;
        }

        public Optional<Long> getPoleBoneHash() {
            return getProperty("pb", Long.class::cast);
        }

        public IkHandle setPoleBoneHash(Long poleBoneHash) {
            createProperty(CastPropertyID.LONG, "pb", poleBoneHash);
            return this;
        }

        public Optional<Boolean> getUseTargetRotation() {
            return getProperty("tr", this::parseBoolean);
        }

        public IkHandle setUseTargetRotation(Boolean useTargetRotation) {
            createProperty(CastPropertyID.BYTE, "tr", useTargetRotation ? 1 : 0);
            return this;
        }
    }

    public static final class Constraint extends CastNode {
        Constraint(AtomicLong hasher) {
            super(CastNodeID.CONSTRAINT, hasher);
        }

        Constraint(long hash, Map<String, CastProperty> properties, List<CastNode> children) {
            super(CastNodeID.CONSTRAINT, hash, properties, children);
        }

        public Optional<String> getName() {
            return getProperty("n", String.class::cast);
        }

        public Constraint setName(String name) {
            createProperty(CastPropertyID.STRING, "n", name);
            return this;
        }

        public ConstraintType getConstraintType() {
            return getProperty("ct", ConstraintType::from).orElseThrow();
        }

        public Constraint setConstraintType(ConstraintType constraintType) {
            createProperty(CastPropertyID.STRING, "ct", constraintType.toString().toLowerCase());
            return this;
        }

        public long getConstraintBoneHash() {
            return getProperty("cb", Long.class::cast).orElseThrow();
        }

        public Constraint setConstraintBoneHash(Long constraintBoneHash) {
            createProperty(CastPropertyID.LONG, "cb", constraintBoneHash);
            return this;
        }

        public long getTargetBoneHash() {
            return getProperty("tb", Long.class::cast).orElseThrow();
        }

        public Constraint setTargetBoneHash(Long targetBoneHash) {
            createProperty(CastPropertyID.LONG, "tb", targetBoneHash);
            return this;
        }

        public Optional<Boolean> getMaintainOffset() {
            return getProperty("mo", this::parseBoolean);
        }

        public Constraint setMaintainOffset(Boolean maintainOffset) {
            createProperty(CastPropertyID.BYTE, "mo", maintainOffset ? 1 : 0);
            return this;
        }

        public Optional<Object> getCustomOffset() {
            return getProperty("co", Object.class::cast);
        }

        public Constraint setCustomOffset(Object customOffset) {
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

        public Constraint setWeight(Float weight) {
            createProperty(CastPropertyID.FLOAT, "wt", weight);
            return this;
        }

        public Optional<Boolean> getSkipX() {
            return getProperty("sx", this::parseBoolean);
        }

        public Constraint setSkipX(Boolean skipX) {
            createProperty(CastPropertyID.BYTE, "sx", skipX ? 1 : 0);
            return this;
        }

        public Optional<Boolean> getSkipY() {
            return getProperty("sy", this::parseBoolean);
        }

        public Constraint setSkipY(Boolean skipY) {
            createProperty(CastPropertyID.BYTE, "sy", skipY ? 1 : 0);
            return this;
        }

        public Optional<Boolean> getSkipZ() {
            return getProperty("sz", this::parseBoolean);
        }

        public Constraint setSkipZ(Boolean skipZ) {
            createProperty(CastPropertyID.BYTE, "sz", skipZ ? 1 : 0);
            return this;
        }
    }

    public static final class Animation extends CastNode {
        Animation(AtomicLong hasher) {
            super(CastNodeID.ANIMATION, hasher);
        }

        Animation(long hash, Map<String, CastProperty> properties, List<CastNode> children) {
            super(CastNodeID.ANIMATION, hash, properties, children);
        }

        public Optional<Skeleton> getSkeletons() {
            return getChildOfType(Skeleton.class);
        }

        public Skeleton createSkeleton() {
            return createChild(new Skeleton(hasher));
        }

        public List<Curve> getCurves() {
            return getChildrenOfType(Curve.class);
        }

        public Curve createCurve() {
            return createChild(new Curve(hasher));
        }

        public List<CurveModeOverride> getCurveModeOverrides() {
            return getChildrenOfType(CurveModeOverride.class);
        }

        public CurveModeOverride createCurveModeOverride() {
            return createChild(new CurveModeOverride(hasher));
        }

        public List<NotificationTrack> getNotificationTracks() {
            return getChildrenOfType(NotificationTrack.class);
        }

        public NotificationTrack createNotificationTrack() {
            return createChild(new NotificationTrack(hasher));
        }

        public Optional<String> getName() {
            return getProperty("n", String.class::cast);
        }

        public Animation setName(String name) {
            createProperty(CastPropertyID.STRING, "n", name);
            return this;
        }

        public float getFramerate() {
            return getProperty("fr", Float.class::cast).orElseThrow();
        }

        public Animation setFramerate(Float framerate) {
            createProperty(CastPropertyID.FLOAT, "fr", framerate);
            return this;
        }

        public Optional<Boolean> getLooping() {
            return getProperty("lo", this::parseBoolean);
        }

        public Animation setLooping(Boolean looping) {
            createProperty(CastPropertyID.BYTE, "lo", looping ? 1 : 0);
            return this;
        }
    }

    public static final class Curve extends CastNode {
        Curve(AtomicLong hasher) {
            super(CastNodeID.CURVE, hasher);
        }

        Curve(long hash, Map<String, CastProperty> properties, List<CastNode> children) {
            super(CastNodeID.CURVE, hash, properties, children);
        }

        public String getNodeName() {
            return getProperty("nn", String.class::cast).orElseThrow();
        }

        public Curve setNodeName(String nodeName) {
            createProperty(CastPropertyID.STRING, "nn", nodeName);
            return this;
        }

        public KeyPropertyName getKeyPropertyName() {
            return getProperty("kp", KeyPropertyName::from).orElseThrow();
        }

        public Curve setKeyPropertyName(KeyPropertyName keyPropertyName) {
            createProperty(CastPropertyID.STRING, "kp", keyPropertyName.toString().toLowerCase());
            return this;
        }

        public Buffer getKeyFrameBuffer() {
            return getProperty("kb", Buffer.class::cast).orElseThrow();
        }

        public Curve setKeyFrameBuffer(Buffer keyFrameBuffer) {
            createIntBufferProperty("kb", keyFrameBuffer);
            return this;
        }

        public Buffer getKeyValueBuffer() {
            return getProperty("kv", Buffer.class::cast).orElseThrow();
        }

        public Curve setKeyValueBuffer(Buffer keyValueBuffer) {
            if (keyValueBuffer instanceof ByteBuffer) {
                createProperty(CastPropertyID.BYTE, "kv", keyValueBuffer);
            } else if (keyValueBuffer instanceof ShortBuffer) {
                createProperty(CastPropertyID.SHORT, "kv", keyValueBuffer);
            } else if (keyValueBuffer instanceof IntBuffer) {
                createProperty(CastPropertyID.INT, "kv", keyValueBuffer);
            } else if (keyValueBuffer instanceof FloatBuffer) {
                createProperty(CastPropertyID.FLOAT, "kv", keyValueBuffer);
            } else {
                throw new IllegalArgumentException("Invalid type for property keyValueBuffer");
            }
            return this;
        }

        public Curve setKeyValueBufferV4(FloatBuffer keyValueBuffer) {
            createProperty(CastPropertyID.VECTOR4, "kv", keyValueBuffer);
            return this;
        }

        public Mode getMode() {
            return getProperty("m", Mode::from).orElseThrow();
        }

        public Curve setMode(Mode mode) {
            createProperty(CastPropertyID.STRING, "m", mode.toString().toLowerCase());
            return this;
        }

        public Optional<Float> getAdditiveBlendWeight() {
            return getProperty("ab", Float.class::cast);
        }

        public Curve setAdditiveBlendWeight(Float additiveBlendWeight) {
            createProperty(CastPropertyID.FLOAT, "ab", additiveBlendWeight);
            return this;
        }
    }

    public static final class CurveModeOverride extends CastNode {
        CurveModeOverride(AtomicLong hasher) {
            super(CastNodeID.CURVE_MODE_OVERRIDE, hasher);
        }

        CurveModeOverride(long hash, Map<String, CastProperty> properties, List<CastNode> children) {
            super(CastNodeID.CURVE_MODE_OVERRIDE, hash, properties, children);
        }

        public String getNodeName() {
            return getProperty("nn", String.class::cast).orElseThrow();
        }

        public CurveModeOverride setNodeName(String nodeName) {
            createProperty(CastPropertyID.STRING, "nn", nodeName);
            return this;
        }

        public Mode getMode() {
            return getProperty("m", Mode::from).orElseThrow();
        }

        public CurveModeOverride setMode(Mode mode) {
            createProperty(CastPropertyID.STRING, "m", mode.toString().toLowerCase());
            return this;
        }

        public Optional<Boolean> getOverrideTranslationCurves() {
            return getProperty("ot", this::parseBoolean);
        }

        public CurveModeOverride setOverrideTranslationCurves(Boolean overrideTranslationCurves) {
            createProperty(CastPropertyID.BYTE, "ot", overrideTranslationCurves ? 1 : 0);
            return this;
        }

        public Optional<Boolean> getOverrideRotationCurves() {
            return getProperty("or", this::parseBoolean);
        }

        public CurveModeOverride setOverrideRotationCurves(Boolean overrideRotationCurves) {
            createProperty(CastPropertyID.BYTE, "or", overrideRotationCurves ? 1 : 0);
            return this;
        }

        public Optional<Boolean> getOverrideScaleCurves() {
            return getProperty("os", this::parseBoolean);
        }

        public CurveModeOverride setOverrideScaleCurves(Boolean overrideScaleCurves) {
            createProperty(CastPropertyID.BYTE, "os", overrideScaleCurves ? 1 : 0);
            return this;
        }
    }

    public static final class NotificationTrack extends CastNode {
        NotificationTrack(AtomicLong hasher) {
            super(CastNodeID.NOTIFICATION_TRACK, hasher);
        }

        NotificationTrack(long hash, Map<String, CastProperty> properties, List<CastNode> children) {
            super(CastNodeID.NOTIFICATION_TRACK, hash, properties, children);
        }

        public String getName() {
            return getProperty("n", String.class::cast).orElseThrow();
        }

        public NotificationTrack setName(String name) {
            createProperty(CastPropertyID.STRING, "n", name);
            return this;
        }

        public Buffer getKeyFrameBuffer() {
            return getProperty("kb", Buffer.class::cast).orElseThrow();
        }

        public NotificationTrack setKeyFrameBuffer(Buffer keyFrameBuffer) {
            createIntBufferProperty("kb", keyFrameBuffer);
            return this;
        }
    }

    public static final class Material extends CastNode {
        private int extraIndex;

        Material(AtomicLong hasher) {
            super(CastNodeID.MATERIAL, hasher);
        }

        Material(long hash, Map<String, CastProperty> properties, List<CastNode> children) {
            super(CastNodeID.MATERIAL, hash, properties, children);
        }

        public List<File> getFiles() {
            return getChildrenOfType(File.class);
        }

        public File createFile() {
            return createChild(new File(hasher));
        }

        public List<Color> getColors() {
            return getChildrenOfType(Color.class);
        }

        public Color createColor() {
            return createChild(new Color(hasher));
        }

        public String getName() {
            return getProperty("n", String.class::cast).orElseThrow();
        }

        public Material setName(String name) {
            createProperty(CastPropertyID.STRING, "n", name);
            return this;
        }

        public Type getType() {
            return getProperty("t", Type::from).orElseThrow();
        }

        public Material setType(Type type) {
            createProperty(CastPropertyID.STRING, "t", type.toString().toLowerCase());
            return this;
        }

        public Optional<Long> getAlbedoHash() {
            return getProperty("albedo", Long.class::cast);
        }

        public Material setAlbedoHash(Long albedoHash) {
            createProperty(CastPropertyID.LONG, "albedo", albedoHash);
            return this;
        }

        public Optional<Long> getDiffuseHash() {
            return getProperty("diffuse", Long.class::cast);
        }

        public Material setDiffuseHash(Long diffuseHash) {
            createProperty(CastPropertyID.LONG, "diffuse", diffuseHash);
            return this;
        }

        public Optional<Long> getNormalHash() {
            return getProperty("normal", Long.class::cast);
        }

        public Material setNormalHash(Long normalHash) {
            createProperty(CastPropertyID.LONG, "normal", normalHash);
            return this;
        }

        public Optional<Long> getSpecularHash() {
            return getProperty("specular", Long.class::cast);
        }

        public Material setSpecularHash(Long specularHash) {
            createProperty(CastPropertyID.LONG, "specular", specularHash);
            return this;
        }

        public Optional<Long> getGlossHash() {
            return getProperty("gloss", Long.class::cast);
        }

        public Material setGlossHash(Long glossHash) {
            createProperty(CastPropertyID.LONG, "gloss", glossHash);
            return this;
        }

        public Optional<Long> getRoughnessHash() {
            return getProperty("roughness", Long.class::cast);
        }

        public Material setRoughnessHash(Long roughnessHash) {
            createProperty(CastPropertyID.LONG, "roughness", roughnessHash);
            return this;
        }

        public Optional<Long> getEmissiveHash() {
            return getProperty("emissive", Long.class::cast);
        }

        public Material setEmissiveHash(Long emissiveHash) {
            createProperty(CastPropertyID.LONG, "emissive", emissiveHash);
            return this;
        }

        public Optional<Long> getEmissiveMaskHash() {
            return getProperty("emask", Long.class::cast);
        }

        public Material setEmissiveMaskHash(Long emissiveMaskHash) {
            createProperty(CastPropertyID.LONG, "emask", emissiveMaskHash);
            return this;
        }

        public Optional<Long> getAmbientOcclusionHash() {
            return getProperty("ao", Long.class::cast);
        }

        public Material setAmbientOcclusionHash(Long ambientOcclusionHash) {
            createProperty(CastPropertyID.LONG, "ao", ambientOcclusionHash);
            return this;
        }

        public Optional<Long> getCavityHash() {
            return getProperty("cavity", Long.class::cast);
        }

        public Material setCavityHash(Long cavityHash) {
            createProperty(CastPropertyID.LONG, "cavity", cavityHash);
            return this;
        }

        public Optional<Long> getAnisotropyHash() {
            return getProperty("aniso", Long.class::cast);
        }

        public Material setAnisotropyHash(Long anisotropyHash) {
            createProperty(CastPropertyID.LONG, "aniso", anisotropyHash);
            return this;
        }

        public Optional<Long> getExtra(int index) {
            return getProperty("extra" + index, Long.class::cast);
        }

        public Material addExtra(Long extra) {
            createProperty(CastPropertyID.LONG, "extra" + extraIndex++, extra);
            return this;
        }
    }

    public static final class File extends CastNode {
        File(AtomicLong hasher) {
            super(CastNodeID.FILE, hasher);
        }

        File(long hash, Map<String, CastProperty> properties, List<CastNode> children) {
            super(CastNodeID.FILE, hash, properties, children);
        }

        public String getPath() {
            return getProperty("p", String.class::cast).orElseThrow();
        }

        public File setPath(String path) {
            createProperty(CastPropertyID.STRING, "p", path);
            return this;
        }
    }

    public static final class Color extends CastNode {
        Color(AtomicLong hasher) {
            super(CastNodeID.COLOR, hasher);
        }

        Color(long hash, Map<String, CastProperty> properties, List<CastNode> children) {
            super(CastNodeID.COLOR, hash, properties, children);
        }

        public Optional<String> getName() {
            return getProperty("n", String.class::cast);
        }

        public Color setName(String name) {
            createProperty(CastPropertyID.STRING, "n", name);
            return this;
        }

        public Optional<ColorSpace> getColorSpace() {
            return getProperty("cs", ColorSpace::from);
        }

        public Color setColorSpace(ColorSpace colorSpace) {
            createProperty(CastPropertyID.STRING, "cs", colorSpace.toString().toLowerCase());
            return this;
        }

        public Vec4 getRgbaColor() {
            return getProperty("rgba", Vec4.class::cast).orElseThrow();
        }

        public Color setRgbaColor(Vec4 rgbaColor) {
            createProperty(CastPropertyID.VECTOR4, "rgba", rgbaColor);
            return this;
        }
    }

    public static final class Instance extends CastNode {
        Instance(AtomicLong hasher) {
            super(CastNodeID.INSTANCE, hasher);
        }

        Instance(long hash, Map<String, CastProperty> properties, List<CastNode> children) {
            super(CastNodeID.INSTANCE, hash, properties, children);
        }

        public List<File> getFiles() {
            return getChildrenOfType(File.class);
        }

        public File createFile() {
            return createChild(new File(hasher));
        }

        public Optional<String> getName() {
            return getProperty("n", String.class::cast);
        }

        public Instance setName(String name) {
            createProperty(CastPropertyID.STRING, "n", name);
            return this;
        }

        public long getReferenceFile() {
            return getProperty("rf", Long.class::cast).orElseThrow();
        }

        public Instance setReferenceFile(Long referenceFile) {
            createProperty(CastPropertyID.LONG, "rf", referenceFile);
            return this;
        }

        public Vec3 getPosition() {
            return getProperty("p", Vec3.class::cast).orElseThrow();
        }

        public Instance setPosition(Vec3 position) {
            createProperty(CastPropertyID.VECTOR3, "p", position);
            return this;
        }

        public Vec4 getRotation() {
            return getProperty("r", Vec4.class::cast).orElseThrow();
        }

        public Instance setRotation(Vec4 rotation) {
            createProperty(CastPropertyID.VECTOR4, "r", rotation);
            return this;
        }

        public Vec3 getScale() {
            return getProperty("s", Vec3.class::cast).orElseThrow();
        }

        public Instance setScale(Vec3 scale) {
            createProperty(CastPropertyID.VECTOR3, "s", scale);
            return this;
        }
    }

    public static final class Metadata extends CastNode {
        Metadata(AtomicLong hasher) {
            super(CastNodeID.METADATA, hasher);
        }

        Metadata(long hash, Map<String, CastProperty> properties, List<CastNode> children) {
            super(CastNodeID.METADATA, hash, properties, children);
        }

        public Optional<String> getAuthor() {
            return getProperty("a", String.class::cast);
        }

        public Metadata setAuthor(String author) {
            createProperty(CastPropertyID.STRING, "a", author);
            return this;
        }

        public Optional<String> getSoftware() {
            return getProperty("s", String.class::cast);
        }

        public Metadata setSoftware(String software) {
            createProperty(CastPropertyID.STRING, "s", software);
            return this;
        }

        public Optional<UpAxis> getUpAxis() {
            return getProperty("up", UpAxis::from);
        }

        public Metadata setUpAxis(UpAxis upAxis) {
            createProperty(CastPropertyID.STRING, "up", upAxis.toString().toLowerCase());
            return this;
        }
    }

    // endregion

}
