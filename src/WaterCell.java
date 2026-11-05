/**
 * WaterCell - a lake that never grows trees or catches fire
 */
public class WaterCell extends Cell {

    public WaterCell(int x, int y) {
        super(x, y);
    }

    @Override
    public void update(Grid snapGrid, Grid grid, WindType wind) {
        // Water is permanent: rain fills it, fire cannot touch it.
    }

    @Override
    public java.awt.Color getColor() {
        return new java.awt.Color(30, 100, 200); // Blue
    }

    public String toString() {
        return "Water";
    }
}
