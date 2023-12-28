package be.twofold.valen.ui;

import be.twofold.valen.resource.*;
import be.twofold.valen.ui.experiment.*;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.tree.*;
import java.awt.*;
import java.util.List;

public class MainFrame extends JFrame {

    private final JTree tree;
    private final JTable table;

    public MainFrame() {
        setSize(1024, 768);
        setLayout(new BorderLayout());

        this.tree = buildTree();
        this.table = buildTable();
        buildMenu();

        var button = new PublishingButton();
        button.subscribe(action -> {
            System.out.println(Thread.currentThread() + ": Received");
        });

        var leftSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        leftSplit.setTopComponent(new JScrollPane(tree));
        leftSplit.setBottomComponent(button);

        var mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mainSplit.setLeftComponent(leftSplit);
        mainSplit.setRightComponent(new JScrollPane(table));
        add(mainSplit, BorderLayout.CENTER);

        SwingUtilities.invokeLater(() -> {
            mainSplit.setDividerLocation(0.25);
        });
    }

    public JTree buildTree() {
        var treeView = new JTree();
        treeView.setModel(new DefaultTreeModel(new DefaultMutableTreeNode("root")));
        return treeView;
    }

    private JTable buildTable() {
        var tableView = new JTable(new ResourceTableModel());
        tableView.getColumnModel().getColumn(0).setPreferredWidth(400);
        tableView.getColumnModel().getColumn(1).setPreferredWidth(100);
        tableView.getColumnModel().getColumn(2).setPreferredWidth(100);
        tableView.getColumnModel().getColumn(3).setPreferredWidth(100);
        return tableView;
    }

    private void buildMenu() {
        var openItem = new JMenuItem("Open");

        var fileMenu = new JMenu("File");
        fileMenu.add(openItem);

        var menuBar = new JMenuBar();
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);
    }

    private static final class ResourceTableModel extends AbstractTableModel {
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

}
