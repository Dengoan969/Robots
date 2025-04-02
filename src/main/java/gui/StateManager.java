package gui;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;

import java.io.File;
import java.io.IOException;

public class StateManager {
    private static final String APP_STATE_FILE = "windowsState.yaml";

    private final ObjectMapper mapper = new ObjectMapper(new YAMLFactory()
            .disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER));

    public WindowsState getState() {
        try {
            return mapper.readValue(new File(APP_STATE_FILE), WindowsState.class);
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
