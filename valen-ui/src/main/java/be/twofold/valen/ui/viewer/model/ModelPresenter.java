package be.twofold.valen.ui.viewer.model;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.geometry.Mesh;
import be.twofold.valen.core.geometry.*;
import be.twofold.valen.ui.*;
import be.twofold.valen.ui.viewer.*;
import jakarta.inject.*;
import javafx.collections.*;
import javafx.scene.shape.*;

import java.nio.*;
import java.util.*;

public final class ModelPresenter extends AbstractPresenter<ModelView> implements Viewer {
    @Inject
    public ModelPresenter(ModelView view) {
        super(view);
    }

    @Override
    public boolean canPreview(AssetType<?> type) {
        return type == AssetType.MODEL;
    }

    @Override
    public void setData(Object data) {
        if (data == null) {
            getView().setMeshes(List.of());
            return;
        }
        var meshes = new ArrayList<TriangleMesh>();
        if (data instanceof Model mdl) {
            meshes.addAll(mapModel(mdl));
        } else if (data instanceof List<?> mdlList) {
            for (Object o : mdlList) {
                meshes.addAll(mapModel((Model) o));
            }
        }

        getView().setMeshes(meshes);
    }

    @Override
    public String getName() {
        return "Model";
    }

    private List<TriangleMesh> mapModel(Model model) {
        return model.meshes().stream().map(this::mapMesh).toList();
    }

    private TriangleMesh mapMesh(Mesh mesh) {
        var pointBuffer = mesh.getBuffer(Semantic.Position).orElseThrow();
        var normalBuffer = mesh.getBuffer(Semantic.Normal).orElseThrow();
        var texCoordBuffer = mesh.getBuffer(Semantic.TexCoord0).orElseThrow();

        var result = new TriangleMesh(VertexFormat.POINT_NORMAL_TEXCOORD);
        copyPoints(pointBuffer, result.getPoints());
        copy(normalBuffer, result.getNormals());
        copy(texCoordBuffer, result.getTexCoords());
        copyFaces(mesh.faceBuffer(), result.getFaces());
        return result;
    }

    private void copy(VertexBuffer buffer, ObservableFloatArray floatArray) {
        var floatBuffer = (FloatBuffer) buffer.buffer();
        var array = new float[floatBuffer.remaining()];
        floatBuffer.get(array);
        floatBuffer.rewind();
        floatArray.setAll(array);
    }

    private void copyPoints(VertexBuffer buffer, ObservableFloatArray floatArray) {
        var floatBuffer = (FloatBuffer) buffer.buffer();
        var array = new float[floatBuffer.remaining()];
        floatBuffer.get(array);
        floatBuffer.rewind();

        for (int i = 0; i < array.length; i++) {
            array[i] *= 100;
        }
        floatArray.setAll(array);
    }

    private void copyFaces(VertexBuffer buffer, ObservableFaceArray faces) {
        switch (buffer.buffer()) {
            case ByteBuffer byteBuffer -> throw new UnsupportedOperationException("ByteBuffer not supported yet");
            case ShortBuffer shortBuffer -> {
                int capacity = shortBuffer.capacity();
                int[] indices = new int[capacity * 3];
                for (int i = 0, o = 0; i < capacity; i++) {
                    int index = Short.toUnsignedInt(shortBuffer.get(i));
                    indices[o++] = index;
                    indices[o++] = index;
                    indices[o++] = index;
                }
                shortBuffer.rewind();
                faces.addAll(indices);
            }
            case IntBuffer intBuffer -> throw new UnsupportedOperationException("IntBuffer not supported yet");
            default -> throw new UnsupportedOperationException("Unexpected type: " + buffer.buffer().getClass());
        }
    }
}
