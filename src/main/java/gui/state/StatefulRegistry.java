package gui.state;

import javax.swing.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class StatefulRegistry {
    private final Map<String, Stateful> windowsById = new HashMap<>();

    private final Map<String, Integer> windowsCounter = new HashMap<>();

    public void registerIfStateful(JInternalFrame frame) {
        if (isStateful(frame)) {
            var stateful = (Stateful) frame;

            var name = stateful.getName();
            var id = generateId(name);
            windowsById.put(id, stateful);
        }
    }

    public Map<String, Stateful> getStatefulWindows() {
        return Collections.unmodifiableMap(windowsById);
    }

    private String generateId(String windowName) {
        var count = windowsCounter.getOrDefault(windowName, 0);
        windowsCounter.put(windowName, count + 1);

        return windowName + "-" + count;
    }

    private boolean isStateful(JInternalFrame frame) {
        for (var implementedInterface : frame.getClass().getInterfaces())
            if (implementedInterface == Stateful.class)
                return true;

        return false;
    }
}
