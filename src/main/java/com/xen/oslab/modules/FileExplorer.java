package com.xen.oslab.modules;

import com.xen.oslab.managers.FolderManager;
import com.xen.oslab.objects.File;
import com.xen.oslab.objects.Folder;
import javafx.scene.control.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class FileExplorer {

    private final SplitPane root = new SplitPane();
    private final TreeView<Folder> folderTree;
    private final TableView<Object> fileTable;

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

        folderTree.setCellFactory(tv -> new TreeCell<>() {
            @Override
            protected void updateItem(Folder item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? "" : item.getFolderName());
            }
        });

        fileTable = new TableView<>();
        fileTable.setColumnResizePolicy(param -> {
            double tableWidth = fileTable.getWidth() - 2;
            double totalColumns = fileTable.getColumns().size();
            if (totalColumns == 0) return true;

            double widthPerColumn = tableWidth / totalColumns;
            for (TableColumn<?, ?> col : fileTable.getColumns()) {
                col.setPrefWidth(widthPerColumn);
            }
            return true;
        });

        TableColumn<Object, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(cellData -> {
            Object obj = cellData.getValue();
            if (obj instanceof File) return new SimpleStringProperty("File");
            if (obj instanceof Folder) return new SimpleStringProperty("Folder");
            return new SimpleStringProperty("");
        });

        TableColumn<Object, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(cellData -> {
            Object obj = cellData.getValue();
            if (obj instanceof File f) return new SimpleStringProperty(f.getFileName());
            if (obj instanceof Folder folder) return new SimpleStringProperty(folder.getFolderName());
            return new SimpleStringProperty("");
        });

        TableColumn<Object, String> pathCol = new TableColumn<>("Path");
        pathCol.setCellValueFactory(cellData -> {
            Object obj = cellData.getValue();
            if (obj instanceof File f) return new SimpleStringProperty(buildPath(f.getParentFolder()) + "/" + f.getFileName());
            if (obj instanceof Folder folder) return new SimpleStringProperty(buildPath(folder));
            return new SimpleStringProperty("");
        });

        fileTable.getColumns().addAll(typeCol, nameCol, pathCol);

        fileTable.setRowFactory(tv -> {
            TableRow<Object> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    Object item = row.getItem();
                    if (item instanceof File f) openFile(f);
                    if (item instanceof Folder folder) openSubFolderWindow(folder);
                }
            });
            return row;
        });

        folderTree.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            ObservableList<Object> items = FXCollections.observableArrayList();
            if (newSel != null) {
                Folder folder = newSel.getValue();
                // assign parentFolder to all files before showing in table
                for (File f : folder.getFiles()) f.setParentFolder(folder);
                items.addAll(folder.getSubFolders());
                items.addAll(folder.getFiles());
            }
            fileTable.setItems(items);
        });

        root.getItems().addAll(folderTree, fileTable);
        root.setDividerPositions(0.3);
    }

    private TreeItem<Folder> buildTree(Folder folder) {
        TreeItem<Folder> node = new TreeItem<>(folder);
        for (Folder sub : folder.getSubFolders()) node.getChildren().add(buildTree(sub));
        node.setExpanded(true);
        return node;
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
        if (file.getParentFolder() == null) {
            TreeItem<Folder> selected = folderTree.getSelectionModel().getSelectedItem();
            if (selected != null) file.setParentFolder(selected.getValue());
        }

        FileEditor.open(file, () -> {
            if (file.getParentFolder() != null) {
                file.getParentFolder().getStorage().saveFolder(file.getParentFolder());
            } else {
                folderManager.getFileStorage().saveFile(file);
            }
        });
    }

    private String buildPath(Folder folder) {
        if (folder == null) return "root";
        StringBuilder path = new StringBuilder(folder.getFolderName());
        Folder parent = folder.getParentFolder();
        while (parent != null) {
            path.insert(0, parent.getFolderName() + "/");
            parent = parent.getParentFolder();
        }
        return "root/" + path.toString();
    }

    public SplitPane getNode() {
        return root;
    }
}
