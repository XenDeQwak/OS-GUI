package com.xen.oslab.managers;

import com.xen.oslab.SnapOnGrid;
import com.xen.oslab.managers.storage.FolderStorageManager;
import com.xen.oslab.modules.FolderWindow;
import com.xen.oslab.objects.Folder;
import com.xen.oslab.utils.FolderEventUtils;
import javafx.scene.control.ContextMenu;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

public class FolderManager {
    private final Pane desktopPane;
    private final SnapOnGrid snapper;
    private final boolean[][] occupied;
    private final double cellW, cellH;
    private final FileManager fileManager;
    private final FolderStorageManager fsm;

    public FolderManager(Pane desktopPane, SnapOnGrid snapper, boolean[][] occupied, double cellW, double cellH, FileManager fileManager, FolderStorageManager fsm) {
        this.desktopPane = desktopPane;
        this.snapper = snapper;
        this.occupied = occupied;
        this.cellW = cellW;
        this.cellH = cellH;
        this.fileManager = fileManager;
        this.fsm = fsm;
    }

    public void createFolder(String name, int row, int col) {
        Folder folder = new Folder(name);
        double x = col * cellW;
        double y = row * cellH;
        folder.setLayoutX(x);
        folder.setLayoutY(y);
        folder.setUserData(new int[]{row, col});
        occupied[row][col] = true;

        attachEvents(folder, false);
        desktopPane.getChildren().add(folder);
    }

    public Folder createFolderAt(String name, double x, double y) {
        Folder folder = new Folder(name);
        folder.setLayoutX(x);
        folder.setLayoutY(y);
        attachEvents(folder, true);
        desktopPane.getChildren().add(folder);
        return folder;
    }


   private void attachEvents(Folder folder, boolean isLoaded) {
        if (!isLoaded) markOccupied(folder);

        folder.setOnMousePressed(e -> handleFolderPress(folder, e));
        folder.setOnMouseDragged(e -> handleFolderDrag(folder, e));
        folder.setOnMouseReleased(e -> handleFolderRelease(folder));

        ContextMenu menu = FolderEventUtils.createRenameContextMenu(folder, () -> fsm.saveFolder(folder));

        folder.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                FolderEventUtils.showMenu(menu, e.getScreenX(), e.getScreenY(), folder);
                e.consume();
            } else if (e.getClickCount() == 2 && e.getButton() == MouseButton.PRIMARY) {
                new FolderWindow(folder, fileManager, fsm);
            }
        });
    }

    private void markOccupied(Folder folder) {
        int row = (int) (folder.getLayoutY() / cellH);
        int col = (int) (folder.getLayoutX() / cellW);
        if (row >= 0 && col >= 0 && row < occupied.length && col < occupied[0].length)
            occupied[row][col] = true;
        folder.setUserData(new int[]{row, col});
    }

    private void handleFolderPress(Folder folder, MouseEvent e) {
        if (e.getButton() == MouseButton.PRIMARY) {
            int[] pos = (int[]) folder.getUserData();
            if (pos != null) occupied[pos[0]][pos[1]] = false;
            folder.setUserData(null);
            folder.toFront();
        }
    }

    private void handleFolderDrag(Folder folder, MouseEvent e) {
        if (e.getButton() == MouseButton.PRIMARY) {
            folder.setLayoutX(e.getSceneX() - folder.getWidth() / 2);
            folder.setLayoutY(e.getSceneY() - folder.getHeight() / 2);
        }
    }

    private void handleFolderRelease(Folder folder) {
        Folder target = null;
        for (var node : desktopPane.getChildren()) {
            if (node instanceof Folder f && f != folder && f.getBoundsInParent().intersects(folder.getBoundsInParent())) {
                target = f;
                break;
            }
        }

        if (target != null) {
            target.addFolder(folder);
            if (!fsm.isLoading()) {
                fsm.storeInFolder(target.getFolderName(), folder.getFolderName());
                fsm.saveFolder(target);
            }
            desktopPane.getChildren().remove(folder);
        } else {
            snapper.snap(folder);
        }
    }
}