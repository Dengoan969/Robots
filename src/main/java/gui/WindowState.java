package gui;

public class WindowState {
    private int x;
    private int y;
    private boolean icon;

    public WindowState() {}

    public WindowState(int x, int y, boolean icon) {
        this.x = x;
        this.y = y;
        this.icon = icon;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public boolean getIcon() {
        return icon;
    }

    public void setIcon(boolean isIcon) {
        this.icon = isIcon;
    }
}
