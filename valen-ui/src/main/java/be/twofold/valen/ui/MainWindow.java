package be.twofold.valen.ui;

import be.twofold.valen.resource.*;
import be.twofold.valen.ui.task.*;
import javafx.beans.property.*;
import javafx.concurrent.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import javafx.util.*;

import java.io.*;
import java.nio.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;

public class MainWindow extends VBox {

    private final TreeView<String> treeView = new TreeView<>();
    private final TableView<Resource> tableView = new TableView<>();
    private final View3d view3d = new View3d();

    private final Window window;
    private Collection<Resource> entries;

    public MainWindow(Window window) {
        this.window = window;
        setupTreeView();
        setupTableView();

        Button openButton = new Button("Open");
        openButton.setOnAction(event -> open());

        ToolBar toolBar = new ToolBar();
        toolBar.getItems().add(openButton);

        SplitPane vSplit = new SplitPane();
        HBox.setHgrow(vSplit, Priority.ALWAYS);
        vSplit.setOrientation(Orientation.VERTICAL);
        vSplit.getItems().add(tableView);
        vSplit.getItems().add(view3d);
        vSplit.getDividers().getFirst().setPosition(0.5);

        SplitPane hSplit = new SplitPane();
        VBox.setVgrow(hSplit, Priority.ALWAYS);
        hSplit.getItems().add(treeView);
        hSplit.getItems().add(vSplit);
        hSplit.getDividers().getFirst().setPosition(0.25);

        Label statusLabel = new Label();
        statusLabel.setText("This will be some status text!");

        ToolBar statusBar = new ToolBar();
        statusBar.getItems().add(new ImageView(createImage()));
        statusBar.getItems().add(statusLabel);

        getChildren().addAll(
            toolBar,
            hSplit,
            statusBar
        );

        populateTree(Path.of("D:\\Games\\SteamLibrary\\steamapps\\common\\DOOMEternal\\base"));
    }

    private void setupTreeView() {
        treeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                loadItems(getFullPath(newValue));
            }
        });
    }

    private void setupTableView() {
        TableColumn<Resource, String> nameColumn = new TableColumn<>("Name");
        nameColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.5));
        nameColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().name().name()));

        TableColumn<Resource, String> typeColumn = new TableColumn<>("Type");
        typeColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.2));
        typeColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().type().name()));

        TableColumn<Resource, Long> sizeColumn = new TableColumn<>("Size");
        sizeColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.12));
        sizeColumn.setCellValueFactory(param -> new SimpleLongProperty(param.getValue().size()).asObject());
        sizeColumn.setCellFactory(param -> createFileSizeTableCell());

        TableColumn<Resource, Long> sizeUncompressedColumn = new TableColumn<>("Size (uncompressed)");
        sizeUncompressedColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.12));
        sizeUncompressedColumn.setCellValueFactory(param -> new SimpleLongProperty(param.getValue().uncompressedSize()).asObject());
        sizeUncompressedColumn.setCellFactory(param -> createFileSizeTableCell());

        tableView.getColumns().add(nameColumn);
        tableView.getColumns().add(typeColumn);
        tableView.getColumns().add(sizeColumn);
        tableView.getColumns().add(sizeUncompressedColumn);
    }

    private static TextFieldTableCell<Resource, Long> createFileSizeTableCell() {
        TextFieldTableCell<Resource, Long> tableCell = new TextFieldTableCell<>(FileSizeStringConverter.Instance);
        tableCell.setAlignment(Pos.CENTER_RIGHT);
        return tableCell;
    }

    private Image createImage() {
        int w = 32;
        int h = 32;
        byte[] pixels = new byte[w * h * 4];
        for (int i = 0; i < pixels.length; i += 4) {
            pixels[i + 2] = (byte) 0xFF;
            pixels[i + 3] = (byte) 0xFF;
        }

        ByteBuffer buffer = ByteBuffer.wrap(pixels);
        PixelBuffer<ByteBuffer> pixelBuffer = new PixelBuffer<>(w, h, buffer, PixelFormat.getByteBgraPreInstance());
        return new WritableImage(pixelBuffer);
    }

    private static String getFullPath(TreeItem<String> newValue) {
        List<String> parts = new ArrayList<>();
        TreeItem<String> item = newValue;
        while (item != null) {
            parts.addFirst(item.getValue());
            item = item.getParent();
        }
        return String.join("/", parts.subList(1, parts.size()));
    }

    private void loadItems(String path) {
        List<Resource> filteredEntries = entries.stream()
            .filter(entry -> entry.name().path().equals(path))
            .sorted(Comparator.comparing(e -> e.name().fullPath()))
            .toList();

        tableView.getItems().clear();
        tableView.getItems().addAll(filteredEntries);
    }

    private void open() {
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Resources", "*.resources");
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(filter);
        fileChooser.setSelectedExtensionFilter(filter);
        File file = fileChooser.showOpenDialog(window);
        if (file != null) {
            populateTree(file.toPath());
        }
    }

    private void populateTree(Path path) {
        LoadResourcesTask loadResourcesTask = new LoadResourcesTask(path);
        loadResourcesTask.setOnSucceeded(e1 -> {
            this.entries = loadResourcesTask.getValue();
            Task<TreeItem<String>> loadTreeTask = new LoadTreeTask(entries);
            loadTreeTask.setOnSucceeded(e2 -> {
                treeView.setRoot(loadTreeTask.getValue());
            });
            ForkJoinPool.commonPool().submit(loadTreeTask);
        });
        loadResourcesTask.setOnFailed(e1 -> {
            loadResourcesTask.getException().printStackTrace();
        });
        ForkJoinPool.commonPool().submit(loadResourcesTask);
    }

    private static final class FileSizeStringConverter extends StringConverter<Long> {
        private static final FileSizeStringConverter Instance = new FileSizeStringConverter();

        private FileSizeStringConverter() {
        }

        @Override
        public String toString(Long object) {
            if (object == null) {
                return "";
            }
            long bytes = object;
            if (bytes < 1024) {
                return bytes + " B";
            } else if (bytes < 1024 * 1024) {
                return String.format("%.2f KB", bytes / 1024.0);
            } else if (bytes < 1024 * 1024 * 1024) {
                return String.format("%.2f MB", bytes / (1024.0 * 1024.0));
            } else {
                return String.format("%.2f GB", bytes / (1024.0 * 1024.0 * 1024.0));
            }
        }

        @Override
        public Long fromString(String string) {
            throw new UnsupportedOperationException();
        }
    }
}
