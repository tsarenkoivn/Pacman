package model;

import controller.GameController;

public class ScoreMultiplierUpgrade extends Upgrade {
    public ScoreMultiplierUpgrade(int x, int y) {
        super(x, y, "upgrade_score_multiplier.png");
    }

    @Override
    public void applyEffect(GameModel model, GameController controller) {
        // Effect is applied by GameController
    }
}
