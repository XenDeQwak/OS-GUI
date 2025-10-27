package com.xen.oslab;

import com.xen.oslab.managers.DesktopMenuManager;
import com.xen.oslab.managers.FileManager;
import com.xen.oslab.managers.GridManager;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;

public class OSController {

    @FXML
    private Pane desktopPane;

    private final int cols = 10;
    private final int rows = 5;
    private final double cellW = 80;
    private final double cellH = 100;
    private final boolean[][] occupied = new boolean[rows][cols];

    private GridManager grid;
    private FileManager fileManager;
    private SnapOnGrid snapper;

    @FXML
    public void initialize() {
        grid = new GridManager(rows, cols, cellW, cellH, occupied);
        snapper = new SnapOnGrid(grid);
        fileManager = new FileManager(desktopPane, snapper, occupied, cellW, cellH);

        new DesktopMenuManager(desktopPane, this::addNewFile);
    }

    private void addNewFile() {
        int[] free = grid.findNearestFree(0, 0);
        if (free.length == 0) {
            System.out.println("Desktop full");
            return;
        }
        fileManager.createFile("New File " + (desktopPane.getChildren().size() + 1), free[0], free[1]);
    }

}
