package com.xen.oslab.objects;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.xen.oslab.managers.LoadManager;
import com.xen.oslab.managers.storage.FolderStorageManager;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class Folder extends VBox {
    private String folderId = UUID.randomUUID().toString();
    private String folderName;
    private final List<File> files = new ArrayList<>();
    private final List<Folder> subFolders = new ArrayList<>();
    private final FolderStorageManager storage;
    private TextField renameField;
    private Label label;
    private Folder parentFolder;

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

        renameField = new TextField(folderName);
        renameField.setAlignment(Pos.CENTER);
        renameField.setMaxWidth(80);
        renameField.setVisible(false);
        getChildren().addAll(folderImage, label, renameField);

        setAlignment(Pos.CENTER);
        setSpacing(5);
    }

    public void startRename() {
        renameField.setText(folderName);
        label.setVisible(false);
        renameField.setVisible(true);
        renameField.requestFocus();
        renameField.selectAll();
    }

    public void finishRename() {
        String newName = renameField.getText().trim();
        String oldName = folderName;

        if (!newName.isEmpty() && !newName.equals(oldName)) {
            folderName = newName;
            label.setText(newName);

            storage.renameFolder(oldName, newName);
            storage.saveFolder(this, true);
        }
        renameField.setVisible(false);
        label.setVisible(true);
    }

    public TextField getRenameField() {
        return renameField;
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

    

    public Folder getParentFolder() { return parentFolder; }
    public void setParentFolder(Folder parent) { this.parentFolder = parent; }

    public String getFolderId() {
        return folderId;
    }

    public void setFolderId(String folderId) {
        this.folderId = folderId;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }
}