package gui;

import gui.state.Stateful;

import javax.swing.*;
import java.awt.*;

public class GameWindow extends JInternalFrame implements Stateful {

    public GameWindow(int width, int height) {
        super("Игровое поле", true, true, true, true);

        var visualizer = new GameVisualizer();
        var panel = new JPanel(new BorderLayout());
        panel.add(visualizer, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
        setSize(width, height);
    }

    @Override
    public String getName() {
        return "GameWindow";
    }
}