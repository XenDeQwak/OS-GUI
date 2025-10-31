package com.xen.oslab;

import java.util.Map;

import com.xen.oslab.managers.*;
import com.xen.oslab.objects.File;
import com.xen.oslab.objects.Folder;

import javafx.fxml.FXML;
import javafx.scene.layout.Pane;

public class OSController {
    @FXML
    private Pane desktopPane;

    private GridManager grid;
    private FileManager fileManager;
    private SnapOnGrid snapper;
    private FileStorageManager storageManager;
    private FolderManager folderManager;

    private final int cols = 10;
    private final int rows = 5;
    private final double cellW = 80;
    private final double cellH = 100;
    private final boolean[][] occupied = new boolean[rows][cols];

    @FXML
    public void initialize() {
        grid = new GridManager(rows, cols, cellW, cellH, occupied);
        snapper = new SnapOnGrid(grid);
        fileManager = new FileManager(desktopPane, snapper, occupied, cellW, cellH);
        folderManager = new FolderManager(desktopPane, snapper, occupied, cellW, cellH);
        storageManager = new FileStorageManager();

        new DesktopMenuManager(desktopPane, Map.of(
            "New File", this::addNewFile,
            "New Folder", this::addNewFolder
        ));
        storageManager.loadAll(desktopPane, fileManager);
    }

    private int getNextFileNumber() {
        long count = desktopPane.getChildren().stream()
            .filter(n -> n instanceof File)
            .count();
        return (int) count + 1;
    }

    private int getNextFolderNumber() {
        long count = desktopPane.getChildren().stream()
            .filter(n -> n instanceof Folder)
            .count();
        return (int) count + 1;
    }

    private void addNewFile() {
        int[] free = grid.findNearestFree(0, 0);
        if (free.length == 0) return;
        int num = getNextFileNumber();
        fileManager.createFile("New File " + num, free[0], free[1]);
    }

    private void addNewFolder() {
        int[] free = grid.findNearestFree(0, 0);
        if (free.length == 0) return;
        int num = getNextFolderNumber();
        folderManager.createFolder("New Folder " + num, free[0], free[1]);
    }


    public void saveState() {
        storageManager.saveAll(desktopPane);
    }
}
