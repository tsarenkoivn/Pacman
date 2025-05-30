package view;

import javax.swing.table.AbstractTableModel;
import model.GameModel;

public class PacmanBoardModel extends AbstractTableModel {
    private GameModel gameModel;
    private int rows;
    private int columns;

    public PacmanBoardModel(GameModel gameModel) {
        this.gameModel = gameModel;
        this.rows = gameModel.getMatrix().length + 1;
        this.columns = gameModel.getMatrix()[0].length;
    }

    @Override
    public int getRowCount() {
        return rows;
    }

    @Override
    public int getColumnCount() {
        return columns;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return null;
    }

    public GameModel getGameModel() {
        return gameModel;
    }
}
