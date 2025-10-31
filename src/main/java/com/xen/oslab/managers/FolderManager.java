package com.xen.oslab.managers;

import com.xen.oslab.SnapOnGrid;
import com.xen.oslab.modules.FolderWindow;
import com.xen.oslab.objects.Folder;
import javafx.scene.layout.Pane;

public class FolderManager {
    private final Pane desktopPane;
    private final SnapOnGrid snapper;
    private final boolean[][] occupied;
    private final double cellW, cellH;
    private final FileStorageManager storage;

    public FolderManager(Pane desktopPane, SnapOnGrid snapper, boolean[][] occupied, double cellW, double cellH) {
        this.desktopPane = desktopPane;
        this.snapper = snapper;
        this.occupied = occupied;
        this.cellW = cellW;
        this.cellH = cellH;
        this.storage = new FileStorageManager();
    }

    public void createFolder(String name, int row, int col) {
        Folder folder = new Folder(name);
        double x = col * cellW;
        double y = row * cellH;
        folder.setLayoutX(x);
        folder.setLayoutY(y);
        folder.setUserData(new int[]{row, col});
        occupied[row][col] = true;

        attachEvents(folder);
        desktopPane.getChildren().add(folder);
    }

    private void attachEvents(Folder folder) {
        folder.setOnMousePressed(e -> {
            int[] pos = (int[]) folder.getUserData();
            if (pos != null) occupied[pos[0]][pos[1]] = false;
            folder.setUserData(null);
            folder.toFront();
        });

        folder.setOnMouseDragged(e -> {
            folder.setLayoutX(e.getSceneX() - folder.getWidth() / 2);
            folder.setLayoutY(e.getSceneY() - folder.getHeight() / 2);
        });

        folder.setOnMouseReleased(e -> snapper.snap(folder));

        folder.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                new FolderWindow(folder);
            }
        });
    }
}
