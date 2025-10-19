package com.xen.oslab.managers;

public class GridManager {
    private final int rows;
    private final int cols;
    private final double cellW;
    private final double cellH;
    private final boolean[][] occupied;

    public GridManager(int rows, int cols, double cellW, double cellH, boolean[][] occupied) {
        this.rows = rows;
        this.cols = cols;
        this.cellW = cellW;
        this.cellH = cellH;
        this.occupied = occupied;
    }

    public boolean isOccupied(int row, int col) {
        return occupied[row][col];
    }

    public void occupy(int row, int col) {
        occupied[row][col] = true;
    }

    public void free(int row, int col) {
        occupied[row][col] = false;
    }

    public double toX(int col) {
        return col * cellW;
    }

    public double toY(int row) {
        return row * cellH;
    }

    public int clampRow(double y) {
        return Math.clamp((int) Math.round(y / cellH), 0, rows - 1);
    }

    public int clampCol(double x) {
        return Math.clamp((int) Math.round(x / cellW), 0, cols - 1);
    }

    public int[] findNearestFree(int targetRow, int targetCol) {
        double best = Double.MAX_VALUE;
        int[] bestPos = new int[0];

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (!occupied[r][c]) {
                    double dist = Math.pow(r - targetRow, 2) + Math.pow(c - targetCol, 2);
                    if (dist < best) {
                        best = dist;
                        bestPos = new int[]{r, c};
                    }
                }
            }
        }
        return bestPos;
    }
}
