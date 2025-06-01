package controller;

import model.*;
import java.util.Random;
import java.util.List;

public class UpgradeGenerationThread implements Runnable {
    private GameModel model;
    private GameController controller;
    private volatile boolean running = true;
    private Random random = new Random();
    private static final long UPGRADE_SPAWN_INTERVAL = 2000;
    private static final double UPGRADE_SPAWN_CHANCE = 0.5;

    public UpgradeGenerationThread(GameModel model, GameController controller) {
        this.model = model;
        this.controller = controller;
    }

    @Override
    public void run() {
        while (running) {
            try {
                if (model.isGamePaused()) {
                    Thread.sleep(100);
                    continue;
                }

                Thread.sleep(UPGRADE_SPAWN_INTERVAL);

                if (random.nextDouble() < UPGRADE_SPAWN_CHANCE) {
                    spawnRandomUpgrade();
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                running = false;
            } catch (Exception e) {
                controller.showErrorDialog("An error occurred in upgrade generation: " + e.getMessage());
            }
        }
    }

    private void spawnRandomUpgrade() {
        synchronized (model) {
            List<int[]> emptyCells = new java.util.ArrayList<>();
            int[][] matrix = model.getMatrix();
            for (int r = 0; r < matrix.length; r++) {
                for (int c = 0; c < matrix[r].length; c++) {
                    if (matrix[r][c] == 0) {
                        emptyCells.add(new int[]{r, c});
                    }
                }
            }

            if (!emptyCells.isEmpty()) {
                int[] spawnPos = emptyCells.get(random.nextInt(emptyCells.size()));
                int spawnY = spawnPos[0];
                int spawnX = spawnPos[1];

                boolean occupiedByUpgrade = false;
                for (Upgrade existingUpgrade : model.getActiveUpgrades()) {
                    if (existingUpgrade.getY() == spawnY && existingUpgrade.getX() == spawnX) {
                        occupiedByUpgrade = true;
                        break;
                    }
                }

                if (!occupiedByUpgrade) {
                    Upgrade newUpgrade = null;
                    int upgradeType = random.nextInt(5);

                    switch (upgradeType) {
                        case 0: newUpgrade = new SpeedUpgrade(spawnX, spawnY); break;
                        case 1: newUpgrade = new InvincibilityUpgrade(spawnX, spawnY); break;
                        case 2: newUpgrade = new ExtraLifeUpgrade(spawnX, spawnY); break;
                        case 3: newUpgrade = new ScoreMultiplierUpgrade(spawnX, spawnY); break;
                        case 4: newUpgrade = new GhostFreezeUpgrade(spawnX, spawnY); break;
                    }

                    if (newUpgrade != null) {
                        model.addUpgrade(newUpgrade);
                    }
                }
            }
        }
    }

    public void stopRunning() {
        running = false;
    }
}
