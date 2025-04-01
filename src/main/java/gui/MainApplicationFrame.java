package gui;

import log.Logger;

import javax.swing.*;

public class MainApplicationFrame extends JFrame {
    private final JDesktopPane desktopPane = new JDesktopPane();

    public MainApplicationFrame() {
        addTranslatedText();

        setContentPane(desktopPane);

        addLogWindow();

        addGameWindow();

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
}
