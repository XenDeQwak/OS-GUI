package com.xen.oslab.managers;

import com.xen.oslab.SnapOnGrid;
import com.xen.oslab.managers.storage.FileStorageManager;
import com.xen.oslab.modules.FileEditor;
import com.xen.oslab.objects.File;
import com.xen.oslab.objects.Folder;

import javafx.scene.Node;
import javafx.scene.layout.Pane;

public class FileManager {
    private final Pane desktopPane;
    private final SnapOnGrid snapper;
    private final double cellW;
    private final double cellH;
    private final boolean[][] occupied;
    private final FileStorageManager storage;

    public FileManager(Pane desktopPane, SnapOnGrid snapper, boolean[][] occupied, double cellW, double cellH) {
        this.desktopPane = desktopPane;
        this.snapper = snapper;
        this.occupied = occupied;
        this.cellW = cellW;
        this.cellH = cellH;
        this.storage = new FileStorageManager();
    }

    public void createFile(String name, int row, int col) {
        File file = new File(name);
        file.setLayoutX(col * cellW);
        file.setLayoutY(row * cellH);
        file.setUserData(new int[]{row, col});
        occupied[row][col] = true;
        attachEvents(file);
        desktopPane.getChildren().add(file);
    }

    public void createFileAt(String name, double x, double y, String content) {
        File file = new File(name);
        file.setLayoutX(x);
        file.setLayoutY(y);
        file.setContent(content);
        attachEvents(file);
        desktopPane.getChildren().add(file);
    }

    public FileStorageManager getStorage() {
        return storage;
    }

    private void attachEvents(File file) {
        file.setOnMousePressed(e -> {
            int[] pos = (int[]) file.getUserData();
            if (pos != null) occupied[pos[0]][pos[1]] = false;
            file.setUserData(null);
            file.toFront();
        });

        file.setOnMouseDragged(e -> {
            file.setLayoutX(e.getSceneX() - file.getWidth() / 2);
            file.setLayoutY(e.getSceneY() - file.getHeight() / 2);
        });


        file.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                FileEditor.open(file, () -> storage.saveAll(desktopPane));
            }
        });

        file.setOnMouseReleased(e -> {
            boolean insideFolder = false;

            for (Node node : desktopPane.getChildren()) {
                if (node instanceof Folder folder) {
                    if (file.getBoundsInParent().intersects(folder.getBoundsInParent())) {
                        folder.addFile(file);
                        desktopPane.getChildren().remove(file);
                        folder.getStorage().saveFolder(folder);
                        insideFolder = true;
                        break;
                    }
                }
            }

            if (!insideFolder) snapper.snap(file);
        });


        // file.setOnMouseClicked(e-> {
        //     if (e.getButton() == MouseButton.SECONDARY) {
        //         //function for name change
        //         //DesktopMenuManager.fileMenu(file)
        //     }
        // });

    }
}
