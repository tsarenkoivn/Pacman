package model;

import controller.GameController;

public class InvincibilityUpgrade extends Upgrade {
    public InvincibilityUpgrade(int x, int y) {
        super(x, y, "upgrade_invincibility.png");
    }

    @Override
    public void applyEffect(GameModel model, GameController controller) {
        // Effect is applied by GameController
    }
}
