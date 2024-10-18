package org.redeye.valen.game.spacemarines2.converters;

import be.twofold.valen.core.math.*;

import java.util.*;

final class SM2Node {
    private final String name;
    private final Matrix4 matrix;
    private final Matrix4 inverseMatrix;
    private final SM2Node parent;
    private final List<SM2Node> children;
    private final int id;
    private boolean isBone;

    SM2Node(String name, Matrix4 matrix, Matrix4 inverseMatrix, SM2Node parent, List<SM2Node> children, boolean isBone, int id) {
        this.name = name;
        this.matrix = matrix;
        this.inverseMatrix = inverseMatrix;
        this.parent = parent;
        this.children = children;
        this.isBone = isBone;
        this.id = id;
    }

    public String name() {
        return name;
    }

    public Matrix4 matrix() {
        return matrix;
    }

    public Matrix4 inverseMatrix() {
        return inverseMatrix;
    }

    public SM2Node parent() {
        return parent;
    }

    public List<SM2Node> children() {
        return children;
    }

    public boolean isBone() {
        return isBone;
    }

    public void setBone(boolean bone) {
        isBone = bone;
    }

    @Override
    public String toString() {
        return "SM2Node{name='" + name + "', parent=" + (parent != null ? parent.name() : "<NO PARENT>") + ", isBone=" + isBone() + '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        SM2Node that = (SM2Node) obj;
        return isBone() == that.isBone() &&
            Objects.equals(name, that.name) &&
            Objects.equals(matrix, that.matrix) &&
            Objects.equals(inverseMatrix, that.inverseMatrix) &&
            Objects.equals(parent, that.parent) &&
            Objects.equals(children, that.children);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, matrix, inverseMatrix, parent, children, isBone());
    }

    public int getId() {
        return id;
    }
}
