package model;

import controller.GameController;

public class GhostFreezeUpgrade extends Upgrade {
    public GhostFreezeUpgrade(int x, int y) {
        super(x, y, "upgrade_ghost_freeze.png");
    }

    @Override
    public void applyEffect(GameModel model, GameController controller) {
        // Effect is applied by GameController
    }
}
