package be.twofold.valen.ui;

import be.twofold.valen.ui.model.*;

import javax.inject.*;
import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;

public class MainViewImpl extends JFrame implements MainView {

    private final DefaultTreeModel treeModel;

    @Inject
    public MainViewImpl() {
        setSize(1024, 768);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        this.treeModel = new DefaultTreeModel(new DefaultMutableTreeNode("root"));
        var tree = buildTree(treeModel);
        var table = buildTable();
        // buildMenu();

        var leftSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        leftSplit.setTopComponent(new JScrollPane(tree));

        var mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mainSplit.setLeftComponent(leftSplit);
        mainSplit.setRightComponent(new JScrollPane(table));
        add(mainSplit, BorderLayout.CENTER);

        SwingUtilities.invokeLater(() -> {
            mainSplit.setDividerLocation(0.25);
        });
    }

    public JTree buildTree(TreeModel treeModel) {
        return new JTree(treeModel);
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
        var openItem = new JMenuItem("Exit");
        openItem.addActionListener(e -> System.exit(0));

        var fileMenu = new JMenu("File");
        fileMenu.add(openItem);

        var menuBar = new JMenuBar();
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);
    }

    @Override
    public void setFileTree(TreeNode root) {
        treeModel.setRoot(root);
    }
}
