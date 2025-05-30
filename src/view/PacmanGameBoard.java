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
        setRowHeight(parentFrame.getHeight() / (getRowCount() + 1));
        for (int i = 0; i < getColumnCount(); i++) {
            TableColumn column = getColumnModel().getColumn(i);
            column.setPreferredWidth(parentFrame.getWidth() / getColumnCount());
        }

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                setRowHeight(parentFrame.getHeight() / (getRowCount() + 1));
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
