package controller;

import model.GameModel;

import javax.swing.*;

public class StatusUpdateThread implements Runnable {
    private GameModel model;
    private JLabel scoreLabel;
    private JLabel livesLabel;
    private JLabel timeLabel;
    private volatile boolean running = true;

    public StatusUpdateThread(GameModel model, JLabel scoreLabel, JLabel livesLabel, JLabel timeLabel) {
        this.model = model;
        this.scoreLabel = scoreLabel;
        this.livesLabel = livesLabel;
        this.timeLabel = timeLabel;
    }

    @Override
    public void run() {
        while (running) {
            try {
                if (model.isGamePaused()) {
                    Thread.sleep(100);
                    continue;
                }

                // Update UI on the Event Dispatch Thread
                SwingUtilities.invokeLater(() -> {
                    scoreLabel.setText("Score: " + model.getScore());
                    livesLabel.setText("Lives: " + model.getLives());
                    timeLabel.setText("Time: " + model.getElapsedTime() + "s");
                });

                Thread.sleep(50); // Update status every 50ms
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                running = false;
            } catch (Exception e) {
                System.err.println("Error in status update thread: " + e.getMessage());
            }
        }
    }

    public void stopRunning() {
        running = false;
    }
}
