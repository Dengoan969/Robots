package gui.game;

import gui.state.Stateful;

import javax.swing.*;
import java.awt.*;
import java.util.Observable;
import java.util.Observer;

public class RobotCoordinatesWindow extends JInternalFrame implements Stateful, Observer {
    private final JLabel coordinatesLabel;

    public RobotCoordinatesWindow(int width, int height) {
        super("Координаты робота", true, true, true, true);

        coordinatesLabel = new JLabel("X: unknown, Y: unknown, Direction: unknown");

        var panel = new JPanel(new BorderLayout());
        panel.add(coordinatesLabel, BorderLayout.CENTER);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 0));

        getContentPane().add(panel);
        pack();
        setSize(width, height);
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof Robot robot) {
            var coordinatesText = String.format("X: %.3f, Y: %.3f, Direction: %.3f",
                    robot.getX(),
                    robot.getY(),
                    robot.getDirection());

            coordinatesLabel.setText(coordinatesText);
        }
    }

    public String getName() {
        return "RobotCoordinatesWindow";
    }
}