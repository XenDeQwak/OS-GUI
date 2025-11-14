package com.xen.oslab;

import java.util.Map;

import com.xen.oslab.managers.DesktopMenuManager;
import com.xen.oslab.managers.FileManager;
import com.xen.oslab.managers.FolderManager;
import com.xen.oslab.managers.GridManager;
import com.xen.oslab.managers.SettingsManager;
import com.xen.oslab.managers.storage.BackgroundStorageManager;
import com.xen.oslab.managers.storage.FileStorageManager;
import com.xen.oslab.managers.storage.FolderStorageManager;
import com.xen.oslab.modules.Taskbar;
import com.xen.oslab.objects.File;
import com.xen.oslab.objects.Folder;
import com.xen.oslab.utils.SnapOnGrid;

import javafx.fxml.FXML;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

public class OSController {
    @FXML
    private Pane desktopPane;
    @FXML
    private HBox taskbar;

    private GridManager grid;
    private FileManager fileManager;
    private SnapOnGrid snapper;
    private FileStorageManager fileStorage;
    private FolderStorageManager folderStorage;
    private FolderManager folderManager;
    private BackgroundStorageManager bgStorage;
    private SettingsManager settingsManager;

    private final int cols = 10;
    private final int rows = 5;
    private final double cellW = 80;
    private final double cellH = 100;
    private final boolean[][] occupied = new boolean[rows][cols];

    @FXML
    public void initialize() {
        grid = new GridManager(rows, cols, cellW, cellH, occupied);
        snapper = new SnapOnGrid(grid);
        fileStorage = new FileStorageManager();
        folderStorage = new FolderStorageManager();
        fileManager = new FileManager(desktopPane, snapper, occupied, cellW, cellH);
        folderManager = new FolderManager(desktopPane, snapper, occupied, cellW, cellH, fileManager, folderStorage);
        bgStorage = new BackgroundStorageManager();
        settingsManager = new SettingsManager();

        Menu bgMenu = new Menu("Change Background");
        String[] backgrounds = {"sky.jpg", "mountain.jpg", "city.jpg"};
        for (String bg : backgrounds) {
            MenuItem item = new MenuItem(bg);
            item.setOnAction(e -> {
                setDesktopBackground(bg);
                bgStorage.saveBackground(bg);
            });
            bgMenu.getItems().add(item);
        }

        new DesktopMenuManager(desktopPane, Map.of(
            "New File", this::addNewFile,
            "New Folder", this::addNewFolder
        ), bgMenu);

        fileStorage.loadAll(desktopPane, fileManager);
        folderStorage.loadAll(folderManager);

        desktopPane.widthProperty().addListener((obs, oldV, newV) -> applySavedBackground());
        desktopPane.heightProperty().addListener((obs, oldV, newV) -> applySavedBackground());
        applySavedBackground();

        Taskbar tb = new Taskbar(
            () -> settingsManager.openSettings(),
            () -> System.exit(0)
        );
        taskbar.getChildren().setAll(tb.getNode().getChildren());
    }

    private void applySavedBackground() {
        String savedBg = bgStorage.loadBackground();
        if (savedBg != null && !savedBg.isEmpty()) {
            setDesktopBackground(savedBg);
        }
    }

    private void setDesktopBackground(String bgFile) {
        Image img = new Image(getClass().getResource("/com/xen/oslab/backgrounds/" + bgFile).toExternalForm(),
                desktopPane.getWidth(), desktopPane.getHeight(), false, true);
        BackgroundImage bg = new BackgroundImage(img,
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(100, 100, true, true, true, false));
        desktopPane.setBackground(new Background(bg));
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
}
