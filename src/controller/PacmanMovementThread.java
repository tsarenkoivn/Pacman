package controller;

import model.GameModel;
import util.PacmanMove;

public class PacmanMovementThread implements Runnable {
    private GameModel model;
    private PacmanMove pacmanMoveListener;
    private GameController controller;
    private volatile boolean running = true;

    public PacmanMovementThread(GameModel model, PacmanMove pacmanMoveListener, GameController controller) {
        this.model = model;
        this.pacmanMoveListener = pacmanMoveListener;
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

                long sleepTime = 150;
                if (System.currentTimeMillis() < model.getPacmanSpeedBoostEndTime()) {
                    sleepTime = 75;
                } else {
                    model.setPacmanSpeedBoostEndTime(0);
                }

                synchronized (model) {
                    if (pacmanMoveListener.isStopGameShortcutActive()) {
                        model.setGamePaused(true);
                        pacmanMoveListener.resetStopGameShortcut();
                        running = false;
                        controller.stopGame();
                        break;
                    }

                    int desiredDx = pacmanMoveListener.getX();
                    int desiredDy = pacmanMoveListener.getY();

                    int currentPacmanX = model.getPacmanX();
                    int currentPacmanY = model.getPacmanY();
                    int[][] matrix = model.getMatrix();
                    int newPacmanX = currentPacmanX + desiredDx;
                    int newPacmanY = currentPacmanY + desiredDy;

                    if (newPacmanX < 0) {
                        newPacmanX = matrix[0].length - 1;
                    } else if (newPacmanX >= matrix[0].length) {
                        newPacmanX = 0;
                    }

                    if (newPacmanY >= 0 && newPacmanY < matrix.length && matrix[newPacmanY][newPacmanX] != 1) {
                        model.setPacmanPosition(newPacmanX, newPacmanY);
                        if (desiredDx != 0) model.setPacmanDirectionX(desiredDx);
                        if (desiredDy != 0) model.setPacmanDirectionY(desiredDy);
                    } else {
                        int lastDx = pacmanMoveListener.getXX();
                        int lastDy = pacmanMoveListener.getYY();
                        int continuedX = currentPacmanX + lastDx;
                        int continuedY = currentPacmanY + lastDy;

                        if (continuedX < 0) {
                            continuedX = matrix[0].length - 1;
                        } else if (continuedX >= matrix[0].length) {
                            continuedX = 0;
                        }

                        if (continuedY >= 0 && continuedY < matrix.length && matrix[continuedY][continuedX] != 1) {
                            model.setPacmanPosition(continuedX, continuedY);
                            if (lastDx != 0) model.setPacmanDirectionX(lastDx);
                            if (lastDy != 0) model.setPacmanDirectionY(lastDy);
                        }
                    }
                    pacmanMoveListener.resetDesiredMovement();
                }
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                running = false;
                controller.showErrorDialog("Pac-Man movement interrupted: " + e.getMessage());
            } catch (Exception e) {
                controller.showErrorDialog("An error occurred in Pac-Man movement: " + e.getMessage());
            }
        }
    }

    public void stopRunning() {
        running = false;
    }

    public void applySpeedUpgrade(long duration) {
        model.setPacmanSpeedBoostEndTime(System.currentTimeMillis() + duration);
    }
}
