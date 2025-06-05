package controller;

import model.GameModel;
import view.PacmanGameBoard;
import view.CellRenderer;
import model.Upgrade;
import model.SpeedUpgrade;
import model.InvincibilityUpgrade;
import model.ScoreMultiplierUpgrade;
import model.ExtraLifeUpgrade;
import model.GhostFreezeUpgrade;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GameController {
    private GameModel model;
    private PacmanGameBoard gameBoard;
    private view.PacmanGameWindow gameWindow;
    private CellRenderer cellRenderer;

    private JLabel scoreLabel;
    private JLabel livesLabel;
    private JLabel timeLabel;

    private GameLoopThread gameLoopThread;
    private PacmanMovementThread pacmanMovementThread;
    private List<GhostMovementThread> ghostMovementThreads;
    private AnimationThread animationThread;
    private UpgradeGenerationThread upgradeGenerationThread;
    private StatusUpdateThread statusUpdateThread;

    private volatile boolean running;

    public GameController(GameModel model, PacmanGameBoard gameBoard, view.PacmanGameWindow gameWindow, CellRenderer cellRenderer, JLabel scoreLabel, JLabel livesLabel, JLabel timeLabel) {
        this.model = model;
        this.gameBoard = gameBoard;
        this.gameWindow = gameWindow;
        this.cellRenderer = cellRenderer;
        this.scoreLabel = scoreLabel;
        this.livesLabel = livesLabel;
        this.timeLabel = timeLabel;
        this.running = false;
        this.ghostMovementThreads = new ArrayList<>();
    }

    public void startGame() {
        running = true;
        gameLoopThread = new GameLoopThread(model, gameBoard, this);
        new Thread(gameLoopThread).start();

        pacmanMovementThread = new PacmanMovementThread(model, gameBoard.getPacmanMoveListener(), this);
        new Thread(pacmanMovementThread).start();

        for (int i = 0; i < model.getGhosts().length; i++) {
            GhostMovementThread ghostThread = new GhostMovementThread(model, model.getGhosts()[i], i, this);
            ghostMovementThreads.add(ghostThread);
            new Thread(ghostThread).start();
        }

        animationThread = new AnimationThread(model, gameBoard, cellRenderer);
        new Thread(animationThread).start();

        upgradeGenerationThread = new UpgradeGenerationThread(model, this);
        new Thread(upgradeGenerationThread).start();

        statusUpdateThread = new StatusUpdateThread(model, scoreLabel, livesLabel, timeLabel);
        new Thread(statusUpdateThread).start();
    }

    public void stopGame() {
        running = false;
        if (gameLoopThread != null) gameLoopThread.stopRunning();
        if (pacmanMovementThread != null) pacmanMovementThread.stopRunning();
        for (GhostMovementThread thread : ghostMovementThreads) {
            if (thread != null) thread.stopRunning();
        }
        if (animationThread != null) animationThread.stopRunning();
        if (upgradeGenerationThread != null) upgradeGenerationThread.stopRunning();
        if (statusUpdateThread != null) statusUpdateThread.stopRunning();

        try {
            if (gameLoopThread != null) new Thread(gameLoopThread).join(500);
            if (pacmanMovementThread != null) new Thread(pacmanMovementThread).join(500);
            for (GhostMovementThread thread : ghostMovementThreads) {
                if (thread != null) new Thread(thread).join(500);
            }
            if (animationThread != null) new Thread(animationThread).join(500);
            if (upgradeGenerationThread != null) new Thread(upgradeGenerationThread).join(500);
            if (statusUpdateThread != null) new Thread(statusUpdateThread).join(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            showErrorDialog("Error stopping game threads: " + e.getMessage());
        }

        SwingUtilities.invokeLater(() -> {
            JFrame currentFrame = (JFrame) gameBoard.getTopLevelAncestor();
            if (currentFrame != null) {
                currentFrame.setVisible(false);
                currentFrame.dispose();
            }
            gameWindow.gameWindow(gameWindow.getWidth(), gameWindow.getHeight());
        });
    }
    public void handleGameOver() {
        stopGame();

        SwingUtilities.invokeLater(() -> {
            JFrame nameInputFrame = new JFrame("Game Over!");
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            JTextArea messageArea = new JTextArea("Game Over! Your score: " + model.getScore() + "\nEnter your nickname:");
            messageArea.setEditable(false);
            messageArea.setBackground(panel.getBackground());
            panel.add(messageArea);

            JTextField nameField = new JTextField(15);
            panel.add(nameField);

            JButton okButton = new JButton("OK");
            okButton.addActionListener(e -> {
                String name = nameField.getText().trim();
                if (name.isEmpty()) {
                    name = "Anonymous";
                }
                saveHighScore(name, model.getScore());
                nameInputFrame.dispose();
            });
            panel.add(okButton);

            nameInputFrame.add(panel);
            nameInputFrame.pack();
            nameInputFrame.setLocationRelativeTo(null);
            nameInputFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
            nameInputFrame.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                    nameInputFrame.dispose();
                    gameWindow.gameWindow(gameWindow.getWidth(), gameWindow.getHeight());
                }
            });
            nameInputFrame.setVisible(true);
        });
    }

    private void saveHighScore(String name, int score) {
        try (BufferedWriter file = new BufferedWriter(new FileWriter("HighScores.txt", true))) {
            file.write(name + ": " + score);
            file.newLine();
        } catch (IOException ex) {
            showErrorDialog("Error saving high score: " + ex.getMessage());
        }
    }

    public void showErrorDialog(String message) {
        SwingUtilities.invokeLater(() ->
                JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE)
        );
    }

    public boolean isRunning() {
        return running;
    }

    public void handlePacmanDeath() {
        synchronized (model) {
            if (model.getLives() > 0) {
                model.loseLife();
                model.resetGameState(false);
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    showErrorDialog("Game interrupted during death pause: " + e.getMessage());
                }
            } else {
                model.setGameOver(true);
            }
        }
    }

    public void handleLevelCompletion() {
        synchronized (model) {
            model.setGameWon(true);
            SwingUtilities.invokeLater(() ->
                    JOptionPane.showMessageDialog(null, "Level Complete! Starting next level...", "Congratulations!", JOptionPane.INFORMATION_MESSAGE)
            );
            model.resetGameState(true);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            model.setGameWon(false);
        }
    }

    public void applyUpgradeEffect(Upgrade upgrade) {
        synchronized (model) {
            if (upgrade instanceof SpeedUpgrade) {
                pacmanMovementThread.applySpeedUpgrade(7000);
            } else if (upgrade instanceof InvincibilityUpgrade) {
                model.setPacmanInvincibilityEndTime(System.currentTimeMillis() + 7000);
            } else if (upgrade instanceof ExtraLifeUpgrade) {
                model.addScore(500);
                if (model.getLives() < 3) {
                    model.addLife();
                }
                SwingUtilities.invokeLater(() -> livesLabel.setText("Lives: " + model.getLives()));
            } else if (upgrade instanceof ScoreMultiplierUpgrade) {
                model.setScoreMultiplier(2);
                model.setScoreMultiplierEndTime(System.currentTimeMillis() + 10000);
            } else if (upgrade instanceof GhostFreezeUpgrade) {
                for (model.Ghost ghost : model.getGhosts()) {
                    ghost.setFrozen(true);
                }
            }
            model.removeUpgrade(upgrade);
        }
    }
}
