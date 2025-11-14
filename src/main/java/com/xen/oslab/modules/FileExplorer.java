package com.xen.oslab.modules;

import com.xen.oslab.managers.FolderManager;
import com.xen.oslab.objects.File;
import com.xen.oslab.objects.Folder;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.TilePane;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.Scene;

public class FileExplorer {

    private final SplitPane root = new SplitPane();
    private final TreeView<Folder> folderTree;
    private final TilePane filePane;

    private final FolderManager folderManager;

    public FileExplorer(FolderManager folderManager) {

        this.folderManager = folderManager;

        folderTree = new TreeView<>();
        folderTree.setShowRoot(false);

        TreeItem<Folder> virtualRoot = new TreeItem<>();
        for (Folder folder : folderManager.getRootFolders()) {
            virtualRoot.getChildren().add(buildTree(folder));
        }

        folderTree.setRoot(virtualRoot);

        folderTree.setCellFactory(tv -> new javafx.scene.control.TreeCell<>() {
            @Override
            protected void updateItem(Folder item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? "" : item.getFolderName());
            }
        });

        filePane = new TilePane();
        filePane.setHgap(10);
        filePane.setVgap(10);

        folderTree.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            filePane.getChildren().clear();
            if (newSel == null) return;

            Folder folder = newSel.getValue();

            for (File f : folder.getFiles()) {
                Button fileBtn = new Button(f.getFileName());
                fileBtn.setOnAction(e -> openFile(f));
                filePane.getChildren().add(fileBtn);
            }

            for (Folder sub : folder.getSubFolders()) {
                filePane.getChildren().add(createFolderNode(sub));
            }
        });

        root.getItems().addAll(folderTree, filePane);
        root.setDividerPositions(0.3);
    }

    private TreeItem<Folder> buildTree(Folder folder) {
        TreeItem<Folder> node = new TreeItem<>(folder);

        for (Folder sub : folder.getSubFolders()) {
            node.getChildren().add(buildTree(sub));
        }

        node.setExpanded(true);
        return node;
    }

    private VBox createFolderNode(Folder folder) {
        VBox box = new VBox();
        box.setAlignment(Pos.CENTER);
        box.setSpacing(5);

        Label nameLabel = new Label(folder.getFolderName());
        nameLabel.setStyle("-fx-text-fill: white; -fx-font-size: 12;");

        Button openBtn = new Button("Open");
        openBtn.setOnAction(e -> openSubFolderWindow(folder));

        box.getChildren().addAll(nameLabel, openBtn);
        return box;
    }

    private void openSubFolderWindow(Folder folder) {
        FileExplorer explorer = new FileExplorer(folderManager);

        TreeItem<Folder> itemToSelect = findTreeItem(explorer.folderTree.getRoot(), folder);

        if (itemToSelect != null)
            explorer.folderTree.getSelectionModel().select(itemToSelect);

        Stage stage = new Stage();
        stage.setTitle(folder.getFolderName());
        stage.setScene(new Scene(explorer.getNode(), 600, 400));
        stage.show();
    }

    private TreeItem<Folder> findTreeItem(TreeItem<Folder> root, Folder target) {
        for (TreeItem<Folder> child : root.getChildren()) {
            if (child.getValue() == target) return child;

            TreeItem<Folder> found = findTreeItem(child, target);
            if (found != null) return found;
        }
        return null;
    }

    private void openFile(File file) {
        FileEditor.open(file, () -> {
            if (file.getParentFolder() != null) {
                file.getParentFolder().getStorage().saveFolder(file.getParentFolder());
            } else {
                folderManager.getFileStorage().saveFile(file);
            }
        });
    }


    public SplitPane getNode() {
        return root;
    }
}
