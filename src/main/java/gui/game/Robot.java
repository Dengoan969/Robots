package gui.game;

import java.util.Observable;

public class Robot extends Observable {
    private static final double MAX_VELOCITY = 0.1;
    private static final double MAX_ANGULAR_VELOCITY = 0.01;

    private static final double ANGULAR_VELOCITY_SCALE = 0.01;

    private volatile double x = 100;
    private volatile double y = 100;
    private volatile double direction = 0;

    private static double distance(double x1, double y1, double x2, double y2) {
        var diffX = x1 - x2;
        var diffY = y1 - y2;
        return Math.sqrt(diffX * diffX + diffY * diffY);
    }

    private static double angleTo(double fromX, double fromY, double toX, double toY) {
        var diffX = toX - fromX;
        var diffY = toY - fromY;

        return asNormalizedRadians(Math.atan2(diffY, diffX));
    }

    private static double applyLimits(double value, double min, double max) {
        if (value < min)
            return min;
        if (value > max)
            return max;
        return value;
    }

    private static double asNormalizedRadians(double angle) {
        while (angle < 0) {
            angle += 2 * Math.PI;
        }
        while (angle >= 2 * Math.PI) {
            angle -= 2 * Math.PI;
        }
        return angle;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getDirection() {
        return direction;
    }

    public void updateRobotPosition(double x, double y, double direction) {
        this.x = x;
        this.y = y;
        this.direction = direction;

        setChanged();
        notifyObservers();
    }

    public void moveToTarget(double targetPositionX, double targetPositionY) {
        var dist = distance(targetPositionX, targetPositionY, x, y);
        if (dist < 2) {
            return;
        }

        var targetAngle = angleTo(x, y, targetPositionX, targetPositionY);

        var diff = targetAngle - direction;
        while (diff > Math.PI) {
            diff -= 2 * Math.PI;
        }
        while (diff < -Math.PI) {
            diff += 2 * Math.PI;
        }

        var angularVelocity = ANGULAR_VELOCITY_SCALE * diff;

        var velocityScale = Math.cos(diff);
        if (velocityScale < 0) {
            velocityScale = 0;
        }
        var linearVelocity = MAX_VELOCITY * velocityScale;

        moveRobot(linearVelocity, angularVelocity, 10);
    }

    private void moveRobot(double velocity, double angularVelocity, double duration) {
        velocity = applyLimits(velocity, 0, MAX_VELOCITY);
        angularVelocity = applyLimits(angularVelocity, -MAX_ANGULAR_VELOCITY, MAX_ANGULAR_VELOCITY);

        var newX = x + velocity / angularVelocity *
                (Math.sin(direction + angularVelocity * duration) -
                        Math.sin(direction));
        if (!Double.isFinite(newX)) {
            newX = x + velocity * duration * Math.cos(direction);
        }

        var newY = y - velocity / angularVelocity *
                (Math.cos(direction + angularVelocity * duration) -
                        Math.cos(direction));
        if (!Double.isFinite(newY)) {
            newY = y + velocity * duration * Math.sin(direction);
        }

        var newDirection = asNormalizedRadians(direction + angularVelocity * duration);

        updateRobotPosition(newX, newY, newDirection);
    }
}