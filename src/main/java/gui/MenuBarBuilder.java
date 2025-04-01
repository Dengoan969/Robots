package gui;

import log.Logger;

import javax.swing.*;
import java.awt.event.KeyEvent;

public class MenuBarBuilder {
    private final JMenuBar menuBar = new JMenuBar();
    private final JFrame frame;

    public MenuBarBuilder(JFrame frame) {
        this.frame = frame;
    }

    public MenuBarBuilder createTestMenu() {
        var testMenu = new JMenu("Тесты");
        testMenu.setMnemonic(KeyEvent.VK_T);
        testMenu.getAccessibleContext().setAccessibleDescription(
                "Тестовые команды");

        var addLogMessageItem = new JMenuItem("Сообщение в лог", KeyEvent.VK_S);
        addLogMessageItem.addActionListener((event) -> {
            Logger.debug("Новая строка");
        });
        testMenu.add(addLogMessageItem);
        menuBar.add(testMenu);

        return this;
    }

    public MenuBarBuilder createLookAndFeelMenu() {
        var lookAndFeelMenu = new JMenu("Режим отображения");
        lookAndFeelMenu.setMnemonic(KeyEvent.VK_V);
        lookAndFeelMenu.getAccessibleContext().setAccessibleDescription(
                "Управление режимом отображения приложения");

        var systemLookAndFeel = new JMenuItem("Системная схема", KeyEvent.VK_S);
        systemLookAndFeel.addActionListener((_) -> {
            setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            frame.invalidate();
        });
        lookAndFeelMenu.add(systemLookAndFeel);

        var crossplatformLookAndFeel = new JMenuItem("Универсальная схема", KeyEvent.VK_S);
        crossplatformLookAndFeel.addActionListener((_) -> {
            setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            frame.invalidate();
        });
        lookAndFeelMenu.add(crossplatformLookAndFeel);
        menuBar.add(lookAndFeelMenu);

        return this;
    }

    public MenuBarBuilder createAppMenu() {
        var appMenu = new JMenu("Приложение");
        appMenu.setMnemonic(KeyEvent.VK_V);
        appMenu.getAccessibleContext().setAccessibleDescription(
                "Управление приложением");

        {
            JMenuItem exit = new JMenuItem("Выход", KeyEvent.VK_E);
            exit.addActionListener(_ -> confirmExit());
            appMenu.add(exit);
        }
        menuBar.add(appMenu);

        return this;
    }

    public JMenuBar build() {
        return menuBar;
    }

    private void confirmExit() {
        var result = JOptionPane.showConfirmDialog(frame,
                "Вы хотите выйти из приложения?",
                "Подтверждение выхода", JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION)
        {
            System.exit(0);
        }

    }

    private void setLookAndFeel(String className) {
        try {
            UIManager.setLookAndFeel(className);
            SwingUtilities.updateComponentTreeUI(frame);
        } catch (ClassNotFoundException | InstantiationException
                 | IllegalAccessException | UnsupportedLookAndFeelException e) {
            Logger.error("Error while change LookAndFeel:\n" + e.getMessage());
        }
    }
}
