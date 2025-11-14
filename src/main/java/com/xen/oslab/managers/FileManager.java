package com.xen.oslab.managers;

import com.xen.oslab.managers.storage.FileStorageManager;
import com.xen.oslab.modules.FileEditor;
import com.xen.oslab.objects.File;
import com.xen.oslab.objects.Folder;
import com.xen.oslab.utils.FileEventUtils;
import com.xen.oslab.utils.SnapOnGrid;

import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
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
        attachEvents(file, true);
        desktopPane.getChildren().add(file);
    }

    public void attachEvents(File file, boolean isDesktop) {
        if (isDesktop) attachDesktopEvents(file);
        attachCommonEvents(file);
    }

    private void attachDesktopEvents(File file) {
        file.setOnMousePressed(e -> handlePress(file));
        file.setOnMouseDragged(e -> handleDrag(file, e));
        file.setOnMouseReleased(e -> handleRelease(file));
    }

    private void handlePress(File file) {
        int[] pos = (int[]) file.getUserData();
        if (pos != null) occupied[pos[0]][pos[1]] = false;
        file.setUserData(null);
        file.toFront();
    }

    private void handleDrag(File file, MouseEvent e) {
        file.setLayoutX(e.getSceneX() - file.getWidth() / 2);
        file.setLayoutY(e.getSceneY() - file.getHeight() / 2);
    }

    private void handleRelease(File file) {
        boolean insideFolder = false;
        for (Node node : desktopPane.getChildren()) {
            if (node instanceof Folder folder && file.getBoundsInParent().intersects(folder.getBoundsInParent())) {
                folder.addFile(file);
                file.setParentFolder(folder);
                desktopPane.getChildren().remove(file);
                storage.deleteFromDesktop(file.getFileId());
                folder.getStorage().saveFolder(folder);
                insideFolder = true;
                break;
            }
        }
        if (!insideFolder) snapper.snap(file);
    }

    private void attachCommonEvents(File file) {
        ContextMenu menu = FileEventUtils.createRenameContextMenu(file, () -> {
            if (file.getParentFolder() != null) file.getParentFolder().getStorage().saveFolder(file.getParentFolder());
            //else storage.saveFile(file);
        });

        file.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                FileEventUtils.showMenu(menu, e.getScreenX(), e.getScreenY(), file);
                e.consume();
            } else if (e.getClickCount() == 2 && e.getButton() == MouseButton.PRIMARY) {
                FileEditor.open(file, () -> {
                    if (file.getParentFolder() != null) file.getParentFolder().getStorage().saveFolder(file.getParentFolder());
                    //else storage.saveFile(file);
                });
            }
        });
    }



    public File createFileInFolder(Folder folder, FlowPane contentPane, String fileName) {
        File file = new File(fileName);
        file.setParentFolder(folder);
        attachEvents(file, false);
        contentPane.getChildren().add(file);
        folder.addFile(file);
        folder.getStorage().saveFolder(folder);
        return file;
    }

    public void markOccupied(File file) {
        int row = (int) (file.getLayoutY() / cellH);
        int col = (int) (file.getLayoutX() / cellW);
        if (row >= 0 && row < occupied.length && col >= 0 && col < occupied[0].length)
            occupied[row][col] = true;
    }

    public double getCellW() { return cellW; }
    public double getCellH() { return cellH; }



}
