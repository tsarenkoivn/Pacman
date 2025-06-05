package controller;

import model.*;
import java.util.Random;
import java.util.List;

public class UpgradeGenerationThread implements Runnable {
    private GameModel model;
    private GameController controller;
    private volatile boolean running = true;
    private Random random = new Random();
    private static final long UPGRADE_SPAWN_INTERVAL = 5000;
    private static final double UPGRADE_SPAWN_CHANCE_PER_GHOST = 0.25;

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

                synchronized (model) {
                    for (Ghost ghost : model.getGhosts()) {
                        if (random.nextDouble() < UPGRADE_SPAWN_CHANCE_PER_GHOST) {
                            attemptSpawnUpgradeAtLocation(ghost.currentX, ghost.currentY);
                        }
                    }
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                running = false;
            } catch (Exception e) {
                controller.showErrorDialog("An error occurred in upgrade generation: " + e.getMessage());
            }
        }
    }

    private void attemptSpawnUpgradeAtLocation(int x, int y) {
        int[][] matrix = model.getMatrix();
        if (y < 0 || y >= matrix.length || x < 0 || x >= matrix[0].length || matrix[y][x] == 1) {
            return;
        }

        for (Upgrade existingUpgrade : model.getActiveUpgrades()) {
            if (existingUpgrade.getY() == y && existingUpgrade.getX() == x) {
                return;
            }
        }

        Upgrade newUpgrade = null;
        int upgradeType = random.nextInt(5);

        switch (upgradeType) {
            case 0: newUpgrade = new SpeedUpgrade(x, y); break;
            case 1: newUpgrade = new InvincibilityUpgrade(x, y); break;
            case 2: newUpgrade = new ExtraLifeUpgrade(x, y); break;
            case 3: newUpgrade = new ScoreMultiplierUpgrade(x, y); break;
            case 4: newUpgrade = new GhostFreezeUpgrade(x, y); break;
        }

        if (newUpgrade != null) {
            model.addUpgrade(newUpgrade);
        }
    }

    public void stopRunning() {
        running = false;
    }
}
