package org.redeye.valen.game.spacemarines2.converters;

import be.twofold.valen.core.geometry.*;
import org.redeye.valen.game.spacemarines2.types.template.*;

import java.util.*;

public class SkeletonConverter {

    private static void processNodes(SM2Node node, List<Bone> bones, Map<Integer, Integer> boneMap) {
        int parentId = -1;
        if (node.parent() != null && node.parent().parent() != null) {
            for (int i = 0; i < bones.size(); i++) {
                if (bones.get(i).name().equals(node.parent().name())) {
                    parentId = i;
                    break;
                }
            }
            if (parentId == -1) {
                throw new IllegalStateException("Failed to find parent: " + node);
            }
        }

        var bone = new Bone(node.name(), parentId, node.matrix().toRotation(), node.matrix().toScale(), node.matrix().toTranslation(), node.inverseMatrix());
        boneMap.put(node.getId(), bones.size());
        bones.add(bone);
        node.children().forEach(n -> processNodes(n, bones, boneMap));
    }

    public Skeleton convertSkeleton(GeometryManager geometryManager, Map<Integer, Integer> boneMap) {

        var sm2Nodes = new HashMap<Integer, SM2Node>();
        geometryManager.objects.forEach(objObj -> handleBone(geometryManager.objects, objObj, sm2Nodes));

        sm2Nodes.get(Integer.valueOf(geometryManager.rootObjId)).setBone(false);
        sm2Nodes.values().stream()
            .filter(SM2Node::isBone)
            .forEach(this::propagateIsBone);
        sm2Nodes.values().forEach(this::pruneBranches);
        sm2Nodes.get(Integer.valueOf(geometryManager.rootObjId)).setBone(true);

        var toRemove = sm2Nodes.entrySet().stream()
            .filter(entry -> !entry.getValue().isBone())
            .map(Map.Entry::getKey).toList();
        toRemove.forEach(sm2Nodes::remove);

        SM2Node rootNode = sm2Nodes.get(Integer.valueOf(geometryManager.rootObjId));
        if (rootNode.children().size() == 1) {
            var bones = new ArrayList<Bone>();
            processNodes(rootNode.children().getFirst(), bones, boneMap);
            if (!bones.isEmpty()) {
                return new Skeleton(bones);
            }
        } else {
            var bones = new ArrayList<Bone>();
            processNodes(rootNode, bones, boneMap);
            if (!bones.isEmpty()) {
                return new Skeleton(bones);
            }
        }
        return null;
    }

    private void handleBone(List<ObjObj> objects, ObjObj node, Map<Integer, SM2Node> nodes) {
        if (nodes.containsKey(Integer.valueOf(node.getId()))) {
            return;
        }

        SM2Node parent;
        if (node.getParentId() == -1) {
            parent = null;
        } else {
            if (!nodes.containsKey(Integer.valueOf(node.getParentId()))) {
                handleBone(objects, objects.get(node.getParentId()), nodes);
            }
            parent = nodes.get(Integer.valueOf(node.getParentId()));
        }

        var bone = new SM2Node(node.getName() == null ? "NODE_" + node.getId() : node.getName(), node.getModelMatrix(), node.getMatrixLt().inverse(), parent, new ArrayList<>(), node.getState().contains(ObjState.OBJ_ST_IS_BONE), node.getId());
        nodes.put(Integer.valueOf(node.getId()), bone);
        if (parent != null) {
            parent.children().add(bone);
        }

    }

    private void propagateIsBone(SM2Node node) {
        if (checkIsBone(node)) {
            setIsBone(node);
        }
    }

    private boolean checkIsBone(SM2Node node) {
        if (node.isBone()) return true;
        for (SM2Node child : node.children()) {
            if (checkIsBone(child)) return true;
        }
        return false;
    }

    private void setIsBone(SM2Node node) {
        node.setBone(true);
        for (SM2Node child : node.children()) {
            setIsBone(child);
        }
    }

    private void pruneBranches(SM2Node node) {
        node.children().removeIf(child -> {
            pruneBranches(child); // Ensure we walk deeper first
            return !child.isBone() && child.children().isEmpty();
        });
    }

}
