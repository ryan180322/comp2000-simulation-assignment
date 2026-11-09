import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
public class Grid {
    private final int width;
    private final int height;
    private Cell[][] cells;
    private final double treeGrowthRate;
    private WindType windType;

    // Creativity feature: weather. A storm front periodically sweeps the map,
    // dousing fires and briefly boosting tree regrowth. rainTicks > 0 means
    // it is raining right now.
    private int rainTicks = 0;
    private static final double RAIN_CHANCE = 0.003;   // storm starts per tick
    private static final int   RAIN_LENGTH = 8;        // ticks a storm lasts

    // Observer pattern: this Grid is the Subject. Everything that watches
    // the simulation (Display, stats label, ...) subscribes here instead of
    // being hard-wired into Main's tick loop.
    private final List<Observer> observers = new ArrayList<>();

    /** Class has no natural order, so stats keys are sorted by type name. */
    private static final Comparator<Class<?>> BY_TYPE_NAME =
            Comparator.comparing(Class::getName);

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
                if (Math.random() < 0.5) {
                    cells[x][y] = new TreeCell(x, y);
                } else {
                    cells[x][y] = new EmptyCell(x, y);
                }
            }
        }
        // Scatter 2-3 lakes as small blobs (random walk of 4-7 cells each).
        int lakes = 2 + (int) (Math.random() * 2);
        for (int k = 0; k < lakes; k++) {
            int lx = 1 + (int) (Math.random() * (width - 2));
            int ly = 1 + (int) (Math.random() * (height - 2));
            int size = 4 + (int) (Math.random() * 4);
            for (int s = 0; s < size; s++) {
                cells[lx][ly] = new WaterCell(lx, ly);
                lx = Math.min(width - 1, Math.max(0, lx + (int) (Math.random() * 3) - 1));
                ly = Math.min(height - 1, Math.max(0, ly + (int) (Math.random() * 3) - 1));
            }
        }
        // Place a single fire at centre (nudge off water if unlucky)
        int cx = width / 2;
        int cy = height / 2;
        if (cells[cx][cy] instanceof WaterCell) cx = (cx + 2) % width;
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


    public boolean inBounds(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    public Stream<Cell> cellStream() {
        return Arrays.stream(cells).flatMap(Arrays::stream);
    }

    public void tick() {
        // Let the wind Strategy advance its own clock (no-op for static winds).
        windType.advance();

        // Weather: storms roll in at random, last a few ticks, then pass.
        if (rainTicks == 0 && Math.random() < RAIN_CHANCE) rainTicks = RAIN_LENGTH;
        boolean raining = rainTicks > 0;
        if (raining) rainTicks--;

        // Snapshot via streams: each row is shallow-copied, one expression,
        // no manual index bookkeeping.
        Cell[][] snapshot = Arrays.stream(cells)
                .map(Cell[]::clone)
                .toArray(Cell[][]::new);
        Grid snapView = new SnapshotGrid(snapshot).createGrid();

        // update all cells using the snapshot
        cellStream().forEach(cell -> cell.update(snapView, this, windType));

        if (raining) {
            IntStream.range(0, width * height)
                    .filter(i -> cells[i % width][i / width] instanceof FireCell
                              && Math.random() < 0.4)
                    .forEach(i -> cells[i % width][i / width] =
                            new EmptyCell(i % width, i / width));
        }

        double growth = raining ? treeGrowthRate * 3 : treeGrowthRate;
        IntStream.range(0, width * height)
                .filter(i -> cells[i % width][i / width] instanceof EmptyCell
                          && Math.random() < growth)
                .forEach(i -> cells[i % width][i / width] =
                        new TreeCell(i % width, i / width));

        // Observer pattern: state change finished -> broadcast once.
        notifyObservers();
    }

    public boolean isRaining() { return rainTicks > 0; }

    public int getWidth() { return width; }
    public int getHeight() { return height; }

    public Map<Class<?>, Long> getStats() {
        return cellStream().collect(Collectors.groupingBy(Cell::getClass,
                () -> new TreeMap<>(BY_TYPE_NAME), Collectors.counting()));
    }

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
