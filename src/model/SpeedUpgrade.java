package model;

import controller.GameController;

public class SpeedUpgrade extends Upgrade {
    public SpeedUpgrade(int x, int y) {
        super(x, y, "upgrade_speed.png");
    }

    @Override
    public void applyEffect(GameModel model, GameController controller) {
        // Effect is applied by GameController
    }
}
