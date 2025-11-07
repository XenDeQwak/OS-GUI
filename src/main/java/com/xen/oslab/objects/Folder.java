package com.xen.oslab.objects;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.xen.oslab.managers.LoadManager;
import com.xen.oslab.managers.storage.FolderStorageManager;

import javafx.geometry.Pos;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class Folder extends VBox {
    private String folderName;
    private final List<File> files = new ArrayList<>();
    private final List<Folder> subFolders = new ArrayList<>();
    private final FolderStorageManager storage;
    private TextField renameFolder;
    private Label label;

    public Folder(String name) {
        this.folderName = name;
        this.storage = new FolderStorageManager();

        LoadManager image = new LoadManager();

        Image folderIcon = image.load("Folder.png");
        ImageView folderImage = new ImageView(folderIcon);
        folderImage.setFitWidth(48);
        folderImage.setFitHeight(50);

        label = new Label(name);
        label.setAlignment(Pos.CENTER);
        label.setWrapText(true);
        label.setMaxWidth(80);
        label.setTextFill(Color.WHITE);


        renameFolder = new TextField(folderName);
        renameFolder.setAlignment(Pos.CENTER);
        renameFolder.setMaxWidth(80);
        renameFolder.setVisible(false);
        getChildren().addAll(folderImage, label, renameFolder);

        ContextMenu contextMenu = new ContextMenu();
        MenuItem renameItem = new MenuItem("Rename Folder");
        renameItem.setOnAction(e -> startRename());
        contextMenu.getItems().add(renameItem);
        label.setOnContextMenuRequested(e -> {
            contextMenu.show(label, e.getScreenX(), e.getScreenY());
            e.consume();
        });

        renameFolder.setOnAction(e -> finishRename());
        renameFolder.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (!isNowFocused) finishRename();
        });

        setAlignment(Pos.CENTER);
        setSpacing(5);
    }

    private void startRename() {
        renameFolder.setText(folderName);
        label.setVisible(false);
        renameFolder.setVisible(true);
        renameFolder.requestFocus();
        renameFolder.selectAll();
    }

    private void finishRename() {
        String newName = renameFolder.getText().trim();
        String oldName = folderName;

        if (!newName.isEmpty() && !newName.equals(oldName)) {
            folderName = newName;
            label.setText(newName);

            storage.renameFolder(oldName, newName);
            storage.saveFolder(this);
        }
        renameFolder.setVisible(false);
        label.setVisible(true);
    }

    public void addFile(File f) {
        files.add(f);
    }

    public void addFolder(Folder folder) {
        subFolders.add(folder);
    }

    public List<File> getFiles() {
        return files;
    }

    public List<Folder> getSubFolders() {
        return subFolders;
    }

    public FolderStorageManager getStorage() {
        return storage;
    }

    public String getFolderName() {
        return folderName;
    }
}

