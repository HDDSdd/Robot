package gui;

import java.util.Map;
/**
 * Интерфейс для компонентов, способных сохранять и восстанавливать своё состояние.
 */

public interface StateWindows {
    String prefix();
    Map<String, String> stateSave();
    void restoreState(Map<String, String> stateSave);
}
