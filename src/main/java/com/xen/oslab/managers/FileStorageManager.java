package com.xen.oslab.managers;

import com.google.gson.*;
import com.xen.oslab.objects.File;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.nio.file.*;
import java.util.stream.Collectors;

public class FileStorageManager {
    private final Path savePath = Paths.get("desktop_files/state.json");
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public FileStorageManager() {
        try {
            Files.createDirectories(savePath.getParent());
            if (!Files.exists(savePath)) Files.writeString(savePath, "[]");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveAll(Pane desktop) {
        JsonArray jsonArray = desktop.getChildren().stream()
                .filter(n -> n instanceof File)
                .map(n -> {
                    File f = (File) n;
                    JsonObject obj = new JsonObject();
                    obj.addProperty("name", f.getFileName());
                    obj.addProperty("x", f.getLayoutX());
                    obj.addProperty("y", f.getLayoutY());
                    return obj;
                })
                .collect(JsonArray::new, JsonArray::add, JsonArray::addAll);

        try {
            Files.writeString(savePath, gson.toJson(jsonArray));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadAll(Pane desktop, FileManager fileManager) {
        if (!Files.exists(savePath)) return;
        try {
            String content = Files.readString(savePath);
            JsonArray arr = JsonParser.parseString(content).getAsJsonArray();

            for (JsonElement el : arr) {
                JsonObject obj = el.getAsJsonObject();
                String name = obj.get("name").getAsString();
                double x = obj.get("x").getAsDouble();
                double y = obj.get("y").getAsDouble();
                fileManager.createFileAt(name, x, y);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
