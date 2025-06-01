package view;

import model.GameModel;
import model.Ghost;
import model.Upgrade;
import util.ImageLoader;
import util.PacmanMove;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.image.BufferedImage;

public class CellRenderer extends DefaultTableCellRenderer {
    private GameModel gameModel;
    private PacmanMove pacmanMove;
    public boolean mouthOpen = true;
    private long lastAnimationToggle = System.currentTimeMillis();
    private static final long ANIMATION_INTERVAL = 100;

    private Image pacmanOpenRight;
    private Image pacmanOpenLeft;
    private Image pacmanOpenUp;
    private Image pacmanOpenDown;
    private Image pacmanClosed;
    private Image wallImage;
    private Image pelletImage;
    private Image ghostRedImage;
    private Image ghostGrayImage;
    private Image ghostPinkImage;
    private Image ghostGreenImage;
    private Image ghostFrozenImage;

    public CellRenderer(GameModel gameModel, PacmanMove pacmanMove) {
        this.gameModel = gameModel;
        this.pacmanMove = pacmanMove;
        loadImages();
    }

    private void loadImages() {
        pacmanOpenRight = ImageLoader.loadImage("pacman_open_right.png");
        pacmanOpenLeft = ImageLoader.loadImage("pacman_open_left.png");
        pacmanOpenUp = ImageLoader.loadImage("pacman_open_up.png");
        pacmanOpenDown = ImageLoader.loadImage("pacman_open_down.png");
        pacmanClosed = ImageLoader.loadImage("pacman_closed.png");
        wallImage = ImageLoader.loadImage("wall.png");
        pelletImage = ImageLoader.loadImage("pellet.png");
        ghostRedImage = ImageLoader.loadImage("ghost_red.png");
        ghostGrayImage = ImageLoader.loadImage("ghost_gray.png");
        ghostPinkImage = ImageLoader.loadImage("ghost_pink.png");
        ghostGreenImage = ImageLoader.loadImage("ghost_green.png");
        ghostFrozenImage = ImageLoader.loadImage("ghost_frozen.png");
    }

    public void setMouthOpen(boolean mouthOpen) {
        this.mouthOpen = mouthOpen;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int cellWidth = getWidth();
                int cellHeight = getHeight();

                synchronized (gameModel) {
                    if (row < gameModel.getMatrix().length && column < gameModel.getMatrix()[0].length) {
                        int cellType = gameModel.getMatrix()[row][column];

                        if (cellType == 1) {
                            if (wallImage != null) {
                                g2d.drawImage(wallImage, 0, 0, cellWidth, cellHeight, this);
                            } else {
                                g2d.setColor(Color.BLUE);
                                g2d.fillRect(0, 0, cellWidth, cellHeight);
                            }
                        } else if (cellType == 0) {
                            if (pelletImage != null) {
                                int pelletSize = Math.min(cellWidth, cellHeight) / 3;
                                g2d.drawImage(pelletImage, (cellWidth - pelletSize) / 2, (cellHeight - pelletSize) / 2, pelletSize, pelletSize, this);
                            } else {
                                g2d.setColor(Color.WHITE);
                                int diameter = Math.min(cellWidth, cellHeight) / 5;
                                g2d.fillOval((cellWidth - diameter) / 2, (cellHeight - diameter) / 2, diameter, diameter);
                            }
                        }

                        for (Upgrade upgrade : gameModel.getActiveUpgrades()) {
                            if (upgrade.getY() == row && upgrade.getX() == column) {
                                Image upgradeImage = ImageLoader.loadImage(upgrade.getIconPath());
                                if (upgradeImage != null) {
                                    int upgradeSize = (int) (Math.min(cellWidth, cellHeight) * 0.7);
                                    g2d.drawImage(upgradeImage, (cellWidth - upgradeSize) / 2, (cellHeight - upgradeSize) / 2, upgradeSize, upgradeSize, this);
                                } else {
                                    g2d.setColor(Color.MAGENTA);
                                    g2d.fillOval((cellWidth - (cellWidth / 2)) / 2, (cellHeight - (cellHeight / 2)) / 2, cellWidth / 2, cellHeight / 2);
                                }
                            }
                        }

                        if (row == gameModel.getPacmanY() && column == gameModel.getPacmanX()) {
                            Image pacmanToDraw = null;
                            boolean isInvincible = System.currentTimeMillis() < gameModel.getPacmanInvincibilityEndTime();

                            if (isInvincible && (System.currentTimeMillis() / 100 % 2 == 0)) {
                                g2d.setColor(new Color(0, 150, 255, 150));
                                g2d.fillOval(0, 0, cellWidth, cellHeight);
                            } else {
                                if (mouthOpen) {
                                    if (gameModel.getPacmanDirectionX() == 1) pacmanToDraw = pacmanOpenRight;
                                    else if (gameModel.getPacmanDirectionX() == -1) pacmanToDraw = pacmanOpenLeft;
                                    else if (gameModel.getPacmanDirectionY() == 1) pacmanToDraw = pacmanOpenDown;
                                    else if (gameModel.getPacmanDirectionY() == -1) pacmanToDraw = pacmanOpenUp;
                                    else pacmanToDraw = pacmanOpenRight;
                                } else {
                                    pacmanToDraw = pacmanClosed;
                                }

                                if (pacmanToDraw != null) {
                                    g2d.drawImage(pacmanToDraw, 0, 0, cellWidth, cellHeight, this);
                                } else {
                                    int diameter = (int) (Math.min(cellWidth, cellHeight) * 0.8);
                                    g2d.setColor(Color.YELLOW);
                                    g2d.fillOval((cellWidth - diameter) / 2, (cellHeight - diameter) / 2, diameter, diameter);
                                    g2d.setColor(Color.BLACK);
                                    if (mouthOpen) {
                                        if (gameModel.getPacmanDirectionX() == -1) {
                                            g2d.fillArc((cellWidth - diameter) / 2, (cellHeight - diameter) / 2, diameter, diameter, 150, 60);
                                        } else if (gameModel.getPacmanDirectionX() == 1) {
                                            g2d.fillArc((cellWidth - diameter) / 2, (cellHeight - diameter) / 2, diameter, diameter, 330, 60);
                                        } else if (gameModel.getPacmanDirectionY() == -1) {
                                            g2d.fillArc((cellWidth - diameter) / 2, (cellHeight - diameter) / 2, diameter, diameter, 60, 60);
                                        } else if (gameModel.getPacmanDirectionY() == 1) {
                                            g2d.fillArc((cellWidth - diameter) / 2, (cellHeight - diameter) / 2, diameter, diameter, 240, 60);
                                        }
                                    }
                                }
                            }
                        }

                        for (int i = 0; i < gameModel.getGhosts().length; i++) {
                            Ghost ghost = gameModel.getGhosts()[i];
                            if (ghost.currentY == row && ghost.currentX == column) {
                                Image ghostToDraw = null;
                                if (ghost.isFrozen()) {
                                    ghostToDraw = ghostFrozenImage;
                                } else {
                                    switch (i) {
                                        case 0: ghostToDraw = ghostRedImage; break;
                                        case 1: ghostToDraw = ghostGrayImage; break;
                                        case 2: ghostToDraw = ghostPinkImage; break;
                                        case 3: ghostToDraw = ghostGreenImage; break;
                                    }
                                }

                                if (ghostToDraw != null) {
                                    g2d.drawImage(ghostToDraw, 0, 0, cellWidth, cellHeight, this);
                                } else {
                                    int diameter = (int) (Math.min(cellWidth, cellHeight) * 0.8);
                                    Color ghostColor = Color.BLACK;
                                    switch (i) {
                                        case 0: ghostColor = Color.RED; break;
                                        case 1: ghostColor = Color.GRAY; break;
                                        case 2: ghostColor = Color.PINK; break;
                                        case 3: ghostColor = Color.GREEN; break;
                                    }
                                    if (ghost.isFrozen()) {
                                        ghostColor = Color.CYAN;
                                    }
                                    g2d.setColor(ghostColor);
                                    g2d.fillOval((cellWidth - diameter) / 2, (cellHeight - diameter) / 2, diameter, diameter);
                                }
                            }
                        }
                    }
                }
            }
        };
        panel.setBackground(Color.black);
        return panel;
    }
}
