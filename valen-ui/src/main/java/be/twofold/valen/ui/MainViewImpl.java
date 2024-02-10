package be.twofold.valen.ui;

import be.twofold.valen.resource.*;
import be.twofold.valen.ui.model.*;
import jakarta.inject.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.tree.*;
import java.awt.*;
import java.util.List;
import java.util.stream.*;

public class MainViewImpl extends JFrame implements MainView {

    private final ListenerHelper<MainViewListener> listeners
        = new ListenerHelper<>(MainViewListener.class);

    private final DefaultTreeModel treeModel = new DefaultTreeModel(new DefaultMutableTreeNode("root"));
    private final ResourceTableModel tableModel = new ResourceTableModel();

    @Inject
    public MainViewImpl() {
        build();
    }

    @Override
    public void setFileTree(TreeNode root) {
        treeModel.setRoot(root);
    }

    @Override
    public void setResources(List<Resource> resources) {
        tableModel.setData(resources);
        tableModel.fireTableDataChanged();
    }

    @Override
    public void addListener(MainViewListener listener) {
        listeners.addListener(listener);
    }

    // region UI

    private void build() {
        setSize(1600, 1000);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        var tree = buildTree(treeModel);
        tree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                var path = IntStream.range(1, e.getPath().getPathCount())
                    .mapToObj(i -> e.getPath().getPathComponent(i).toString())
                    .collect(Collectors.joining("/"));

                listeners.fire().onPathSelected(path);
            }
        });

        var table = buildTable(tableModel);
        buildMenu();

        var leftSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        leftSplit.setTopComponent(new JScrollPane(tree));

        var rightSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        rightSplit.setLeftComponent(new JScrollPane(table));
        rightSplit.setRightComponent(null);

        var mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mainSplit.setLeftComponent(leftSplit);
        mainSplit.setRightComponent(rightSplit);
        add(mainSplit, BorderLayout.CENTER);

        SwingUtilities.invokeLater(() -> {
            mainSplit.setDividerLocation(0.25);
        });
    }

    public JTree buildTree(TreeModel treeModel) {
        return new JTree(treeModel);
    }

    private JTable buildTable(ResourceTableModel tableModel) {
        var tableView = new JTable(tableModel);
        tableView.getColumnModel().getColumn(0).setPreferredWidth(800);
        tableView.getColumnModel().getColumn(1).setMinWidth(200);
        tableView.getColumnModel().getColumn(1).setMaxWidth(200);
        tableView.getColumnModel().getColumn(2).setMinWidth(200);
        tableView.getColumnModel().getColumn(2).setMaxWidth(200);
        tableView.getColumnModel().getColumn(2).setCellRenderer(new FileSizeTableCellRenderer());
        tableView.getColumnModel().getColumn(3).setMinWidth(200);
        tableView.getColumnModel().getColumn(3).setMaxWidth(200);
        tableView.getColumnModel().getColumn(3).setCellRenderer(new FileSizeTableCellRenderer());
        tableView.setRowSorter(new TableRowSorter<>(tableModel));
        return tableView;
    }

    private void buildMenu() {
        var openItem = new JMenuItem("Exit");
        openItem.addActionListener(e -> System.exit(0));

        var fileMenu = new JMenu("File");
        fileMenu.add(openItem);

        var menuBar = new JMenuBar();
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);
    }

    private static class FileSizeTableCellRenderer extends DefaultTableCellRenderer {
        FileSizeTableCellRenderer() {
            setHorizontalAlignment(SwingConstants.RIGHT);
        }

        @Override
        protected void setValue(Object value) {
            if (!(value instanceof Number number)) {
                super.setValue(value);
                return;
            }

            setText(formatFileSize(number.longValue()));
        }

        private String formatFileSize(long size) {
            if (size < 1024) {
                return size + " B";
            } else if (size < 1024 * 1024) {
                return String.format("%.2f KiB", size / 1024.0);
            } else if (size < 1024 * 1024 * 1024) {
                return String.format("%.2f MiB", size / (1024.0 * 1024.0));
            } else {
                return String.format("%.2f GiB", size / (1024.0 * 1024.0 * 1024.0));
            }
        }
    }

    // endregion

}
