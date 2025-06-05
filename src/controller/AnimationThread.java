package controller;

import model.GameModel;
import view.PacmanGameBoard;
import view.CellRenderer;

public class AnimationThread implements Runnable {
    private GameModel model;
    private PacmanGameBoard gameBoard;
    private CellRenderer cellRenderer;
    private volatile boolean running = true;

    public AnimationThread(GameModel model, PacmanGameBoard gameBoard, CellRenderer cellRenderer) {
        this.model = model;
        this.gameBoard = gameBoard;
        this.cellRenderer = cellRenderer;
    }

    @Override
    public void run() {
        while (running) {
            try {
                if (model.isGamePaused()) {
                    Thread.sleep(100);
                    continue;
                }
                cellRenderer.setMouthOpen(!cellRenderer.mouthOpen);

                gameBoard.repaint();
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                running = false;
            } catch (Exception e) {
                System.err.println("Error in animation thread: " + e.getMessage());
            }
        }
    }

    public void stopRunning() {
        running = false;
    }
}
