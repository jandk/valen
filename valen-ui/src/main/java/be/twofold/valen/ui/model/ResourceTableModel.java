package be.twofold.valen.ui.model;

import be.twofold.valen.resource.*;

import javax.swing.table.*;
import java.util.*;

public final class ResourceTableModel extends AbstractTableModel {
    private List<Resource> data = List.of();

    public void setData(List<Resource> data) {
        this.data = List.copyOf(data);
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return switch (columnIndex) {
            case 0, 1 -> String.class;
            case 2, 3 -> Integer.class;
            default -> null;
        };
    }

    @Override
    public int getColumnCount() {
        return 4;
    }

    @Override
    public String getColumnName(int column) {
        return switch (column) {
            case 0 -> "Name";
            case 1 -> "Type";
            case 2 -> "Compressed";
            case 3 -> "Uncompressed";
            default -> null;
        };
    }

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        var resource = data.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> resource.nameString();
            case 1 -> resource.type().name();
            case 2 -> resource.compressedSize();
            case 3 -> resource.uncompressedSize();
            default -> null;
        };
    }
}
