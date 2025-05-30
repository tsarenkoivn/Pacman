package model;

import util.GhostPath;
import java.util.ArrayList;
import java.util.List;

public class Ghost {
    public List<int[]> path = new ArrayList<>();
    public int currentX;
    public int currentY;
    private int start_i;
    private int start_j;
    private int end_i;
    private int end_j;
    private boolean frozen;
    private long freezeEndTime;

    public Ghost(int[][] matrix, int continue_i, int continue_j){
        boolean zero = false;
        if(continue_i == -1 && continue_j == -1) {
            for (int i = matrix.length - 1; i >= 0; i = i - 1) {
                for (int j = matrix[i].length - 1; j >= 0; j = j - 1) {
                    if (matrix[i][j] == 0 || matrix[i][j] == 2) {
                        zero = true;
                        start_i = i;
                        start_j = j;
                        break;
                    }
                }
                if(zero){
                    break;
                }
            }
        }else{
            start_i = continue_i;
            start_j = continue_j;
        }
        currentX = start_j;
        currentY = start_i;

        zero = false;
        while(!zero){
            int i = (int)(Math.random()* matrix.length);
            int j = (int)(Math.random()* matrix[0].length);
            end_i = i;
            end_j = j;
            if(matrix[i][j] == 0 || matrix[i][j] == 2){
                zero = true;
            }
        }
        GhostPath ghostPath = new GhostPath(start_i,start_j,end_i,end_j,matrix);
        path = ghostPath.path;
        this.frozen = false;
        this.freezeEndTime = 0;
    }

    public void setPosition(int y, int x) {
        this.currentY = y;
        this.currentX = x;
    }

    public int getStart_i() {
        return start_i;
    }

    public int getStart_j() {
        return start_j;
    }

    public void setFrozen(boolean frozen) {
        this.frozen = frozen;
        if (frozen) {
            this.freezeEndTime = System.currentTimeMillis() + 5000;
        } else {
            this.freezeEndTime = 0;
        }
    }

    public boolean isFrozen() {
        return frozen && System.currentTimeMillis() < freezeEndTime;
    }

    public long getFreezeEndTime() {
        return freezeEndTime;
    }
}
