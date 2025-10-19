package com.xen.oslab.managers;

import javafx.scene.image.Image;

import java.util.Objects;

public class LoadManager {
    public Image load(String name) {
        return new Image(Objects.requireNonNull(
                getClass().getResource("/com/xen/oslab/icons/" + name)
        ).toExternalForm());
    }
}
