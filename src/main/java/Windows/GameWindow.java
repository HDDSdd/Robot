package Windows;

import gui.Controller;
import gui.GameVisualizer;
import gui.RobotModel;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Окно игрового поля, реализованное как внутреннее окно ({@code JInternalFrame}).
 * <p>
 * Выступает в роли View/Mediator в архитектуре приложения: объединяет модель
 * ({@link RobotModel}), компонент отрисовки ({@link GameVisualizer}) и логику
 * управления ({@link Controller}). Реализует интерфейс {@link StateWindows}
 */
public class GameWindow extends JInternalFrame implements StateWindows {

    /**
     * Создаёт и инициализирует игровое окно с привязкой к указанной модели.
     * Конфигурирует заголовок окна, разрешает изменение размера, закрытие,
     * разворачивание и сворачивание. Создаёт экземпляр визуализатора и контроллера,
     * связывает их с моделью и добавляет визуализатор в центральную область окна.
     */
    public GameWindow(RobotModel model) {
        super("Игровое поле", true, true, true, true);

        GameVisualizer gameVisualizer = new GameVisualizer(model);
        Controller controller = new Controller(model, gameVisualizer);

        setLayout(new BorderLayout());
        add(gameVisualizer, BorderLayout.CENTER);

        setSize(400, 400);
        setVisible(true);
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