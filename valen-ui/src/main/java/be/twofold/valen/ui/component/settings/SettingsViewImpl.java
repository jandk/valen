package be.twofold.valen.ui.component.settings;

import be.twofold.valen.ui.common.*;
import be.twofold.valen.ui.common.settings.*;
import jakarta.inject.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.ScrollPane.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import org.jetbrains.annotations.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

@Singleton
public final class SettingsViewImpl extends AbstractView<SettingsView.Listener> implements SettingsView {

    private final VBox root = new VBox();
    private final VBox container = new VBox(20.0);

    // One per control, re-run after any change so settings can gate each other.
    private final List<Runnable> disableRefreshers = new ArrayList<>();

    @Inject
    public SettingsViewImpl() {
        buildUI();
    }

    @Override
    public Parent getFXNode() {
        return root;
    }

    @Override
    public void setDescriptors(SettingDescriptor<?, ?> @UnknownNullability ... descriptors) {
        container.getChildren().clear();
        disableRefreshers.clear();

        var grouped = Arrays.stream(descriptors)
            .collect(Collectors.groupingBy(
                SettingDescriptor::group,
                LinkedHashMap::new,
                Collectors.toList()
            ));

        for (var entry : grouped.entrySet()) {
            var group = entry.getKey();
            var groupDescriptors = entry.getValue();

            var title = new Label(group.displayName());
            title.setStyle("-fx-font-weight: bold; -fx-font-size: 1.2em;");
            container.getChildren().add(title);

            for (var descriptor : groupDescriptors) {
                var control = createControl(descriptor);

                // Disabling the wrapper greys the label and help text along with it.
                disableRefreshers.add(() -> control.setDisable(descriptor.disabled().getAsBoolean()));
                container.getChildren().add(control);
            }

            container.getChildren().add(new Separator());
        }

        if (!container.getChildren().isEmpty()) {
            container.getChildren().removeLast();
        }

        refreshDisabled();
    }

    private <T> void set(SettingDescriptor<T, ?> descriptor, T value) {
        descriptor.setter().accept(value);
        refreshDisabled();
    }

    private void refreshDisabled() {
        disableRefreshers.forEach(Runnable::run);
    }

    // region UI

    private void buildUI() {
        container.setStyle("-fx-border-style: none;");

        var scrollPane = new ScrollPane(container);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);
        scrollPane.setPadding(new Insets(10.0));
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        var saveButton = new Button("Save");
        saveButton.setMinWidth(80);
        saveButton.setOnAction(_ -> getListener().onSave());

        var buttonBar = new HBox(saveButton);
        VBox.setMargin(buttonBar, new Insets(10.0));

        root.getChildren().setAll(scrollPane, new Separator(), buttonBar);
    }

    // endregion

    // region Control Factory

    private Node createControl(SettingDescriptor<?, ?> descriptor) {
        return switch (descriptor.type()) {
            case BOOLEAN -> createBooleanControl((SettingDescriptor<Boolean, ?>) descriptor);
            case STRING -> createStringControl((SettingDescriptor<String, ?>) descriptor);
            case PATH -> createPathControl((SettingDescriptor<Path, ?>) descriptor);
            case MULTI_SINGLE -> createEnumControl((SettingDescriptor<Object, ?>) descriptor);
            case MULTI_MULTIPLE -> createMultiEnumControl((SettingDescriptor<Set<Object>, ?>) descriptor);
        };
    }

    private Node createBooleanControl(SettingDescriptor<Boolean, ?> descriptor) {
        CheckBox checkBox = new CheckBox(descriptor.label());
        checkBox.setSelected(descriptor.getter().get());
        checkBox.selectedProperty().addListener((_, _, newValue) -> set(descriptor, newValue));
        return wrapWithLabelAndHelp(checkBox, descriptor.label(), descriptor.helpText());
    }

    private Node createStringControl(SettingDescriptor<String, ?> descriptor) {
        TextField textField = new TextField();
        textField.setText(descriptor.getter().get());
        textField.textProperty().addListener((_, _, newValue) -> set(descriptor, newValue));
        return wrapWithLabelAndHelp(textField, descriptor.label(), descriptor.helpText());
    }

    private Node createPathControl(SettingDescriptor<Path, ?> descriptor) {
        Path path = descriptor.getter().get();

        TextField textField = new TextField();
        textField.setText(path.toString());
        textField.setEditable(false);

        Button browseButton = new Button("Browse...");
        browseButton.setOnAction(_ -> {
            DirectoryChooser chooser = new DirectoryChooser();
            if (Files.exists(path)) {
                chooser.setInitialDirectory(path.toFile());
            }
            Optional.ofNullable(chooser.showDialog(browseButton.getScene().getWindow()))
                .map(File::toPath)
                .ifPresent(newPath -> {
                    textField.setText(newPath.toString());
                    set(descriptor, newPath);
                });
        });

        HBox hBox = new HBox(5, textField, browseButton);
        HBox.setHgrow(textField, Priority.ALWAYS);
        return wrapWithLabelAndHelp(hBox, descriptor.label(), descriptor.helpText());
    }

    @SuppressWarnings("unchecked")
    private <O> Node createEnumControl(SettingDescriptor<Object, O> descriptor) {
        ComboBox<O> comboBox = new ComboBox<>();
        comboBox.getItems().setAll(descriptor.options());
        comboBox.setConverter(new FunctionalStringConverter<>((Function<O, String>) descriptor.labeler()));
        Object value = descriptor.getter().get();
        descriptor.options().stream()
            .filter(o -> {
                if (o instanceof Map.Entry<?, ?> entry) {
                    return entry.getKey().equals(value);
                }
                return Objects.toString(o).equals(value);
            })
            .findFirst()
            .ifPresent(comboBox.getSelectionModel()::select);

        comboBox.getSelectionModel().selectedItemProperty().addListener((_, _, newValue) -> {
            if (newValue != null) {
                if (newValue instanceof Map.Entry<?, ?> entry) {
                    set(descriptor, entry.getKey());
                } else {
                    set(descriptor, newValue.toString());
                }
            }
        });
        comboBox.setMaxWidth(Double.MAX_VALUE);
        return wrapWithLabelAndHelp(comboBox, descriptor.label(), descriptor.helpText());
    }

    private <O> Node createMultiEnumControl(SettingDescriptor<Set<Object>, O> descriptor) {
        VBox vBox = new VBox(5);
        Set<Object> currentValues = descriptor.getter().get();

        for (O option : descriptor.options()) {
            CheckBox checkBox = new CheckBox(descriptor.labeler().apply(option));
            checkBox.setSelected(currentValues.contains(option));
            checkBox.selectedProperty().addListener((_, _, newValue) -> {
                if (newValue) {
                    currentValues.add(option);
                } else {
                    currentValues.remove(option);
                }
                set(descriptor, new HashSet<>(currentValues));
            });
            vBox.getChildren().add(checkBox);
        }

        return wrapWithLabelAndHelp(vBox, descriptor.label(), descriptor.helpText());
    }

    private Node wrapWithLabelAndHelp(Node control, String labelText, String helpText) {
        VBox vBox = new VBox(5);
        if (labelText != null && !labelText.isBlank()) {
            vBox.getChildren().add(new Label(labelText));
        }
        if (helpText != null && !helpText.isBlank()) {
            Label helpLabel = new Label(helpText);
            helpLabel.setWrapText(true);
            helpLabel.setStyle("-fx-font-size: 0.9em; -fx-text-fill: gray;");
            vBox.getChildren().add(helpLabel);
        }
        vBox.getChildren().add(control);
        return vBox;
    }

    // endregion

}
