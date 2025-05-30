package controller;

import model.GameModel;
import model.Ghost;
import util.GhostPath;

public class GhostMovementThread implements Runnable {
    private GameModel model;
    private Ghost ghost;
    private int ghostIndex;
    private GameController controller;
    private volatile boolean running = true;
    private int pathIndex = 0;

    public GhostMovementThread(GameModel model, Ghost ghost, int ghostIndex, GameController controller) {
        this.model = model;
        this.ghost = ghost;
        this.ghostIndex = ghostIndex;
        this.controller = controller;
    }

    @Override
    public void run() {
        while (running) {
            try {
                if (model.isGamePaused() || ghost.isFrozen()) {
                    Thread.sleep(100);
                    continue;
                }

                synchronized (model) {
                    if (ghost.path == null || ghost.path.isEmpty() || pathIndex >= ghost.path.size()) {
                        ghost = new Ghost(model.getMatrix(), ghost.currentY, ghost.currentX);
                        model.getGhosts()[ghostIndex] = ghost;
                        pathIndex = 0;
                    }

                    if (pathIndex < ghost.path.size()) {
                        int[] nextPos = ghost.path.get(pathIndex);
                        ghost.setPosition(nextPos[0], nextPos[1]);
                        pathIndex++;
                    }
                }
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                running = false;
                controller.showErrorDialog("Ghost movement interrupted: " + e.getMessage());
            } catch (Exception e) {
                controller.showErrorDialog("An error occurred in ghost movement: " + e.getMessage());
            }
        }
    }

    public void stopRunning() {
        running = false;
    }
}
