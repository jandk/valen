package be.twofold.valen;

import be.twofold.valen.ui.*;
import javafx.application.*;
import javafx.scene.*;
import javafx.stage.*;

public final class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Scene scene = new Scene(
            new MainWindow(primaryStage),
            1024, 768
        );
        primaryStage.setScene(scene);
        primaryStage.show();
    }

}
