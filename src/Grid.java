import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Grid {
    private final int width;
    private final int height;
    private Cell[][] cells;
    private final double treeGrowthRate;
    private WindType windType;

    // Observer pattern: this Grid is the Subject. Everything that watches
    // the simulation (Display, stats label, ...) subscribes here instead of
    // being hard-wired into Main's tick loop.
    private final List<Observer> observers = new ArrayList<>();

    public void registerObserver(Observer o)  { observers.add(o); }
    public void removeObserver(Observer o)    { observers.remove(o); }

    /** Push-style notification: hand each observer the current grid. */
    private void notifyObservers() {
        for (Observer o : observers) o.update(this);
    }

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
        if (!inBounds(x, y)) {
            throw new InvalidGridPositionException(x, y, width, height);
        }
        return cells[x][y];
    }

    public void setCell(int x, int y, Cell newCell) {
        if (!inBounds(x, y)) {
            throw new InvalidGridPositionException(x, y, width, height);
        }
        cells[x][y] = newCell;
    }

    /** Non-throwing bounds check. Lets callers filter out-of-bounds probes
     *  instead of using exceptions as flow control (Week 6 lesson). */
    public boolean inBounds(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    /** Every cell as one flat Stream — the 2D→1D flattening that the whole
     *  stats/observer layer is built on (Week 10 flatMap). */
    public Stream<Cell> cellStream() {
        return Arrays.stream(cells).flatMap(Arrays::stream);
    }

    public void tick() {
        // Snapshot via streams: each row is shallow-copied, one expression,
        // no manual index bookkeeping.
        Cell[][] snapshot = Arrays.stream(cells)
                .map(Cell[]::clone)
                .toArray(Cell[][]::new);
        Grid snapView = new SnapshotGrid(snapshot).createGrid();

        // update all cells using the snapshot
        cellStream().forEach(cell -> cell.update(snapView, this, windType));

        // grow tree: filter → map to replanting decisions → apply
        IntStream.range(0, width * height)
                .filter(i -> cells[i % width][i / width] instanceof EmptyCell
                          && Math.random() < treeGrowthRate)
                .forEach(i -> cells[i % width][i / width] =
                        new TreeCell(i % width, i / width));

        // Observer pattern: state change finished -> broadcast once.
        notifyObservers();
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }

    public Map<Class<?>, Long> getStats() {
        return cellStream()
                .collect(Collectors.groupingBy(Cell::getClass, Collectors.counting()));
    }

    /** Short-circuit terminal op: true as soon as any burning cell is found.
     *  Used by Main to auto-pause a dead simulation (Week 10 EPIC idea ③). */
    public boolean anyFire() {
        return cellStream().anyMatch(c -> c instanceof FireCell);
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
        // rows joined with newline; each row is the cells' toString concatenated
        return Arrays.stream(cells)
                .map(row -> Arrays.stream(row).map(Cell::toString)
                                        .collect(Collectors.joining()))
                .collect(Collectors.joining("\n"));
    }
}
