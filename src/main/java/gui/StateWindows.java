package gui;

import java.util.Map;

public interface StateWindows {
    String prefix();
    Map<String, String> stateSave();
    void restoreState(Map<String, String> stateSave);
}
