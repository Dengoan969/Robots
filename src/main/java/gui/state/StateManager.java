package gui.state;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

import java.io.File;
import java.io.IOException;

public class StateManager {
    private static final String APP_STATE_FILE = "windowsState.yaml";

    private final ObjectMapper mapper = new YAMLMapper();

    public WindowsState getState() {
        var file = new File(APP_STATE_FILE);

        if (!file.exists()) {
            return new WindowsState();
        }

        try {
            return mapper.readValue(file, WindowsState.class);
        } catch (IOException e) {
            System.err.println("Error when getting state:\n" + e.getMessage());
            return new WindowsState();
        }
    }

    public void setState(WindowsState state) {
        try {
            mapper.writeValue(new File(APP_STATE_FILE), state);
        } catch (IOException e) {
            System.err.println("Error when setting state:\n" + e.getMessage());
        }
    }
}
