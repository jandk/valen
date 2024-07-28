package be.twofold.valen.ui;

import be.twofold.valen.resource.*;
import be.twofold.valen.ui.model.*;
import jakarta.inject.*;
import javafx.beans.property.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;

import java.nio.*;
import java.util.*;

public final class MainViewFx extends BorderPane implements MainView {
    private final ListenerHelper<MainViewListener> listeners
        = new ListenerHelper<>(MainViewListener.class);

    private final SplitPane splitPane = new SplitPane();
    private final TreeView<String> treeView = new TreeView<>();
    private final TableView<Resource> tableView = new TableView<>();
    private final ImageViewerPane imageViewerPane = new ImageViewerPane();

    @Inject
    public MainViewFx() {
        buildUI();
    }

    @Override
    public Parent getView() {
        return this;
    }

    @Override
    public void setFileTree(TreeItem<String> root) {
        treeView.setRoot(root);
    }

    @Override
    public void setResources(List<Resource> resources) {
        tableView.getItems().setAll(resources);
    }

    @Override
    public void setImage(byte[] bgra, int width, int height) {
        var pixelBuffer = new PixelBuffer<>(width, height,
            ByteBuffer.wrap(bgra), PixelFormat.getByteBgraPreInstance());
        WritableImage image = new WritableImage(pixelBuffer);
        imageViewerPane.setSourceImage(image);
    }

    @Override
    public void addListener(MainViewListener listener) {
        listeners.addListener(listener);
    }

    public void togglePreview() {
        var positions = splitPane.getDividerPositions();
        switch (splitPane.getItems().size()) {
            case 3 -> {
                splitPane.getItems().remove(2);
                splitPane.setDividerPositions(positions[0]);
            }
            case 2 -> {
                splitPane.getItems().add(imageViewerPane);
                splitPane.setDividerPositions(positions[0], 0.75);
            }
            default -> throw new IllegalStateException("Unexpected number of items: " + splitPane.getItems().size());
        }
    }

    private void buildUI() {
        setPrefSize(900, 600);
        setTop(buildMenu());
        setCenter(buildMainContent());
        setBottom(buildStatusBar());
//            getChildren().addAll(
//                buildMenu(),
//                buildMainContent(),
//                buildStatusBar()
//            );
    }

    private SplitPane buildMainContent() {
        splitPane.setDividerPositions(0.25);
        splitPane.getItems().addAll(
            buildTreeView(),
            buildTableView()
        );
        VBox.setVgrow(splitPane, Priority.ALWAYS);
        return splitPane;
    }

    private TreeView<String> buildTreeView() {
        treeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                List<String> path = new ArrayList<>();
                for (var item = newValue; item != null; item = item.getParent()) {
                    path.add(item.getValue());
                }
                Collections.reverse(path);
                listeners.fire().onPathSelected(String.join("/", path.subList(1, path.size())));
            }
        });
        treeView.setShowRoot(false);
        return treeView;
    }

    private TableView<Resource> buildTableView() {
        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                listeners.fire().onResourceSelected(newValue);
            }
        });
        TableColumn<Resource, String> nameColumn = new TableColumn<>();
        nameColumn.setText("Name");
        nameColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().name().file()));

        TableColumn<Resource, String> typeColumn = new TableColumn<>();
        typeColumn.setText("Type");
        typeColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().type().name()));

        TableColumn<Resource, Size> compressedColumn = new TableColumn<>();
        compressedColumn.setText("Compressed");
        compressedColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(new Size(param.getValue().compressedSize())));

        TableColumn<Resource, Size> uncompressedColumn = new TableColumn<>();
        uncompressedColumn.setText("Uncompressed");
        uncompressedColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(new Size(param.getValue().uncompressedSize())));

        tableView.getColumns().addAll(nameColumn, typeColumn, compressedColumn, uncompressedColumn);
        return tableView;
    }

    private HBox buildStatusBar() {
        var leftStatus = new Label("Left status");
        leftStatus.setTextFill(Color.color(0.625, 0.625, 0.625));
        HBox.setHgrow(leftStatus, Priority.ALWAYS);

        var pane = new Pane();
        HBox.setHgrow(pane, Priority.ALWAYS);

        var rightStatus = new Label("Right status");
        rightStatus.setTextFill(Color.color(0.625, 0.625, 0.625));
        HBox.setHgrow(rightStatus, Priority.NEVER);

        var hBox = new HBox(leftStatus, pane, rightStatus);
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.setSpacing(5.0);
        hBox.setPadding(new Insets(3.0));
        return hBox;
    }

    private Menu buildMenuFile() {
        var menuFileNew = new MenuItem("New");
        var menuFileOpen = new MenuItem("Open");
        var menuFileOpenRecent = new Menu("Open Recent");

        var menuFileClose = new MenuItem("Close");
        var menuFileSave = new MenuItem("Save");
        var menuFileSaveAs = new MenuItem("Save As");
        var menuFileRevert = new MenuItem("Revert");

        var menuFilePreferences = new MenuItem("Preferences");

        var menuFileQuit = new MenuItem("Quit");

        var menuFile = new Menu("File");
        menuFile.getItems().addAll(
            menuFileNew,
            menuFileOpen,
            menuFileOpenRecent,
            new SeparatorMenuItem(),
            menuFileClose,
            menuFileSave,
            menuFileSaveAs,
            menuFileRevert,
            new SeparatorMenuItem(),
            menuFilePreferences,
            new SeparatorMenuItem(),
            menuFileQuit
        );
        return menuFile;
    }

    private Menu buildMenuEdit() {
        var menuEditUndo = new MenuItem("Undo");
        var menuEditRedo = new MenuItem("Redo");
        var menuEditCut = new MenuItem("Cut");
        var menuEditCopy = new MenuItem("Copy");
        var menuEditPaste = new MenuItem("Paste");
        var menuEditDelete = new MenuItem("Delete");
        var menuEditSelectAll = new MenuItem("Select All");
        var menuEditUnselectAll = new MenuItem("Unselect All");

        var menuEdit = new Menu("Edit");
        menuEdit.getItems().addAll(
            menuEditUndo,
            menuEditRedo,
            new SeparatorMenuItem(),
            menuEditCut,
            menuEditCopy,
            menuEditPaste,
            menuEditDelete,
            new SeparatorMenuItem(),
            menuEditSelectAll,
            menuEditUnselectAll
        );
        return menuEdit;
    }

    private Menu buildMenuView() {
        var menuView = new Menu("View");
        var menuViewPreview = new CheckMenuItem("Preview");
        menuViewPreview.onActionProperty().set(event -> togglePreview());
        menuView.getItems().addAll(menuViewPreview);
        return menuView;
    }

    private Menu buildMenuHelp() {
        var menuHelpAbout = new MenuItem("About");
        var menuHelp = new Menu("Help");
        menuHelp.getItems().add(menuHelpAbout);
        return menuHelp;
    }

    private MenuBar buildMenu() {
        var menuBar = new MenuBar();
        menuBar.getMenus().addAll(
            buildMenuFile(),
            buildMenuEdit(),
            buildMenuView(),
            buildMenuHelp()
        );
        return menuBar;
    }
}
