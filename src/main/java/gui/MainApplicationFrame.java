package gui;

import gui.helpers.MenuBarBuilder;
import gui.state.StateManager;
import gui.state.StatefulRegistry;
import gui.state.WindowState;
import gui.state.WindowsState;
import log.Logger;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyVetoException;

public class MainApplicationFrame extends JFrame {
    private final JDesktopPane desktopPane = new JDesktopPane();
    private final StateManager stateManager = new StateManager();
    private final StatefulRegistry statefulRegistry = new StatefulRegistry();

    public MainApplicationFrame() {
        setContentPane(desktopPane);
        addTranslatedText();

        addLogWindow();
        addGameWindow();

        loadWindowsState();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                saveWindowsState();
            }
        });

        setJMenuBar(createMenuBar());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private void addLogWindow() {
        var logWindow = new LogWindow(300, 800, Logger.getDefaultLogSource());

        setMinimumSize(logWindow.getSize());
        logWindow.pack();
        Logger.debug("Протокол работает");
        addWindow(logWindow);
    }

    private void addGameWindow() {
        var gameWindow = new GameWindow(400, 400);

        addWindow(gameWindow);
    }

    protected void addWindow(JInternalFrame frame) {
        desktopPane.add(frame);
        frame.setVisible(true);

        statefulRegistry.registerIfStateful(frame);
    }

    private void addTranslatedText() {
        UIManager.put("OptionPane.yesButtonText", "Да");
        UIManager.put("OptionPane.noButtonText", "Нет");
    }

    private JMenuBar createMenuBar() {
        return new MenuBarBuilder(this)
                .createAppMenu()
                .createLookAndFeelMenu()
                .createTestMenu()
                .build();
    }

    private void saveWindowsState() {
        var windowsState = new WindowsState();

        for (var entry : statefulRegistry.getStatefulWindows().entrySet()) {
            var id = entry.getKey();
            var frame = (JInternalFrame) entry.getValue();

            var state = new WindowState(frame.getX(), frame.getY(), frame.isIcon());
            windowsState.getWindowsStates().put(id, state);
        }
        stateManager.setState(windowsState);
    }

    private void loadWindowsState() {
        var windowsState = stateManager.getState();
        if (windowsState.getWindowsStates().isEmpty()) {
            return;
        }

        for (var entry : statefulRegistry.getStatefulWindows().entrySet()) {
            var id = entry.getKey();
            var frame = (JInternalFrame) entry.getValue();

            if (windowsState.getWindowsStates().containsKey(id)) {
                var state = windowsState.getWindowsStates().get(id);
                frame.setLocation(state.getX(), state.getY());
                try {
                    frame.setIcon(state.getIcon());
                } catch (PropertyVetoException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
