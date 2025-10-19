package com.xen.oslab;

import javafx.scene.image.Image;

import java.util.Objects;

public class LoadImages {
    public Image load(String name) {
        return new Image(Objects.requireNonNull(
                getClass().getResource("/com/xen/oslab/icons/" + name)
        ).toExternalForm());
    }
}
