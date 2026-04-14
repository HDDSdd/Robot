package gui;

import log.Logger;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Контроллер игровой логики, реализующий паттерн MVC.
 * Отвечает за два ключевых аспекта:
 * Игровой цикл: запускает периодический {@link Timer}, который
 * регулярно вызывает обновление состояния модели.
 * Обработка ввода: регистрирует слушатель кликов мыши на визуальном
 * компоненте и передаёт координаты цели в модель.</li>
 */
public class Controller {

    private static final int UPDATE_INTERVAL_MS = 3;
    /**
     * Создаёт контроллер и инициализирует игровую логику.
     * Мгновенно запускает таймер обновления и регистрирует обработчик
     * событий мыши на переданном представлении.
     * @param model модель игры, получающая команды обновления и целевые координаты.
     *              Не должен быть {@code null}.
     * @param view  визуальный компонент, на который навешивается слушатель мыши.
     *              Координаты клика передаются в локальной системе координат этого компонента.
     *              Не должен быть {@code null}.
     */
    public Controller(RobotModel model, GameVisualizer view) {

        Timer timer = new Timer(UPDATE_INTERVAL_MS, e ->
                model.update(UPDATE_INTERVAL_MS)
        );
        timer.start();

        view.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Logger.info("Цель: x=" + e.getX() + ", y=" + e.getY());
                model.setTargetPosition(e.getPoint());
            }
        });
    }
}