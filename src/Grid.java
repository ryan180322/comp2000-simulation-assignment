public class Grid {
    private final int width;
    private final int height;

    @SuppressWarnings("unused")
    private Cell[][] cells;

    public Grid(int width, int height) {
        this.width = width;
        this.height = height;
        this.cells = new Cell[width][height];
        initialize();
    }

    private void initialize() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                // hardcoded
                if (Math.random() < 0.7) {
                    cells[x][y] = new TreeCell(x, y);
                } else {
                    cells[x][y] = new EmptyCell(x, y);
                }
            }
        }
        // Place a single fire
        int cx = width / 2;
        int cy = height / 2;
        cells[cx][cy] = new FireCell(cx, cy);
    }

    public Cell getCell(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) return null;
        return cells[x][y];
    }

    public void setCell(int x, int y, Cell newCell) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            cells[x][y] = newCell;
        }
    }

    public void tick() {
        // create snapshot
        Cell[][] snapshot = new Cell[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                snapshot[x][y] = cells[x][y];
            }
        }

        // update all cells using the snapshot
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Cell cell = cells[x][y];
                cell.update(new SnapshotGrid(snapshot).getCell(x, y));
            }
        }

        // grow tree, hardcoded
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (cells[x][y] instanceof EmptyCell) {
                    if (Math.random() < 0.005) {
                        cells[x][y] = new TreeCell(x, y);
                    }
                }
            }
        }
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }

    private class SnapshotGrid {
        private final Cell[][] snapshot;
        private Grid output;
        public SnapshotGrid(Cell[][] snapshot) { this.snapshot = snapshot; }
        public Grid getCell(int x, int y) {
            if (x < 0 || x >= Grid.this.width || y < 0 || y >= Grid.this.height) return null;
            output = new Grid(snapshot.length,snapshot[0].length);
            output.cells = snapshot;
            return output;
        }
    }
}
