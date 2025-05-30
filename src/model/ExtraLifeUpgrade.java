package model;

import controller.GameController;

public class ExtraLifeUpgrade extends Upgrade {
    public ExtraLifeUpgrade(int x, int y) {
        super(x, y, "upgrade_extralife.png");
    }

    @Override
    public void applyEffect(GameModel model, GameController controller) {
        // Effect is applied by GameController
    }
}
