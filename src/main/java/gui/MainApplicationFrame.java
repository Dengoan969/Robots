package gui;

import log.Logger;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyVetoException;

public class MainApplicationFrame extends JFrame {
    private final JDesktopPane desktopPane = new JDesktopPane();
    private final StateManager stateManager = new StateManager();

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
        for (var frame : desktopPane.getAllFrames()) {
            var title = frame.getTitle();
            var state = new WindowState(
                    frame.getX(),
                    frame.getY(),
                    frame.isIcon()
            );
            windowsState.getWindows().put(title, state);
        }
        stateManager.setState(windowsState);
    }

    private void loadWindowsState() {
        var windowsState = stateManager.getState();
        if (windowsState == null || windowsState.getWindows() == null || windowsState.getWindows().isEmpty()) {
            return;
        }
        for (var frame : desktopPane.getAllFrames()) {
            var title = frame.getTitle();
            if (windowsState.getWindows().containsKey(title)) {
                var state = windowsState.getWindows().get(title);
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
