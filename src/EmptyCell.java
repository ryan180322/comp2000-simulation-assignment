public class EmptyCell extends Cell {
    public EmptyCell(int x, int y) {
        super(x, y);
    }

    @Override
    public void update(Grid snapGrid, Grid grid) {
        // nothing by themselves, change from grid
    }
    
    @Override
    public java.awt.Color getColor() {
        return new java.awt.Color(128, 128, 128); // Grey
    }
}