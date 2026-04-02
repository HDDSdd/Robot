package gui;

import Windows.MainApplicationFrame;
import Windows.StateProcessing;

import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class RobotsProgram {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
//        UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
//        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//        UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        StateProcessing stateManager = new StateProcessing();

        SwingUtilities.invokeLater(() -> {
            MainApplicationFrame frame = new MainApplicationFrame(stateManager);
            frame.setVisible(true);
        });
    }
}

