package com.xen.oslab.modules;

import com.xen.oslab.managers.FileManager;
import com.xen.oslab.managers.storage.FolderStorageManager;
import com.xen.oslab.objects.File;
import com.xen.oslab.objects.Folder;
import com.xen.oslab.utils.FolderEventUtils;

import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class FolderWindow {
    private ContextMenu openMenu = null;

    public FolderWindow(Folder folder, FileManager fileManager, FolderStorageManager fsm) {
        FlowPane contentPane = new FlowPane(20, 20);
        contentPane.setStyle("-fx-background-color: #2b2b2b;");
        contentPane.setPickOnBounds(true);

        ScrollPane scroll = new ScrollPane(contentPane);
        scroll.setFitToWidth(true);
        scroll.setFitToHeight(true);
        contentPane.prefWidthProperty().bind(scroll.widthProperty().subtract(20));
        contentPane.prefHeightProperty().bind(scroll.heightProperty().subtract(20));

        ContextMenu mainMenu = new ContextMenu();
        MenuItem newFile = new MenuItem("New File");
        MenuItem newFolder = new MenuItem("New Folder");
        mainMenu.getItems().addAll(newFile, newFolder);

        contentPane.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                showMenu(mainMenu, e.getScreenX(), e.getScreenY(), contentPane);
            } else {
                if (mainMenu.isShowing()) mainMenu.hide();
            }
        });

        newFile.setOnAction(e -> {
            File file = fileManager.createFileInFolder(folder, contentPane, "New File");
            folder.addFile(file);
        });

        newFolder.setOnAction(e -> {
            Folder sub = new Folder("New Folder");
            sub.setParentFolder(folder);
            folder.addFolder(sub);
            attachSubfolderEvents(sub, fileManager, fsm);
            contentPane.getChildren().add(sub);
            fsm.saveFolder(folder);
        });

        for (File file : folder.getFiles()) {
            fileManager.attachEvents(file, false);
            contentPane.getChildren().add(file);
        }

        for (Folder sub : folder.getSubFolders()) {
            attachSubfolderEvents(sub, fileManager, fsm);
            contentPane.getChildren().add(sub);
        }

        Stage stage = new Stage();
        stage.setTitle(folder.getFolderName());
        stage.initModality(Modality.NONE);
        stage.setScene(new Scene(scroll, 500, 400));
        stage.setOnCloseRequest(e -> {
            Folder root = getRootFolder(folder);
            fsm.saveFolder(root);
        });
        stage.show();
    }

    private void attachSubfolderEvents(Folder sub, FileManager fm, FolderStorageManager fsm) {
        ContextMenu menu = FolderEventUtils.createRenameContextMenu(sub, () -> fsm.saveFolder(getRootFolder(sub)));

        sub.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                FolderEventUtils.showMenu(menu, e.getScreenX(), e.getScreenY(), sub);
                e.consume();
            } else if (e.getClickCount() == 2 && e.getButton() == MouseButton.PRIMARY) {
                new FolderWindow(sub, fm, fsm);
            }
        });
    }


    private Folder getRootFolder(Folder folder) {
        Folder current = folder;
        while (current.getParentFolder() != null) {
            current = current.getParentFolder();
        }
        return current;
    }

    private void showMenu(ContextMenu menu, double x, double y, javafx.scene.Node node) {
        if (openMenu != null && openMenu.isShowing()) openMenu.hide();
        openMenu = menu;
        menu.show(node, x, y);
        menu.setOnHidden(e -> openMenu = null);
    }
}

