public class FireCell extends Cell {
    private int burnTime;
    private static final int MAX_BURN_TIME = 10;

    public FireCell(int x, int y) {
        super(x, y);
        this.burnTime = MAX_BURN_TIME;
    }

    @Override
    public void update(Grid snapGrid, Grid grid) {
        burnTime--;

        if (burnTime <= 0) {
            grid.setCell(x, y, new EmptyCell(x, y));
            return;
        }

        /*/ hardcoded
        int[] dx = {-1, 1, 0, 0};
        int[] dy = {0, 0, -1, 1};
        for (int i = 0; i < 4; i++) {
            Cell neighbour = grid.getCell(x + dx[i], y + dy[i]);
            if (neighbour instanceof TreeCell) {
                if (Math.random() < 0.6) {
                    grid.setCell(x + dx[i], y + dy[i], new FireCell(x + dx[i], y + dy[i]));
                }
            }
        }*/
    }

    @Override
    public java.awt.Color getColor() {
        float t = (float) burnTime / MAX_BURN_TIME;
        int r = 255;
        int g = Math.clamp((int) (255 * t), 0, 255);
        return new java.awt.Color(r, g, 0);
    }
}