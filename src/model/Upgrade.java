package model;

import controller.GameController;

public abstract class Upgrade {
    protected int x;
    protected int y;
    protected String iconPath;

    public Upgrade(int x, int y, String iconPath) {
        this.x = x;
        this.y = y;
        this.iconPath = iconPath;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public String getIconPath() {
        return iconPath;
    }

    public abstract void applyEffect(GameModel model, GameController controller);
}
