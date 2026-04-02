package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.beans.PropertyChangeEvent;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Панель визуализации, отображающая робота и его цель на игровом поле.
 * Класс подписывается на RobotModel, запускает периодическое
 * обновление модели и перерисовывается при любом её изменении
 */
public class GameVisualizer extends JPanel {
    private final Timer timer = initTimer();
    private final RobotModel model;

    /**
     * Создаёт фоновый таймер для периодического обновления модели.
     */
    private static Timer initTimer() {
        return new Timer("events generator", true);
    }

    /**
     * Создаёт визуализатор на основе переданной модели.
     */
    public GameVisualizer(RobotModel model) {
        this.model = model;

        this.model.addPropertyChangeListener(this::onModelChanged);

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                model.update(10);
            }
        }, 0, 10);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                model.setTargetPosition(e.getPoint());
            }
        });

        setDoubleBuffered(true);
    }
    /**
     * Возвращает модель, которая визуализируется на панели
     */
    public RobotModel getModel() {
        return model;
    }

    /**
     * Обрабатывает уведомление об изменении модели и запускает перерисовку.
     */
    private void onModelChanged(PropertyChangeEvent event) {
        EventQueue.invokeLater(this::repaint);
    }

    /**
     * Рисует текущее состояние игрового поля.
     */
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g.create();
        try {
            drawRobot(g2d, round(model.getRobotX()), round(model.getRobotY()), model.getRobotDir());
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

    /**
     * Округляет число до ближайшего целого в большую сторону.
     */
    private static int round(double value) {
        return (int) (value + 0.5);
    }
}
