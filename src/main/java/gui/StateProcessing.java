package gui;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Менеджер состояния приложения.
 * Отвечает за сохранение и восстановление настроек всех окон в файл конфигурации
 */
public class StateProcessing {
    private final String config;
    private final List<StateWindows> states = new ArrayList<>();
    public StateProcessing() {
        this.config = System.getProperty("user.home") + "/state.cfg";
    }
    /**
     * Записывает словарь состояния в файл конфигурации.
     */
    public void WriteToFile(Map<String, String> stateSave) {
        try {
            File file = new File(config);
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                boolean created = parentDir.mkdirs();
            }

            try (FileWriter writer = new FileWriter(config, false)) {
                for (Map.Entry<String, String> item : stateSave.entrySet()) {
                    writer.write(item.getKey() + "-" + item.getValue() + "\n");
                }
                writer.flush();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * Читает словарь состояния из файла конфигурации.
     */
    public Map<String, String> ReadFromFile() {
        Map<String, String> stateSave = new HashMap<>();
        File file = new File(config);

        if (!file.exists()) {
            return stateSave;
        }

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            String s;
            while ((s = bufferedReader.readLine()) != null) {
                if (s.trim().isEmpty()) {
                    continue;
                }
                String[] parts = s.split("-", 2);
                if (parts.length >= 2) {
                    stateSave.put(parts[0], parts[1]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stateSave;
    }
    /**
     * Регистрирует компонент для участия в сохранении состояния.
     */
    public void CreateStateMap(StateWindows stateWindows) {
        states.add(stateWindows);
    }
    /**
     * Сохраняет состояния всех зарегистрированных компонентов в файл.
     * Вызывается при закрытии приложения.
     */
    public void SaveStateMap() {
        Map<String, String> globalStateMap = new HashMap<>();
        for (StateWindows stateWindows : states) {
            Map<String,String> componentState = stateWindows.stateSave();
            String prefix = stateWindows.prefix() + ".";
            for (Map.Entry<String, String> entry : componentState.entrySet()) {
                globalStateMap.put(prefix + entry.getKey(), entry.getValue());
            }
        }
        WriteToFile(globalStateMap);
    }
    /**
     * Восстанавливает состояния всех зарегистрированных компонентов из файла.
     * Вызывается при запуске приложения.
     */
    public void restoreAllStates() {
        Map<String, String> globalState = ReadFromFile();
        for (StateWindows component : states) {
            String prefix = component.prefix() + ".";
            Map<String, String> componentState = filterByPrefix(globalState, prefix);
            component.restoreState(componentState);
        }
    }
    /**
     * Фильтрует глобальный словарь состояния, извлекая записи, принадлежащие конкретному компоненту.
     * Метод просматривает все записи и выбирает только те, ключи которых
     * начинаются с указанного. Из выбранных ключей префикс удаляется,
     */
    private Map<String, String> filterByPrefix(Map<String, String> global, String prefix) {
        Map<String, String> filtered = new HashMap<>();
        for (Map.Entry<String, String> entry : global.entrySet()) {
            if (entry.getKey().startsWith(prefix)) {
                filtered.put(
                        entry.getKey().substring(prefix.length()),
                        entry.getValue()
                );
            }
        }
        return filtered;
    }

    }
