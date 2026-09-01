public class Grid {
    private final int width;
    private final int height;

    // later
    @SuppressWarnings("unused")
    private Cell[][] cells;

    public Grid(int width, int height) {
        this.width = width;
        this.height = height;
        this.cells = new Cell[width][height];
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }
}
