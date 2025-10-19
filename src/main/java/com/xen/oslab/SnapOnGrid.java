package com.xen.oslab;

import javafx.scene.Node;

public class SnapOnGrid {

    public void snap(Node node) {
        double cellW = 80;
        double cellH = 100;
        int cols = 10;
        int rows = 5;

        double x = node.getLayoutX();
        double y = node.getLayoutY();

        double snappedX = Math.round(x / cellW) * cellW;
        double snappedY = Math.round(y / cellH) * cellH;

        snappedX = Math.clamp(snappedX, 0, (cols - 1) * cellW);
        snappedY = Math.clamp(snappedY, 0, (rows - 1) * cellH);

        node.setLayoutX(snappedX);
        node.setLayoutY(snappedY);

    }
}
