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

                    int currentDesiredDx = pacmanMoveListener.getDesiredDx();
                    int currentDesiredDy = pacmanMoveListener.getDesiredDy();

                    int moveDx = (currentDesiredDx != 0 || currentDesiredDy != 0) ? currentDesiredDx : pacmanMoveListener.getLastSuccessfulDx();
                    int moveDy = (currentDesiredDx != 0 || currentDesiredDy != 0) ? currentDesiredDy : pacmanMoveListener.getLastSuccessfulDy();

                    int currentPacmanX = model.getPacmanX();
                    int currentPacmanY = model.getPacmanY();
                    int[][] matrix = model.getMatrix();

                    int newPacmanX = currentPacmanX + moveDx;
                    int newPacmanY = currentPacmanY + moveDy;

                    if (newPacmanX < 0) {
                        newPacmanX = matrix[0].length - 1;
                    } else if (newPacmanX >= matrix[0].length) {
                        newPacmanX = 0;
                    }

                    boolean canMoveToNewPos = (newPacmanY >= 0 && newPacmanY < matrix.length && matrix[newPacmanY][newPacmanX] != 1);

                    if (canMoveToNewPos) {
                        model.setPacmanPosition(newPacmanX, newPacmanY);
                        pacmanMoveListener.setLastSuccessfulDx(moveDx);
                        pacmanMoveListener.setLastSuccessfulDy(moveDy);

                        model.setPacmanDirectionX(moveDx);
                        model.setPacmanDirectionY(moveDy);
                    } else {
                        pacmanMoveListener.setLastSuccessfulDx(0);
                        pacmanMoveListener.setLastSuccessfulDy(0);
                        model.setPacmanDirectionX(0);
                        model.setPacmanDirectionY(0);
                    }
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
