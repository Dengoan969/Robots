package gui;

import gui.state.Stateful;
import log.LogChangeListener;
import log.LogWindowSource;

import javax.swing.*;
import java.awt.*;

public class LogWindow extends JInternalFrame implements LogChangeListener, Stateful {
    private final LogWindowSource logSource;
    private final TextArea logContent;

    public LogWindow(int width, int height, LogWindowSource logSource) {
        super("Протокол работы", true, true, true, true);
        this.logSource = logSource;
        this.logSource.registerListener(this);
        logContent = new TextArea("");
        logContent.setSize(200, 500);

        var panel = new JPanel(new BorderLayout());
        panel.add(logContent, BorderLayout.CENTER);
        getContentPane().add(panel);

        pack();
        updateLogContent();
        setSize(width, height);
    }

    private void updateLogContent() {
        var content = new StringBuilder();
        for (var entry : logSource.all()) {
            content.append(entry.getMessage()).append("\n");
        }
        logContent.setText(content.toString());
        logContent.invalidate();
    }

    @Override
    public void onLogChanged() {
        EventQueue.invokeLater(this::updateLogContent);
    }

    @Override
    public String getName() {
        return "LogWindow";
    }
}
