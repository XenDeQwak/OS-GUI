package com.xen.oslab;

import com.xen.oslab.managers.GridManager;
import javafx.scene.Node;

public class SnapOnGrid {
    private final GridManager grid;

    public SnapOnGrid(GridManager grid) {
        this.grid = grid;
    }

    public void snap(Node node) {
        double x = node.getLayoutX();
        double y = node.getLayoutY();

        int col = grid.clampCol(x);
        int row = grid.clampRow(y);

        int[] old = (int[]) node.getUserData();
        if (old != null) grid.free(old[0], old[1]);

        if (grid.isOccupied(row, col)) {
            int[] free = grid.findNearestFree(row, col);
            if (free.length == 0) {
                if (old != null) {
                    node.setLayoutX(grid.toX(old[1]));
                    node.setLayoutY(grid.toY(old[0]));
                    grid.occupy(old[0], old[1]);
                }
                return;
            }
            row = free[0];
            col = free[1];
        }

        node.setLayoutX(grid.toX(col));
        node.setLayoutY(grid.toY(row));
        node.setUserData(new int[]{row, col});
        grid.occupy(row, col);
    }
}
