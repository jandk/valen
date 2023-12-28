package be.twofold.valen;

import be.twofold.valen.ui.*;
import com.formdev.flatlaf.*;
import com.formdev.flatlaf.extras.*;

import javax.swing.*;

public final class Main {

    public static void main(String[] args) throws InterruptedException {
        FlatLightLaf.setup();
        FlatInspector.install("ctrl shift alt X");
        FlatUIDefaultsInspector.install("ctrl shift alt Y");

        SwingUtilities.invokeLater(() -> {
            var frame = new MainFrame();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

}
