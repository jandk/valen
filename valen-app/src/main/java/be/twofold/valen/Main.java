package be.twofold.valen;

import be.twofold.valen.reader.*;
import be.twofold.valen.ui.*;
import be.twofold.valen.ui.settings.*;
import com.formdev.flatlaf.*;
import com.formdev.flatlaf.extras.*;

import javax.swing.*;
import javax.swing.filechooser.*;
import java.io.*;
import java.nio.file.*;

public final class Main {

    public static void main(String[] args) {
        // System.setProperty("sun.java2d.uiScale", "2");
        // System.setProperty("flatlaf.uiScale", "1.5");

        FlatLightLaf.setup();
        FlatInspector.install("ctrl shift alt X");
        FlatUIDefaultsInspector.install("ctrl shift alt Y");

        SwingUtilities.invokeLater(() -> {
            if (SettingsManager.get().getGameDirectory().isEmpty()) {
                var fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Select the game executable (DoomEternalx64vk.exe)");
                fileChooser.setFileFilter(new FileNameExtensionFilter("Game executable", "exe"));
                if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    Path path = fileChooser.getSelectedFile().toPath();
                    SettingsManager.get().setGameDirectory(path.getParent());
                }
            }

            try {
                var manager = DaggerManagerFactory.create().fileManager();
                manager.load(SettingsManager.get().getGameDirectory().get().resolve("base"));
                manager.select("common");

                var presenter = DaggerPresenterFactory.create().presenter();
                presenter.show();
                presenter.setResources(manager.getEntries());
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        });
    }
}
