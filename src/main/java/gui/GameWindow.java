package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

public class GameWindow extends JInternalFrame implements StateWindows
{
    private final GameVisualizer gameVisualizer;

    public GameWindow() 
    {
        super("Игровое поле", true, true, true, true);
        gameVisualizer = new GameVisualizer();
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(gameVisualizer, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
    }

    @Override
    public String prefix() {
        return "Game";
    }

    /**
     * Сохраняет: координаты, размеры, состояние свёрнутости.
     */
    @Override
    public Map<String, String> stateSave() {
        Map<String, String> state = new HashMap<>();
        state.put("x", String.valueOf(getX()));
        state.put("y", String.valueOf(getY()));
        state.put("width", String.valueOf(getWidth()));
        state.put("height", String.valueOf(getHeight()));
        state.put("isIcon", String.valueOf(isIcon()));
        state.put("isSelected", String.valueOf(isSelected()));
        return state;
    }
    /**
     * Восстанавливает: координаты и размеры.
     */
    @Override
    public void restoreState(Map<String, String> stateSave) {
        if (stateSave.containsKey("x")) {
            setBounds(
                    Integer.parseInt(stateSave.get("x")),
                    Integer.parseInt(stateSave.get("y")),
                    Integer.parseInt(stateSave.get("width")),
                    Integer.parseInt(stateSave.get("height"))
            );
        }
    }
}
