package gui;

import javax.swing.*;
import java.awt.*;

public class GameWindow extends JInternalFrame {

    public GameWindow(int width, int height) {
        super("Игровое поле", true, true, true, true);

        var m_visualizer = new GameVisualizer();
        var panel = new JPanel(new BorderLayout());
        panel.add(m_visualizer, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
        setSize(width, height);
    }
}
