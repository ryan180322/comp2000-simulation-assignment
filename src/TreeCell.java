public class TreeCell extends Cell {
    private int age;
    private static final int MAX_AGE = 100;

    public TreeCell(int x, int y) {
        super(x, y);
        this.age = 0;
    }

    public int getAge() { return age; }

    @Override
    public void update(Grid grid) {
        age = Math.min(age + 1, MAX_AGE);

        // hardcoded
        if (Math.random() < 0.001) {
            grid.setCell(x, y, new FireCell(x, y));
            return;
        }

        // hardcoded
        int[] dx = {-1, 1, 0, 0};
        int[] dy = {0, 0, -1, 1};
        for (int i = 0; i < 4; i++) {
            Cell neighbour = grid.getCell(x + dx[i], y + dy[i]);
            if (neighbour instanceof FireCell) {
                if (Math.random() < 0.15) {
                    grid.setCell(x, y, new FireCell(x, y));
                    return;
                }
            }
        }
    }

    @Override
    public java.awt.Color getColor() {
        // Green shade varies by age (darker = older)
        int shade = Math.max(50, 200 - age);
        return new java.awt.Color(0, shade, 0);
    }
}