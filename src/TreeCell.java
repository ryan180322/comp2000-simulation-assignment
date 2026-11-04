import java.util.stream.IntStream;

public class TreeCell extends Cell {
    private int age;
    private static final int MAX_AGE = 100;
    private final double lightningProb;
    private final double fireSpreadProb;

    public TreeCell(int x, int y) {
        this(x, y, 0.15, 0.001);
    }

    public TreeCell(int x, int y, double fireSpreadProb, double lightningProb) {
        super(x, y);
        this.age = 0;
        this.fireSpreadProb = fireSpreadProb;
        this.lightningProb = lightningProb;
    }

    public int getAge() { return age; }

    @Override
    public void update(Grid snapGrid, Grid grid, WindType wind) {
        age = Math.min(age + 1, MAX_AGE);

        // lightning
        if (Math.random() < lightningProb) {
            grid.setCell(x, y, new FireCell(x, y));
            return;
        }

        // fire spread: scan the 4 neighbours as a stream.
        // filter(inBounds) replaces the old try/catch — the exception stays a
        // genuine error signal instead of being used as flow control (Week 6).
        // anyMatch short-circuits on the first igniting neighbour.
        int[] dx = {-1, 1, 0, 0};
        int[] dy = {0, 0, -1, 1};
        boolean ignites = IntStream.range(0, 4)
                .filter(i -> snapGrid.inBounds(x + dx[i], y + dy[i]))
                .filter(i -> snapGrid.getCell(x + dx[i], y + dy[i]) instanceof FireCell)
                .anyMatch(i -> {
                    double windMod = wind.getSpreadMultiplier(-dx[i], -dy[i]);
                    double adj = Math.min(fireSpreadProb * windMod, 1.0);
                    return Math.random() < adj;
                });
        if (ignites) grid.setCell(x, y, new FireCell(x, y));
    }

    public void update(Grid snapGrid, Grid grid) { update(snapGrid, grid, new NoWind()); }

    @Override
    public java.awt.Color getColor() {
        // Green shade varies by age (darker = older)
        int shade = Math.max(50, 200 - age);
        return new java.awt.Color(0, shade, 0);
    }

    public String toString() {
        return "Trees";
    }
}