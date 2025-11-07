package com.xen.oslab.modules;

import com.xen.oslab.managers.FileManager;
import com.xen.oslab.managers.storage.FolderStorageManager;
import com.xen.oslab.objects.File;
import com.xen.oslab.objects.Folder;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.FlowPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class FolderWindow {
    public FolderWindow(Folder folder, FileManager fileManager, FolderStorageManager fsm) {
        FlowPane contentPane = new FlowPane(20, 20);
        contentPane.setStyle("-fx-background-color: #2b2b2b;");
        contentPane.setPickOnBounds(true);

        ScrollPane scroll = new ScrollPane(contentPane);
        scroll.setFitToWidth(true);
        scroll.setFitToHeight(true);

        contentPane.prefWidthProperty().bind(scroll.widthProperty().subtract(20));
        contentPane.prefHeightProperty().bind(scroll.heightProperty().subtract(20));

        ContextMenu menu = new ContextMenu();
        MenuItem newFile = new MenuItem("New File");
        MenuItem newFolder = new MenuItem("New Folder");
        menu.getItems().addAll(newFile, newFolder);

        contentPane.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.SECONDARY)
                menu.show(contentPane, e.getScreenX(), e.getScreenY());
            else menu.hide();
        });

        // newFile.setOnAction(e -> {
        //     File file = fileManager.createFileInFolder(folder, contentPane, "New File");
        //     folder.addFile(file);
        //     fsm.saveFolder(folder);
        // });

        newFolder.setOnAction(e -> {
            Folder sub = new Folder("New Folder");
            sub.setParentFolder(folder);
            folder.addFolder(sub);
            attachSubfolderEvents(sub, fileManager, fsm);
            contentPane.getChildren().add(sub);
            fsm.saveFolder(folder, false);
        });

        for (File file : folder.getFiles()) {
            File node = new File(file.getFileName());
            node.setContent(file.getContent());
            fileManager.attachEvents(node);
            contentPane.getChildren().add(node);
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
            fsm.saveFolder(root, true);
            
        });
        stage.show();
    }

    private Folder getRootFolder(Folder folder) {
        Folder current = folder;
        while (current.getParentFolder() != null) {
            current = current.getParentFolder();
        }
        return current;
    }


    private void attachSubfolderEvents(Folder sub, FileManager fm, FolderStorageManager fsm) {
        sub.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2 && e.getButton() == MouseButton.PRIMARY)
                new FolderWindow(sub, fm, fsm);
        });
    }
}
