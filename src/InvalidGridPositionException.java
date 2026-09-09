public class InvalidGridPositionException extends RuntimeException {
    private final int x;
    private final int y;
    private final int width;
    private final int height;

    public InvalidGridPositionException(int x, int y, int width, int height) {
        super(String.format("(%d,%d) outside grid bounds (0-%d, 0-%d)",
                x, y, width - 1, height - 1));
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
}
