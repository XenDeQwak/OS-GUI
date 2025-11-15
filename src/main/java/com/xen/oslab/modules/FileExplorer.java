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

    // Modern dark theme colors
    private static final String BG_PRIMARY = "#1a1a1a";
    private static final String BG_SECONDARY = "#242424";
    private static final String BG_TERTIARY = "#2d2d2d";
    private static final String ACCENT_BLUE = "#3b82f6";
    private static final String ACCENT_CYAN = "#06b6d4";
    private static final String TEXT_PRIMARY = "#ffffff";
    private static final String TEXT_SECONDARY = "#a1a1aa";
    private static final String HOVER_BG = "#3a3a3a";
    private static final String BORDER_COLOR = "#333333";

    private final SplitPane root = new SplitPane();
    private final TreeView<Folder> folderTree;
    private final TableView<Object> fileTable;

    private final FolderManager folderManager;

    public FileExplorer(FolderManager folderManager) {

        this.folderManager = folderManager;

        folderTree = new TreeView<>();
        folderTree.setShowRoot(false);
        
        // Style the tree view
        folderTree.setStyle("""
            -fx-background-color: %s;
            -fx-border-color: %s;
            -fx-border-width: 0 1 0 0;
            -fx-focus-color: transparent;
            -fx-faint-focus-color: transparent;
        """.formatted(BG_SECONDARY, BORDER_COLOR));

        TreeItem<Folder> virtualRoot = new TreeItem<>();
        for (Folder folder : folderManager.getRootFolders()) {
            virtualRoot.getChildren().add(buildTree(folder));
        }
        folderTree.setRoot(virtualRoot);

        folderTree.setCellFactory(tv -> new TreeCell<>() {
            @Override
            protected void updateItem(Folder item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? "" : "📁 " + item.getFolderName());
                
                // Style individual cells
                if (empty || item == null) {
                    setStyle("-fx-background-color: transparent;");
                } else {
                    setStyle("""
                        -fx-background-color: transparent;
                        -fx-text-fill: %s;
                        -fx-font-size: 14px;
                        -fx-padding: 8 12;
                    """.formatted(TEXT_PRIMARY));
                    
                    // Hover effect
                    setOnMouseEntered(e -> {
                        if (!isSelected()) {
                            setStyle("""
                                -fx-background-color: %s;
                                -fx-text-fill: %s;
                                -fx-font-size: 14px;
                                -fx-padding: 8 12;
                                -fx-background-radius: 6;
                            """.formatted(HOVER_BG, TEXT_PRIMARY));
                        }
                    });
                    
                    setOnMouseExited(e -> {
                        if (!isSelected()) {
                            setStyle("""
                                -fx-background-color: transparent;
                                -fx-text-fill: %s;
                                -fx-font-size: 14px;
                                -fx-padding: 8 12;
                            """.formatted(TEXT_PRIMARY));
                        }
                    });
                }
                
                // Selected state styling
                if (isSelected()) {
                    setStyle("""
                        -fx-background-color: %s;
                        -fx-text-fill: white;
                        -fx-font-size: 14px;
                        -fx-font-weight: 600;
                        -fx-padding: 8 12;
                        -fx-background-radius: 6;
                    """.formatted(ACCENT_BLUE));
                }
            }
        });

        fileTable = new TableView<>();
        
        // Style the table view
        fileTable.setStyle("""
            -fx-background-color: %s;
            -fx-border-color: transparent;
            -fx-focus-color: transparent;
            -fx-faint-focus-color: transparent;
            -fx-table-cell-border-color: transparent;
            -fx-selection-bar: %s;
            -fx-selection-bar-non-focused: %s;
        """.formatted(BG_PRIMARY, ACCENT_BLUE, ACCENT_BLUE));
        
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
        styleTableColumn(typeCol);
        typeCol.setCellValueFactory(cellData -> {
            Object obj = cellData.getValue();
            if (obj instanceof File) return new SimpleStringProperty("📄 File");
            if (obj instanceof Folder) return new SimpleStringProperty("📁 Folder");
            return new SimpleStringProperty("");
        });

        TableColumn<Object, String> nameCol = new TableColumn<>("Name");
        styleTableColumn(nameCol);
        nameCol.setCellValueFactory(cellData -> {
            Object obj = cellData.getValue();
            if (obj instanceof File f) return new SimpleStringProperty(f.getFileName());
            if (obj instanceof Folder folder) return new SimpleStringProperty(folder.getFolderName());
            return new SimpleStringProperty("");
        });

        TableColumn<Object, String> pathCol = new TableColumn<>("Path");
        styleTableColumn(pathCol);
        pathCol.setCellValueFactory(cellData -> {
            Object obj = cellData.getValue();
            if (obj instanceof File f) return new SimpleStringProperty(buildPath(f.getParentFolder()) + "/" + f.getFileName());
            if (obj instanceof Folder folder) return new SimpleStringProperty(buildPath(folder));
            return new SimpleStringProperty("");
        });

        fileTable.getColumns().addAll(typeCol, nameCol, pathCol);
        
        // Style column headers after they're added to the table
        javafx.application.Platform.runLater(() -> {
            fileTable.lookupAll(".column-header-background").forEach(node -> {
                node.setStyle("""
                    -fx-background-color: %s;
                """.formatted(BG_SECONDARY));
            });
            
            fileTable.lookupAll(".column-header .label").forEach(node -> {
                node.setStyle("""
                    -fx-text-fill: %s;
                    -fx-font-size: 14px;
                    -fx-font-weight: 600;
                """.formatted(TEXT_PRIMARY));
            });
        });
        
        // Custom row factory with styling
        fileTable.setRowFactory(tv -> {
            TableRow<Object> row = new TableRow<>() {
                @Override
                protected void updateItem(Object item, boolean empty) {
                    super.updateItem(item, empty);
                    
                    if (empty || item == null) {
                        setStyle("-fx-background-color: transparent;");
                    } else {
                        // Check if selected first
                        if (isSelected()) {
                            setStyle("""
                                -fx-background-color: %s;
                                -fx-text-fill: white;
                                -fx-border-color: %s;
                                -fx-border-width: 0 0 1 0;
                                -fx-background-insets: 0;
                                -fx-padding: 0;
                            """.formatted(ACCENT_BLUE, ACCENT_CYAN));
                        } else {
                            setStyle("""
                                -fx-background-color: %s;
                                -fx-text-fill: %s;
                                -fx-border-color: %s;
                                -fx-border-width: 0 0 1 0;
                                -fx-background-insets: 0;
                                -fx-padding: 0;
                            """.formatted(BG_PRIMARY, TEXT_PRIMARY, BORDER_COLOR));
                            
                            // Hover effect
                            setOnMouseEntered(e -> {
                                if (!isSelected()) {
                                    setStyle("""
                                        -fx-background-color: %s;
                                        -fx-text-fill: %s;
                                        -fx-border-color: %s;
                                        -fx-border-width: 0 0 1 0;
                                        -fx-cursor: hand;
                                        -fx-background-insets: 0;
                                        -fx-padding: 0;
                                    """.formatted(HOVER_BG, TEXT_PRIMARY, BORDER_COLOR));
                                }
                            });
                            
                            setOnMouseExited(e -> {
                                if (!isSelected()) {
                                    setStyle("""
                                        -fx-background-color: %s;
                                        -fx-text-fill: %s;
                                        -fx-border-color: %s;
                                        -fx-border-width: 0 0 1 0;
                                        -fx-background-insets: 0;
                                        -fx-padding: 0;
                                    """.formatted(BG_PRIMARY, TEXT_PRIMARY, BORDER_COLOR));
                                }
                            });
                        }
                    }
                }
            };
            
            // Update style on selection change
            row.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
                if (isNowSelected) {
                    row.setStyle("""
                        -fx-background-color: %s;
                        -fx-text-fill: white;
                        -fx-border-color: %s;
                        -fx-border-width: 0 0 1 0;
                        -fx-background-insets: 0;
                        -fx-padding: 0;
                    """.formatted(ACCENT_BLUE, ACCENT_CYAN));
                } else {
                    row.setStyle("""
                        -fx-background-color: %s;
                        -fx-text-fill: %s;
                        -fx-border-color: %s;
                        -fx-border-width: 0 0 1 0;
                        -fx-background-insets: 0;
                        -fx-padding: 0;
                    """.formatted(BG_PRIMARY, TEXT_PRIMARY, BORDER_COLOR));
                }
            });
            
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
        
        // Style the split pane
        root.setStyle("""
            -fx-background-color: %s;
            -fx-border-color: transparent;
        """.formatted(BG_PRIMARY));
        
        // Style the divider
        root.lookupAll(".split-pane-divider").forEach(node -> {
            node.setStyle("""
                -fx-background-color: %s;
                -fx-padding: 0 2 0 2;
            """.formatted(BORDER_COLOR));
        });
    }
    
    private void styleTableColumn(TableColumn<Object, String> column) {
        column.setStyle("""
            -fx-background-color: %s;
            -fx-text-fill: %s;
            -fx-font-size: 14px;
            -fx-font-weight: 600;
            -fx-alignment: CENTER_LEFT;
            -fx-border-color: %s;
            -fx-border-width: 0 0 2 0;
        """.formatted(BG_SECONDARY, TEXT_PRIMARY, BORDER_COLOR));
        
        // Style table cells - need to track selection state
        column.setCellFactory(col -> new TableCell<>() {
            
            private void updateCellStyle() {
                TableRow<?> currentRow = getTableRow();
                if (isEmpty()) {
                    setStyle("-fx-background-color: transparent;");
                } else if (currentRow != null && currentRow.isSelected()) {
                    setStyle("""
                        -fx-background-color: %s !important;
                        -fx-text-fill: white !important;
                        -fx-font-size: 13px;
                        -fx-padding: 12 16;
                        -fx-alignment: CENTER_LEFT;
                    """.formatted(ACCENT_BLUE));
                } else {
                    setStyle("""
                        -fx-background-color: %s !important;
                        -fx-text-fill: %s;
                        -fx-font-size: 13px;
                        -fx-padding: 12 16;
                        -fx-alignment: CENTER_LEFT;
                    """.formatted(BG_PRIMARY, TEXT_PRIMARY));
                }
            }
            
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : item);
                updateCellStyle();
                
                // Listen to row selection changes
                TableRow<?> currentRow = getTableRow();
                if (currentRow != null) {
                    currentRow.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
                        updateCellStyle();
                    });
                }
            }
        });
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
        Scene scene = new Scene(explorer.getNode(), 800, 500);
        
        // Set dark background for the scene
        scene.setFill(javafx.scene.paint.Color.web(BG_PRIMARY));
        
        stage.setScene(scene);
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