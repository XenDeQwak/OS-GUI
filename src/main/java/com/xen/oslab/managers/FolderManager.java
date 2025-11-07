package com.xen.oslab.managers;

import com.xen.oslab.SnapOnGrid;
import com.xen.oslab.managers.storage.FolderStorageManager;
import com.xen.oslab.modules.FolderWindow;
import com.xen.oslab.objects.Folder;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
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

        attachEvents(folder);
        desktopPane.getChildren().add(folder);
    }

    public Folder createFolderAt(String name, double x, double y) {
        Folder folder = new Folder(name);
        folder.setLayoutX(x);
        folder.setLayoutY(y);
        attachEvents(folder);
        desktopPane.getChildren().add(folder);
        return folder;
    }

    private void attachEvents(Folder folder) {
        // Simple context menu for folder icons - just rename for now
        ContextMenu folderMenu = new ContextMenu();
        MenuItem renameItem = new MenuItem("Rename");
        folderMenu.getItems().add(renameItem);

        folder.setOnMousePressed(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                int[] pos = (int[]) folder.getUserData();
                if (pos != null) occupied[pos[0]][pos[1]] = false;
                folder.setUserData(null);
                folder.toFront();
            }
        });

        folder.setOnMouseDragged(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                folder.setLayoutX(e.getSceneX() - folder.getWidth() / 2);
                folder.setLayoutY(e.getSceneY() - folder.getHeight() / 2);
            }
        });

        folder.setOnMouseReleased(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                Folder target = null;
                for (var node : desktopPane.getChildren()) {
                    if (node instanceof Folder f && f != folder) {
                        if (f.getBoundsInParent().intersects(folder.getBoundsInParent())) {
                            target = f;
                            break;
                        }
                    }
                }

                if (target != null) {
                    target.addFolder(folder);
                    fsm.storeInFolder(target.getFolderName(), folder.getFolderName());
                    fsm.saveFolder(target, true);
                    desktopPane.getChildren().remove(folder);
                } else {
                    snapper.snap(folder);
                }
            }
        });

        folder.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                folderMenu.show(folder, e.getScreenX(), e.getScreenY());
                e.consume();
            } else if (e.getClickCount() == 2 && e.getButton() == MouseButton.PRIMARY) {
                new FolderWindow(folder, fileManager, fsm);
            } else {
                folderMenu.hide();
            }
        });

        folder.getRenameField().setOnAction(e -> folder.finishRename());
        folder.getRenameField().focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (!isNowFocused) folder.finishRename();
        });

        renameItem.setOnAction(e-> {
            folder.startRename();
        });
    }

}