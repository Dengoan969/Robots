package gui.game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.util.Timer;
import java.util.TimerTask;

public class GameVisualizer extends JPanel {
    private final Robot robot;
    private volatile int targetPositionX = 150;
    private volatile int targetPositionY = 100;

    public GameVisualizer(Robot robot) {
        this.robot = robot;

        var timer = initTimer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                onRedrawEvent();
            }
        }, 0, 50);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                onModelUpdateEvent();
            }
        }, 0, 10);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                setTargetPosition(e.getPoint());
                repaint();
            }
        });
        setDoubleBuffered(true);
    }

    private static Timer initTimer() {
        return new Timer("events generator", true);
    }

    private static int round(double value) {
        return (int) (value + 0.5);
    }

    private static void fillOval(Graphics g, int centerX, int centerY, int diam1, int diam2) {
        g.fillOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }

    private static void drawOval(Graphics g, int centerX, int centerY, int diam1, int diam2) {
        g.drawOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }

    protected void setTargetPosition(Point p) {
        targetPositionX = p.x;
        targetPositionY = p.y;
    }

    protected void onRedrawEvent() {
        EventQueue.invokeLater(this::repaint);
    }

    protected void onModelUpdateEvent() {
        robot.moveToTarget(targetPositionX, targetPositionY);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        var g2d = (Graphics2D) g;

        var robotX = robot.getX();
        var robotY = robot.getY();
        var robotDirection = robot.getDirection();

        drawRobot(g2d, round(robotX), round(robotY), robotDirection);
        drawTarget(g2d, targetPositionX, targetPositionY);
    }

    private void drawRobot(Graphics2D g, int x, int y, double direction) {
        var robotX = robot.getX();
        var robotY = robot.getY();

        var robotCenterX = round(robotX);
        var robotCenterY = round(robotY);
        var t = AffineTransform.getRotateInstance(direction, robotCenterX, robotCenterY);
        g.setTransform(t);
        g.setColor(Color.MAGENTA);
        fillOval(g, robotCenterX, robotCenterY, 30, 10);
        g.setColor(Color.BLACK);
        drawOval(g, robotCenterX, robotCenterY, 30, 10);
        g.setColor(Color.WHITE);
        fillOval(g, robotCenterX + 10, robotCenterY, 5, 5);
        g.setColor(Color.BLACK);
        drawOval(g, robotCenterX + 10, robotCenterY, 5, 5);
    }

    private void drawTarget(Graphics2D g, int x, int y) {
        var t = AffineTransform.getRotateInstance(0, 0, 0);
        g.setTransform(t);
        g.setColor(Color.GREEN);
        fillOval(g, x, y, 5, 5);
        g.setColor(Color.BLACK);
        drawOval(g, x, y, 5, 5);
    }
}
