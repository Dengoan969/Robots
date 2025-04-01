package gui;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

import java.io.File;
import java.io.IOException;

public class StateManager {
    private final ObjectMapper mapper = new YAMLMapper();
    private static final String CONFIG_FILE = "config.yaml";

    public WindowsState getState() {
        try {
            return mapper.readValue(new File(CONFIG_FILE), WindowsState.class);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return new WindowsState();
        }
    }

    public void setState(WindowsState state) {
        try {
            mapper.writeValue(new File(CONFIG_FILE), state);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}
