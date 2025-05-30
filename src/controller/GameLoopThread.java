package controller;

import model.GameModel;
import model.Ghost;
import model.Upgrade;

public class GameLoopThread implements Runnable {
    private GameModel model;
    private view.PacmanGameBoard gameBoard;
    private GameController controller;
    private volatile boolean running = true;

    public GameLoopThread(GameModel model, view.PacmanGameBoard gameBoard, GameController controller) {
        this.model = model;
        this.gameBoard = gameBoard;
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

                synchronized (model) {
                    if (System.currentTimeMillis() > model.getPacmanInvincibilityEndTime()) {
                        model.setPacmanInvincibilityEndTime(0);
                    }
                    if (System.currentTimeMillis() > model.getScoreMultiplierEndTime()) {
                        model.setScoreMultiplier(1);
                        model.setScoreMultiplierEndTime(0);
                    }

                    int pacmanY = model.getPacmanY();
                    int pacmanX = model.getPacmanX();
                    if (model.getMatrix()[pacmanY][pacmanX] == 0) {
                        model.setPelletEaten(pacmanY, pacmanX);
                        model.addScore(10 * model.getScoreMultiplier());
                    }

                    Upgrade collectedUpgrade = null;
                    for (Upgrade upgrade : model.getActiveUpgrades()) {
                        if (upgrade.getY() == pacmanY && upgrade.getX() == pacmanX) {
                            collectedUpgrade = upgrade;
                            break;
                        }
                    }
                    if (collectedUpgrade != null) {
                        controller.applyUpgradeEffect(collectedUpgrade);
                    }

                    if (model.getPacmanInvincibilityEndTime() == 0) {
                        for (Ghost ghost : model.getGhosts()) {
                            if (ghost.currentY == pacmanY && ghost.currentX == pacmanX) {
                                controller.handlePacmanDeath();
                                break;
                            }
                        }
                    }

                    if (model.getLives() <= 0) {
                        model.setGameOver(true);
                    }

                    if (model.getPelletsEaten() == model.getTotalPellets()) {
                        model.setGameWon(true);
                    }

                    if (model.isGameOver()) {
                        running = false;
                        controller.handleGameOver();
                        break;
                    } else if (model.isGameWon()) {
                        controller.handleLevelCompletion();
                    }
                }

                gameBoard.repaint();
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                running = false;
                controller.showErrorDialog("Game loop interrupted: " + e.getMessage());
            } catch (Exception e) {
                controller.showErrorDialog("An error occurred in the game loop: " + e.getMessage());
            }
        }
    }

    public void stopRunning() {
        running = false;
    }
}
