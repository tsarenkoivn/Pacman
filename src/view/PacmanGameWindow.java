package view;

import controller.GameController;
import model.GameModel;
import util.GenerateBoard;
import util.PacmanMove;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class PacmanGameWindow extends JFrame {

    public PacmanGameWindow() {
        setTitle("Pac-Man Game");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    public void gameWindow(int width, int height) {
        setSize(width, height);
        displayMainMenu();
        setVisible(true);
    }

    private void displayMainMenu() {
        getContentPane().removeAll();
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new GridBagLayout());
        menuPanel.setBackground(Color.BLACK);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JButton newGameButton = new JButton("New Game");
        styleButton(newGameButton);
        newGameButton.addActionListener(e -> showNewGameDialog());

        JButton highScoresButton = new JButton("High Scores");
        styleButton(highScoresButton);
        highScoresButton.addActionListener(e -> showHighScores());

        JButton exitButton = new JButton("Exit");
        styleButton(exitButton);
        exitButton.addActionListener(e -> System.exit(0));

        gbc.gridx = 0;
        gbc.gridy = 0;
        menuPanel.add(newGameButton, gbc);

        gbc.gridy = 1;
        menuPanel.add(highScoresButton, gbc);

        gbc.gridy = 2;
        menuPanel.add(exitButton, gbc);

        add(menuPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 20));
        button.setBackground(new Color(255, 200, 0));
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(Color.ORANGE, 3));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void showNewGameDialog() {
        JDialog dialog = new JDialog(this, "New Game Settings", true);
        dialog.setLayout(new GridBagLayout());
        dialog.getContentPane().setBackground(Color.DARK_GRAY);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel columnsLabel = new JLabel("Columns (10-100):");
        columnsLabel.setForeground(Color.WHITE);
        JTextField columnsField = new JTextField("20", 5);
        columnsField.setBackground(Color.LIGHT_GRAY);

        JLabel rowsLabel = new JLabel("Rows (10-100):");
        rowsLabel.setForeground(Color.WHITE);
        JTextField rowsField = new JTextField("20", 5);
        rowsField.setBackground(Color.LIGHT_GRAY);

        JButton playButton = new JButton("Play");
        styleButton(playButton);
        playButton.addActionListener(e -> {
            try {
                int columns = Integer.parseInt(columnsField.getText());
                int rows = Integer.parseInt(rowsField.getText());

                if (columns >= 10 && columns <= 100 && rows >= 10 && rows <= 100) {
                    dialog.dispose();
                    startGame(rows, columns);
                } else {
                    JOptionPane.showMessageDialog(dialog, "Please enter values between 10 and 100 for rows and columns.", "Invalid Input", JOptionPane.WARNING_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid input. Please enter numbers for rows and columns.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 0;
        dialog.add(columnsLabel, gbc);
        gbc.gridx = 1;
        dialog.add(columnsField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        dialog.add(rowsLabel, gbc);
        gbc.gridx = 1;
        dialog.add(rowsField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        dialog.add(playButton, gbc);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void startGame(int rows, int columns) {
        getContentPane().removeAll();
        setVisible(false);

        JFrame gameFrame = new JFrame("Pac-Man Game");
        gameFrame.setSize(getWidth(), getHeight());
        gameFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        gameFrame.setLocationRelativeTo(null);

        GameModel gameModel = new GameModel(rows, columns);
        // PacmanBoardModel now only has 'rows' for the game grid, not +1
        PacmanBoardModel pacmanBoardModel = new PacmanBoardModel(gameModel);
        PacmanGameBoard pacmanGameBoard = new PacmanGameBoard(pacmanBoardModel, gameFrame);
        CellRenderer cellRenderer = new CellRenderer(gameModel, pacmanGameBoard.getPacmanMoveListener());

        for (int j = 0; j < pacmanGameBoard.getColumnCount(); j++) {
            pacmanGameBoard.getColumnModel().getColumn(j).setCellRenderer(cellRenderer);
        }

        gameFrame.add(new JScrollPane(pacmanGameBoard), BorderLayout.CENTER);
        pacmanGameBoard.setupBoard();

        // Create status panel
        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 50, 5));
        statusBar.setBackground(Color.BLACK);
        JLabel scoreLabel = new JLabel("Score: 0");
        JLabel livesLabel = new JLabel("Lives: 3");
        JLabel timeLabel = new JLabel("Time: 0s");

        Font statusFont = new Font("Monospaced", Font.BOLD, 20);
        scoreLabel.setForeground(Color.WHITE);
        livesLabel.setForeground(Color.WHITE);
        timeLabel.setForeground(Color.WHITE);
        scoreLabel.setFont(statusFont);
        livesLabel.setFont(statusFont);
        timeLabel.setFont(statusFont);

        statusBar.add(scoreLabel);
        statusBar.add(livesLabel);
        statusBar.add(timeLabel);

        gameFrame.add(statusBar, BorderLayout.SOUTH); // Add status bar to the bottom

        // Pass labels to GameController for updating
        GameController gameController = new GameController(gameModel, pacmanGameBoard, this, cellRenderer, scoreLabel, livesLabel, timeLabel);

        gameFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                gameController.stopGame();
            }
        });

        gameFrame.setVisible(true);
        pacmanGameBoard.requestFocusInWindow();
        gameController.startGame();
    }

    private void showHighScores() {
        JDialog dialog = new JDialog(this, "High Scores", true);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(Color.BLACK);

        SerializableModel serializableModel = new SerializableModel();
        JList<String> highScoresList = new JList<>(serializableModel);
        highScoresList.setBackground(Color.DARK_GRAY);
        highScoresList.setForeground(Color.WHITE);
        highScoresList.setFont(new Font("Monospaced", Font.PLAIN, 18));
        highScoresList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        highScoresList.setLayoutOrientation(JList.VERTICAL);

        highScoresList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                label.setHorizontalAlignment(CENTER);
                return label;
            }
        });


        JScrollPane scrollPane = new JScrollPane(highScoresList);
        scrollPane.getViewport().setBackground(Color.DARK_GRAY);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        dialog.add(scrollPane, BorderLayout.CENTER);

        JButton backButton = new JButton("Back to Menu");
        styleButton(backButton);
        backButton.addActionListener(e -> dialog.dispose());
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.BLACK);
        buttonPanel.add(backButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setSize(500, 700);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
}
