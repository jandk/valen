package be.twofold.valen.ui.component.settings;

import be.twofold.valen.ui.common.*;
import be.twofold.valen.ui.common.settings.*;
import be.twofold.valen.ui.component.*;
import jakarta.inject.*;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import org.jetbrains.annotations.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

@Singleton
public final class SettingsController extends AbstractView<SettingsViewListener> implements SettingsView, Controller {

    private @FXML Parent root;
    private @FXML VBox container;

    @Inject
    public SettingsController() {
    }

    @Override
    public Parent getFXNode() {
        return root;
    }

    @FXML
    public void handleSave() {
        getListener().onSave();
    }

    @Override
    public void setDescriptors(SettingDescriptor<?, ?> @UnknownNullability ... descriptors) {
        container.getChildren().clear();

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
                container.getChildren().add(createControl(descriptor));
            }

            container.getChildren().add(new Separator());
        }

        if (!container.getChildren().isEmpty()) {
            container.getChildren().removeLast();
        }
    }

    // region UI Factory

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
        checkBox.selectedProperty().addListener((_, _, newValue) -> descriptor.setter().accept(newValue));
        return wrapWithLabelAndHelp(checkBox, descriptor.label(), descriptor.helpText());
    }

    private Node createStringControl(SettingDescriptor<String, ?> descriptor) {
        TextField textField = new TextField();
        textField.setText(descriptor.getter().get());
        textField.textProperty().addListener((_, _, newValue) -> descriptor.setter().accept(newValue));
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
                    descriptor.setter().accept(newPath);
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
                    descriptor.setter().accept(entry.getKey());
                } else {
                    descriptor.setter().accept(newValue.toString());
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
                descriptor.setter().accept(new HashSet<>(currentValues));
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
