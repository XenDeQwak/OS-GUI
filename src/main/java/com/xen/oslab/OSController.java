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
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class OSController {
    @FXML
    private Pane desktopPane;
    @FXML
    private HBox taskbar;
    private Rectangle selectionRectangle;
    private double startX, startY;

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
    
    private Stage stage;

    @FXML
    public void initialize() {
        grid = new GridManager(rows, cols, cellW, cellH, occupied);
        snapper = new SnapOnGrid(grid);
        fileStorage = new FileStorageManager();
        folderStorage = new FolderStorageManager();
        fileManager = new FileManager(desktopPane, snapper, occupied, cellW, cellH);
        folderManager = new FolderManager(desktopPane, snapper, occupied, cellW, cellH, fileManager, folderStorage, selectedItems);
        bgStorage = new BackgroundStorageManager();
        settingsManager = new SettingsManager();
        

        desktopPane.setOnMousePressed(e -> {
            handleDesktopMousePressed(e);
            desktopPane.requestFocus();
            });
        desktopPane.setOnMouseDragged(this::handleDesktopMouseDragged);
        desktopPane.setOnMouseReleased(this::handleDesktopMouseReleased);

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
        folderManager.fsm.loadAll(folderManager);

        desktopPane.widthProperty().addListener((obs, oldV, newV) -> applySavedBackground());
        desktopPane.heightProperty().addListener((obs, oldV, newV) -> applySavedBackground());
        applySavedBackground();

        Taskbar tb = new Taskbar(
            folderManager,
            () -> settingsManager.openSettings(),
            () -> System.exit(0), settingsManager
        );
        taskbar.getChildren().setAll(tb.getNode().getChildren());

        desktopPane.setFocusTraversable(true);
        desktopPane.setOnKeyPressed(this::handleDesktopKeyPressed);
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

    private void clearSelection() {
        for (javafx.scene.Node node : selectedItems) {
            node.setStyle(null);
        }
        selectedItems.clear();
    }

    private void handleDesktopMousePressed(javafx.scene.input.MouseEvent e) {
        if (e.getTarget() == desktopPane && e.isPrimaryButtonDown()) {
            clearSelection();
            startX = e.getX();
            startY = e.getY();
            selectionRectangle = new javafx.scene.shape.Rectangle(startX, startY, 0, 0);
            selectionRectangle.setStyle(SELECTION_BOX_STYLE);
            selectionRectangle.setMouseTransparent(true);
            desktopPane.getChildren().add(selectionRectangle);
        }
    }
    private void handleDesktopMouseDragged(javafx.scene.input.MouseEvent e) {
        if (selectionRectangle == null || !e.isPrimaryButtonDown()) {
            return;
        }
        double currentX = e.getX();
        double currentY = e.getY();
        double newX = Math.min(startX, currentX);
        double newY = Math.min(startY, currentY);
        double width = Math.abs(startX - currentX);
        double height = Math.abs(startY - currentY);
        selectionRectangle.setX(newX);
        selectionRectangle.setY(newY);
        selectionRectangle.setWidth(width);
        selectionRectangle.setHeight(height);
    }
    private void handleDesktopMouseReleased(javafx.scene.input.MouseEvent e) {
        if (selectionRectangle == null) {
            return;
        }
        selectItemsInRect(selectionRectangle.getBoundsInParent());
        desktopPane.getChildren().remove(selectionRectangle);
        selectionRectangle = null;
    }

    private void selectItemsInRect(javafx.geometry.Bounds selectionBounds) {
        for (javafx.scene.Node node : desktopPane.getChildren()) {
            if (node instanceof File || node instanceof Folder) {
                if (node.getBoundsInParent().intersects(selectionBounds)) {
                    selectedItems.add(node);
                    node.setStyle(SELECTED_ICON_STYLE);
                }
            }
        }
    }

    private void handleDesktopKeyPressed(javafx.scene.input.KeyEvent e) {
        if (e.getCode() == javafx.scene.input.KeyCode.DELETE) {
            java.util.List<javafx.scene.Node> itemsToDelete = new java.util.ArrayList<>(selectedItems);
            for (javafx.scene.Node item : itemsToDelete) {
                if (item instanceof File) {
                    // Use the new method we created
                    fileManager.deleteFile((File) item);
                } else if (item instanceof Folder) {
                    Folder folder = (Folder) item;
                    if (folder.isDeletable()) {
                        folderManager.deleteFolder(folder);
                    }
                }
            }
            selectedItems.clear(); 
        }
    }
    public void setStage(Stage stage) {
        this.stage = stage;

        this.stage.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
        if (!isNowFocused) {
            cancelDragSelection();
        }
        });
    }
    private void cancelDragSelection() {
        if (selectionRectangle != null) {
            desktopPane.getChildren().remove(selectionRectangle);
            selectionRectangle = null;
        }
    }
    

    private final java.util.List<javafx.scene.Node> selectedItems = new java.util.ArrayList<>();
    private final String SELECTION_BOX_STYLE = "-fx-fill: rgba(0, 120, 215, 0.3); -fx-stroke: rgba(0, 120, 215, 0.7); -fx-stroke-width: 1;";
    private final String SELECTED_ICON_STYLE = "-fx-effect: dropshadow(gaussian, rgba(40, 147, 255, 0.8), 15, 0.6, 0, 0);";

}
