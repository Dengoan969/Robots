package gui.state;

import java.util.HashMap;
import java.util.Map;

public class WindowsState {
    private Map<String, WindowState> windowsStates;

    public WindowsState() {
        windowsStates = new HashMap<>();
    }

    public Map<String, WindowState> getWindowsStates() {
        return windowsStates;
    }

    public void setWindowsStates(Map<String, WindowState> windowsStates) {
        this.windowsStates = windowsStates;
    }
}
