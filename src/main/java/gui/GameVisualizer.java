package gui;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Визуальный компонент для отображения состояния игры на основе Swing.
 * Панель отрисовывает текущее положение робота (заполненный овал)
 * и цели (контур овала). Автоматически обновляет отображение при изменении
 * состояния модели через механизм {@link PropertyChangeListener}.
 */
public class GameVisualizer extends JPanel {

    private final RobotModel model;
    /**
     * Создаёт экземпляр визуализатора, связанный с указанной моделью.
     * Автоматически регистрирует анонимный слушатель изменений свойств модели,
     * который вызывает {@link #repaint()} при любом изменении,
     * обеспечивая актуальность графического отображения.
     */
    public GameVisualizer(RobotModel model) {
        this.model = model;
        model.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                repaint();
            }
        });
    }

    /**
     * Отрисовывает игровые объекты: робота и цель.
     * Робот отображается как заполненный круг диаметром 10 пикселей,
     * цель — как контурный круг того же размера. Координаты берутся
     * из модели, при этом левый верхний угол ограничивающего прямоугольника
     * вычисляется как {@code (coordinate - 5)}, чтобы переданные координаты
     * соответствовали центру фигуры
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.fillOval((int) (model.getRobotX() - 5), (int) (model.getRobotY() - 5), 10, 10);
        g2d.drawOval(model.getTargetX() - 5, model.getTargetY() - 5, 10, 10);
    }

}