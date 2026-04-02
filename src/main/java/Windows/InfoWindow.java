package Windows;

import gui.RobotModel;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.util.HashMap;
import java.util.Map;

/**
 * Внутреннее окно, отображающее текущие числовые параметры модели робота.
 * Окно подписывается на изменения и обновляет поля с координатами, углом
 * поворота и углом до цели при каждом изменении состояния модели
 */
public class InfoWindow extends JInternalFrame implements StateWindows {
    private final RobotModel model;
    private final JLabel robotXValue = new JLabel();
    private final JLabel robotYValue = new JLabel();
    private final JLabel robotDirValue = new JLabel();
    private final JLabel angleToTargetValue = new JLabel();

    /**
     * Создаёт окно, привязанное к переданной модели робота
     */
    public InfoWindow(RobotModel model) {
        super("Окно координат", true, true, true, true);
        this.model = model;

        setLayout(new BorderLayout());
        add(createContentPanel(), BorderLayout.CENTER);
        pack();

        this.model.addPropertyChangeListener(this::onModelChanged);
        refreshValues();
    }

    /**
     * Создаёт панель с подписями и значениями параметров модели.
     * Возвращает панель, размещающая отображаемые поля в виде таблицы 4x2
     */
    private JPanel createContentPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 2));
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        panel.add(new JLabel("X:"));
        panel.add(robotXValue);
        panel.add(new JLabel("Y:"));
        panel.add(robotYValue);
        panel.add(new JLabel("Угол поворота:"));
        panel.add(robotDirValue);
        panel.add(new JLabel("Угол до цели:"));
        panel.add(angleToTargetValue);

        return panel;
    }
    /**
     * Обрабатывает уведомление об изменении модели.
     */
    private void onModelChanged(PropertyChangeEvent event) {
        SwingUtilities.invokeLater(this::refreshValues);
    }

    /**
     * Перечитывает значения из модели и обновляет подписи в окне.
     */
    private void refreshValues() {
        robotXValue.setText(format(model.getRobotX()));
        robotYValue.setText(format(model.getRobotY()));
        robotDirValue.setText(format(model.getRobotDir()));
        angleToTargetValue.setText(format(model.getAngleToTarget()));
    }

    /**
     * Форматирует число для компактного отображения в интерфейсе.
     * Возвращает строку с четырьмя знаками после запятой
     */
    private String format(double value) {
        return String.format("%.4f", value);
    }

    @Override
    public String prefix() {
        return "information";
    }

    @Override
    public Map<String, String> stateSave() {
        Map<String, String> state = new HashMap<>();
        state.put("x", String.valueOf(getX()));
        state.put("y", String.valueOf(getY()));
        state.put("width", String.valueOf(getWidth()));
        state.put("height", String.valueOf(getHeight()));
        state.put("extendedState", String.valueOf(isIcon()));
        return state;
    }
}
