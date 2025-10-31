package com.xen.oslab.managers.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.nio.file.*;

public abstract class DesktopStorageManager {
    protected final Path baseDir = Paths.get("desktop_files");
    protected final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public DesktopStorageManager() {
        try {
            Files.createDirectories(baseDir);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected void write(Path path, String content) {
        try {
            Files.writeString(path, content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected String read(Path path) {
        try {
            return Files.readString(path);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }
}
