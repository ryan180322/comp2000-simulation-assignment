import java.util.ArrayList;
import java.util.List;

public class Grid {
    private final int width;
    private final int height;
    private Cell[][] cells;
    private final double treeGrowthRate;
    private WindType windType;

    public Grid(int width, int height) {
        this(width, height, new NoWind());
    }

    public Grid(int width, int height, WindType windType) {
        this(width, height, 0.005, windType);
    }

    public Grid(int width, int height, double treeGrowthRate, WindType windType) {
        this.width = width;
        this.height = height;
        this.treeGrowthRate = treeGrowthRate;
        this.windType = windType;
        this.cells = new Cell[width][height];
        initialize();
    }

    public WindType getWindType() { return windType; }
    public void setWindType(WindType w) { this.windType = w; }

    private void initialize() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                // hardcoded
                if (Math.random() < 0.5) {
                    cells[x][y] = new TreeCell(x, y);
                } else {
                    cells[x][y] = new EmptyCell(x, y);
                }
            }
        }
        // Place a single fire at centre
        int cx = width / 2;
        int cy = height / 2;
        cells[cx][cy] = new FireCell(cx, cy);
    }

    public Cell getCell(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            throw new InvalidGridPositionException(x, y, width, height);
        }
        return cells[x][y];
    }

    public void setCell(int x, int y, Cell newCell) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            throw new InvalidGridPositionException(x, y, width, height);
        }
        cells[x][y] = newCell;
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
                cell.update(new SnapshotGrid(snapshot).createGrid(), this, windType);
            }
        }

        // grow tree
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (cells[x][y] instanceof EmptyCell) {
                    if (Math.random() < treeGrowthRate) {
                        cells[x][y] = new TreeCell(x, y);
                    }
                }
            }
        }
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }

    public List<Integer> getStats() {
        int trees = 0, fires = 0, empties = 0;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (cells[x][y] instanceof TreeCell) trees++;
                else if (cells[x][y] instanceof FireCell) fires++;
                else if (cells[x][y] instanceof EmptyCell) empties++;
            }
        }
        List<Integer> stats = new ArrayList<>();
        stats.add(trees);
        stats.add(fires);
        stats.add(empties);
        return stats;
    }

    private class SnapshotGrid {
        private final Cell[][] snapshot;
        private Grid output;
        public SnapshotGrid(Cell[][] snapshot) { this.snapshot = snapshot; }
        public Grid createGrid() {
            output = new Grid(Grid.this.width,Grid.this.height);
            output.cells = snapshot;
            return output;
        }
    }

    public String toString() {
        String output = "";
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                output += cells[j][i];
            }
            output += "\n";
        }
        return output;
    }
}
