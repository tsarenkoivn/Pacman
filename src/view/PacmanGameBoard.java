package view;

import model.GameModel;
import util.PacmanMove;

import javax.swing.*;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class PacmanGameBoard extends JTable {
    private PacmanBoardModel pacmanBoardModel;
    private JFrame parentFrame;
    private PacmanMove pacmanMoveListener;

    public PacmanGameBoard(PacmanBoardModel pacmanBoardModel, JFrame parentFrame) {
        super(pacmanBoardModel);
        this.pacmanBoardModel = pacmanBoardModel;
        this.parentFrame = parentFrame;
        this.pacmanMoveListener = new PacmanMove();
        addKeyListener(pacmanMoveListener);
        setFocusable(true);
        requestFocusInWindow();
    }

    public void setupBoard() {
        // Adjusted denominator to account for the removed status row from the table model
        // The total height should be divided by the actual number of game rows
        setRowHeight(parentFrame.getHeight() / getRowCount());
        for (int i = 0; i < getColumnCount(); i++) {
            TableColumn column = getColumnModel().getColumn(i);
            column.setPreferredWidth(parentFrame.getWidth() / getColumnCount());
        }

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                setRowHeight(parentFrame.getHeight() / getRowCount());
                for (int i = 0; i < getColumnCount(); i++) {
                    TableColumn column = getColumnModel().getColumn(i);
                    column.setMinWidth(parentFrame.getWidth() / getColumnCount());
                }
            }
        });

        setShowGrid(false);
        setBackground(Color.black);
    }

    public PacmanMove getPacmanMoveListener() {
        return pacmanMoveListener;
    }
}
