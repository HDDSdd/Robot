package Windows;

import gui.GameVisualizer;
import gui.RobotModel;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class GameWindow extends JInternalFrame implements StateWindows {
    private final GameVisualizer gameVisualizer;


    public GameWindow(RobotModel model) {
        super("Игровое поле", true, true, true, true);
        gameVisualizer = new GameVisualizer(model);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(gameVisualizer, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
    }

    @Override
    public String prefix() {
        return "Game";
    }

    @Override
    public Map<String, String> stateSave() {
        Map<String, String> state = new HashMap<>();
        state.put("x", String.valueOf(getX()));
        state.put("y", String.valueOf(getY()));
        state.put("width", String.valueOf(getWidth()));
        state.put("height", String.valueOf(getHeight()));
        state.put("isIcon", String.valueOf(isIcon()));
        return state;
    }
}
