package model;

import util.GenerateBoard;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class GameModel {
    private int[][] matrix;
    private int pacmanX;
    private int pacmanY;
    private int score;
    private int lives;
    private long startTime;
    private long elapsedTime;
    private Ghost[] ghosts;
    private List<Upgrade> activeUpgrades;
    private int totalPellets;
    private int pelletsEaten;
    private boolean gameOver;
    private boolean gameWon;
    private boolean gamePaused;

    private int pacmanDirectionX = 1;
    private int pacmanDirectionY = 0;

    private long pacmanInvincibilityEndTime = 0;
    private int scoreMultiplier = 1;
    private long scoreMultiplierEndTime = 0;
    private long pacmanSpeedBoostEndTime = 0;

    public GameModel(int rows, int columns) {
        this.matrix = new int[rows][columns];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                matrix[i][j] = 1;
            }
        }
        GenerateBoard generateBoard = new GenerateBoard();
        int[][] visited = new int[rows][columns];
        generateBoard.generateBoard(matrix, rows / 2, columns / 2, visited);

        for (int i = Math.max(0, (rows / 2) - 1); i < Math.min(rows, (rows / 2) + 2); i++) {
            for (int j = 0; j < columns; j++) {
                if (matrix[i][j] == 1) {
                    matrix[i][j] = 0;
                }
            }
        }

        this.totalPellets = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (matrix[i][j] == 0) {
                    totalPellets++;
                }
            }
        }

        findInitialPacmanPosition();

        this.score = 0;
        this.lives = 3;
        this.startTime = System.currentTimeMillis();
        this.elapsedTime = 0;
        this.ghosts = new Ghost[4];
        for (int i = 0; i < ghosts.length; i++) {
            ghosts[i] = new Ghost(matrix, -1, -1);
        }
        this.activeUpgrades = new CopyOnWriteArrayList<>();
        this.pelletsEaten = 0;
        this.gameOver = false;
        this.gameWon = false;
        this.gamePaused = false;
    }

    public synchronized void resetGameState(boolean resetPellets) {
        if (resetPellets) {
            for (int i = 0; i < matrix.length; i++) {
                for (int j = 0; j < matrix[i].length; j++) {
                    if (matrix[i][j] == 2) {
                        matrix[i][j] = 0;
                    }
                }
            }
            pelletsEaten = 0;
        }

        findInitialPacmanPosition();
        pacmanDirectionX = 1;
        pacmanDirectionY = 0;

        for (int i = 0; i < ghosts.length; i++) {
            ghosts[i] = new Ghost(matrix, -1, -1);
        }

        activeUpgrades.clear();

        pacmanInvincibilityEndTime = 0;
        scoreMultiplier = 1;
        scoreMultiplierEndTime = 0;
        pacmanSpeedBoostEndTime = 0;

        gameOver = false;
        gameWon = false;
        gamePaused = false;
    }

    private void findInitialPacmanPosition() {
        boolean found = false;
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                if (matrix[i][j] == 0 || matrix[i][j] == 2) {
                    pacmanY = i;
                    pacmanX = j;
                    found = true;
                    break;
                }
            }
            if (found) {
                break;
            }
        }
        if (!found) {
            pacmanX = 0;
            pacmanY = 0;
        }
    }

    public synchronized int[][] getMatrix() {
        return matrix;
    }

    public synchronized int getPacmanX() {
        return pacmanX;
    }

    public synchronized int getPacmanY() {
        return pacmanY;
    }

    public synchronized void setPacmanPosition(int x, int y) {
        this.pacmanX = x;
        this.pacmanY = y;
    }

    public synchronized int getScore() {
        return score;
    }

    public synchronized void addScore(int points) {
        this.score += points;
    }

    public synchronized int getLives() {
        return lives;
    }

    public synchronized void loseLife() {
        this.lives--;
    }

    public synchronized void addLife() {
        this.lives++;
    }

    public synchronized Ghost[] getGhosts() {
        return ghosts;
    }

    public synchronized List<Upgrade> getActiveUpgrades() {
        return activeUpgrades;
    }

    public synchronized void addUpgrade(Upgrade upgrade) {
        this.activeUpgrades.add(upgrade);
    }

    public synchronized void removeUpgrade(Upgrade upgrade) {
        this.activeUpgrades.remove(upgrade);
    }

    public synchronized long getElapsedTime() {
        return (System.currentTimeMillis() - startTime) / 1000;
    }

    public synchronized void setPelletEaten(int row, int col) {
        if (matrix[row][col] == 0) {
            matrix[row][col] = 2;
            pelletsEaten++;
        }
    }

    public synchronized int getPelletsEaten() {
        return pelletsEaten;
    }

    public synchronized int getTotalPellets() {
        return totalPellets;
    }

    public synchronized boolean isGameOver() {
        return gameOver;
    }

    public synchronized void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    public synchronized boolean isGameWon() {
        return gameWon;
    }

    public synchronized void setGameWon(boolean gameWon) {
        this.gameWon = gameWon;
    }

    public synchronized boolean isGamePaused() {
        return gamePaused;
    }

    public synchronized void setGamePaused(boolean gamePaused) {
        this.gamePaused = gamePaused;
    }

    public synchronized int getPacmanDirectionX() {
        return pacmanDirectionX;
    }

    public synchronized void setPacmanDirectionX(int pacmanDirectionX) {
        this.pacmanDirectionX = pacmanDirectionX;
    }

    public synchronized int getPacmanDirectionY() {
        return pacmanDirectionY;
    }

    public synchronized void setPacmanDirectionY(int pacmanDirectionY) {
        this.pacmanDirectionY = pacmanDirectionY;
    }

    public synchronized long getPacmanInvincibilityEndTime() {
        return pacmanInvincibilityEndTime;
    }

    public synchronized void setPacmanInvincibilityEndTime(long pacmanInvincibilityEndTime) {
        this.pacmanInvincibilityEndTime = pacmanInvincibilityEndTime;
    }

    public synchronized int getScoreMultiplier() {
        return scoreMultiplier;
    }

    public synchronized void setScoreMultiplier(int scoreMultiplier) {
        this.scoreMultiplier = scoreMultiplier;
    }

    public synchronized long getScoreMultiplierEndTime() {
        return scoreMultiplierEndTime;
    }

    public synchronized void setScoreMultiplierEndTime(long scoreMultiplierEndTime) {
        this.scoreMultiplierEndTime = scoreMultiplierEndTime;
    }

    public synchronized long getPacmanSpeedBoostEndTime() {
        return pacmanSpeedBoostEndTime;
    }

    public synchronized void setPacmanSpeedBoostEndTime(long pacmanSpeedBoostEndTime) {
        this.pacmanSpeedBoostEndTime = pacmanSpeedBoostEndTime;
    }
}
