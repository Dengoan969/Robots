package gui;

import java.util.HashMap;
import java.util.Map;

public class WindowsState {
    private Map<String, WindowState> windows;

    public WindowsState() {
        windows = new HashMap<>();
    }

    public Map<String, WindowState> getWindows() {
        return windows;
    }

    public void setWindows(Map<String, WindowState> windows) {
        this.windows = windows;
    }
}
