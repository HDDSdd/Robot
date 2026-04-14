package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
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
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g.create();
        try {
            drawRobot(g2d, (int) model.getRobotX(), (int) model.getRobotY(), model.getRobotDir());
            drawTarget(g2d, model.getTargetX(), model.getTargetY());
        } finally {
            g2d.dispose();
        }
    }

    /**
     * Рисует тело робота и его ориентир.
     */
    private void drawRobot(Graphics2D g, int x, int y, double direction) {
        AffineTransform t = AffineTransform.getRotateInstance(direction, x, y);
        g.setTransform(t);

        g.setColor(Color.MAGENTA);
        fillOval(g, x, y, 30, 10);
        g.setColor(Color.BLACK);
        drawOval(g, x, y, 30, 10);
        g.setColor(Color.WHITE);
        fillOval(g, x + 10, y, 5, 5);
        g.setColor(Color.BLACK);
        drawOval(g, x + 10, y, 5, 5);
    }

    /**
     * Рисует цель на поле.
     */
    private void drawTarget(Graphics2D g, int x, int y) {
        g.setTransform(new AffineTransform());

        g.setColor(Color.GREEN);
        fillOval(g, x, y, 5, 5);
        g.setColor(Color.BLACK);
        drawOval(g, x, y, 5, 5);
    }

    /**
     * Рисует закрашенный овал, центрированный в указанной точке.
     */
    private static void fillOval(Graphics g, int centerX, int centerY, int diam1, int diam2) {
        g.fillOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }

    /**
     * Рисует контур овала, центрированного в указанной точке
     */
    private static void drawOval(Graphics g, int centerX, int centerY, int diam1, int diam2) {
        g.drawOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }

}