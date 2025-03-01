package be.twofold.valen.ui.component.modelviewer;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.geometry.Mesh;
import be.twofold.valen.core.geometry.*;
import be.twofold.valen.ui.common.*;
import be.twofold.valen.ui.component.*;
import jakarta.inject.*;
import javafx.collections.*;
import javafx.scene.shape.*;

import java.nio.*;
import java.util.*;

public final class ModelPresenter extends AbstractFXPresenter<ModelView> implements Viewer {
    @Inject
    public ModelPresenter(ModelView view) {
        super(view);
    }

    @Override
    public boolean canPreview(AssetType type) {
        return type == AssetType.MODEL;
    }

    @Override
    public void setData(Object data) {
        if (data == null) {
            getView().setMeshes(List.of());
            return;
        }

        var model = (Model) data;
        var meshes = mapModel(model);
        getView().setMeshes(meshes);
    }

    @Override
    public String getName() {
        return "Model";
    }

    private List<TriangleMesh> mapModel(Model model) {
        return model.meshes().stream()
            .map(this::mapMesh)
            .toList();
    }

    private TriangleMesh mapMesh(Mesh mesh) {
        var pointBuffer = mesh.getBuffer(Semantic.POSITION).orElseThrow();
        var normalBuffer = mesh.getBuffer(Semantic.NORMAL).orElseThrow();
        var texCoordBuffer = mesh.getBuffer(Semantic.TEX_COORD0).orElseThrow();

        var result = new TriangleMesh(VertexFormat.POINT_NORMAL_TEXCOORD);
        copyPoints(pointBuffer, result.getPoints());
        copy(normalBuffer, result.getNormals());
        copy(texCoordBuffer, result.getTexCoords());
        copyIndices(mesh.indexBuffer(), result.getFaces());
        return result;
    }

    private void copy(VertexBuffer<?> buffer, ObservableFloatArray floatArray) {
        var floatBuffer = (FloatBuffer) buffer.buffer();
        var array = new float[floatBuffer.remaining()];
        floatBuffer.get(array);
        floatBuffer.rewind();
        floatArray.setAll(array);
    }

    private void copyPoints(VertexBuffer<?> buffer, ObservableFloatArray floatArray) {
        var floatBuffer = (FloatBuffer) buffer.buffer();
        var array = new float[floatBuffer.remaining()];
        floatBuffer.get(array);
        floatBuffer.rewind();

        for (int i = 0; i < array.length; i++) {
            array[i] *= 100;
        }
        floatArray.setAll(array);
    }

    private void copyIndices(VertexBuffer<?> buffer, ObservableFaceArray faces) {
        switch (buffer.buffer()) {
            case ByteBuffer _ -> throw new UnsupportedOperationException("ByteBuffer not supported yet");
            case ShortBuffer shortBuffer -> {
                int capacity = shortBuffer.limit();
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
            case IntBuffer _ -> throw new UnsupportedOperationException("IntBuffer not supported yet");
            default -> throw new UnsupportedOperationException("Unexpected type: " + buffer.buffer().getClass());
        }
    }
}
