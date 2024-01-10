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
            case 0 -> resource.name().name();
            case 1 -> resource.type().name();
            case 2 -> formatFileSize(resource.size());
            case 3 -> formatFileSize(resource.uncompressedSize());
            default -> null;
        };
    }

    private String formatFileSize(long size) {
        if (size < 1024) {
            return size + " B";
        } else if (size < 1024 * 1024) {
            return String.format("%.2f KB", size / 1024.0);
        } else if (size < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", size / (1024.0 * 1024.0));
        } else {
            return String.format("%.2f GB", size / (1024.0 * 1024.0 * 1024.0));
        }
    }
}
