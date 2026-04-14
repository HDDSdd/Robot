package Windows;

import gui.Controller;
import gui.GameVisualizer;
import gui.RobotModel;
import log.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;

public class MainApplicationFrame extends JFrame implements StateWindows {
    private final JDesktopPane desktopPane = new JDesktopPane();
    private StateProcessing stateProcessing;
    private LogWindow logWindow;
    private GameWindow gameWindow;
    private InfoWindow infoWindow;

    /**
     * Создаёт главное окно с менеджером состояния.
     * @param stateProcessing менеджер для сохранения/восстановления состояния
     */
    public MainApplicationFrame(StateProcessing stateProcessing) {
        this.stateProcessing = stateProcessing;

        RobotModel model = new RobotModel();
        GameVisualizer view = new GameVisualizer(model);
        Controller controller = new Controller(model, view);


        stateProcessing.CreateStateMap(this);

        int inset = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset,
                screenSize.width - inset * 2,
                screenSize.height - inset * 2);
        setContentPane(desktopPane);

        logWindow = createLogWindow();
        stateProcessing.CreateStateMap(logWindow);
        addWindow(logWindow);

        RobotModel robotModel = new RobotModel();

        gameWindow = new GameWindow(robotModel);
        stateProcessing.CreateStateMap(gameWindow);
        gameWindow.setSize(400, 400);
        addWindow(gameWindow);

        infoWindow = new InfoWindow(robotModel);
        stateProcessing.CreateStateMap(infoWindow);
        infoWindow.setSize(320, 180);
        addWindow(infoWindow);

        stateProcessing.restoreAllStates();
        setJMenuBar(generateMenuBar());
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                confirmAndExit();
            }
        });
    }

    /**
     * Метод обработки запроса на выход из приложения.
     * Содержит диалог подтверждения и логику завершения.
     * Вызывается в одном месте для унификации логики.
     */
    private void confirmAndExit() {
        UIManager.put("OptionPane.yesButtonText", "Да");
        UIManager.put("OptionPane.noButtonText", "Нет");

        int result = JOptionPane.showConfirmDialog(
                this,
                "Вы действительно хотите выйти из приложения?",
                "Подтверждение выхода",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (result == JOptionPane.YES_OPTION) {
            stateProcessing.SaveStateMap();
            System.exit(0);
        }
    }

    protected LogWindow createLogWindow() {
        LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource());
        logWindow.setLocation(10, 10);
        logWindow.setSize(300, 800);
        setMinimumSize(logWindow.getSize());
        logWindow.pack();
        Logger.debug("Протокол работает");
        return logWindow;
    }

    protected void addWindow(JInternalFrame frame) {
        desktopPane.add(frame);
        frame.setVisible(true);
    }

    /**
     * Собирает главную панель меню из отдельных подменю.
     */
    private JMenuBar generateMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(createFileMenu());
        menuBar.add(createLookAndFeelMenu());
        menuBar.add(createTestMenu());

        return menuBar;
    }

    /**
     * Создает меню "Файл" с пунктом выхода.
     */
    private JMenu createFileMenu() {
        JMenu fileMenu = new JMenu("Файл");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        JMenuItem exitItem = new JMenuItem("Выход", KeyEvent.VK_Q);
        exitItem.getAccessibleContext().setAccessibleDescription("Закрыть приложение");
        exitItem.addActionListener((event) -> {
            Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(
                    new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
        });

        fileMenu.add(exitItem);
        return fileMenu;
    }

    /**
     * Создает меню выбора темы оформления.
     */
    private JMenu createLookAndFeelMenu() {
        JMenu lookAndFeelMenu = new JMenu("Режим отображения");
        lookAndFeelMenu.setMnemonic(KeyEvent.VK_V);
        lookAndFeelMenu.getAccessibleContext().setAccessibleDescription("Управление режимом отображения приложения");
        JMenuItem systemLookAndFeel = new JMenuItem("Системная схема", KeyEvent.VK_S);
        systemLookAndFeel.addActionListener((event) -> {
            setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            this.invalidate();
        });
        lookAndFeelMenu.add(systemLookAndFeel);
        JMenuItem crossplatformLookAndFeel = new JMenuItem("Универсальная схема", KeyEvent.VK_U);
        crossplatformLookAndFeel.addActionListener((event) -> {
            setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            this.invalidate();
        });
        lookAndFeelMenu.add(crossplatformLookAndFeel);
        return lookAndFeelMenu;
    }

    /**
     * Создает меню с тестовыми командами.
     */
    private JMenu createTestMenu() {
        JMenu testMenu = new JMenu("Тесты");
        testMenu.setMnemonic(KeyEvent.VK_T);
        testMenu.getAccessibleContext().setAccessibleDescription("Тестовые команды");
        JMenuItem addLogMessageItem = new JMenuItem("Сообщение в лог", KeyEvent.VK_L);
        JMenuItem newAddLogMessageItem = new JMenuItem("Новое сообщение в лог", KeyEvent.VK_Q);
        addLogMessageItem.addActionListener((event) -> {
            Logger.debug("Новая строка");
        });
        newAddLogMessageItem.addActionListener((event) -> {
            Logger.debug("Самая новая строка");
        });
        testMenu.add(addLogMessageItem);
        testMenu.add(newAddLogMessageItem);
        return testMenu;
    }

    private void setLookAndFeel(String className) {
        try {
            UIManager.setLookAndFeel(className);
            SwingUtilities.updateComponentTreeUI(this);
        } catch (ClassNotFoundException | InstantiationException
                 | IllegalAccessException | UnsupportedLookAndFeelException e) {
            // just ignore
            Logger.debug("Не удалось установить схему оформления: " + className);
        }
    }

    @Override
    public String prefix() {
        return "Main";
    }

    @Override
    public Map<String, String> stateSave() {
        Map<String, String> state = new HashMap<>();
        state.put("x", String.valueOf(getX()));
        state.put("y", String.valueOf(getY()));
        state.put("width", String.valueOf(getWidth()));
        state.put("height", String.valueOf(getHeight()));
        state.put("extendedState", String.valueOf(getExtendedState()));
        return state;
    }

}
