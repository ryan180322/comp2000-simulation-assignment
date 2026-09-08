import javax.swing.*;
import java.awt.*;

public class Display extends JPanel {
    private static final int CELL_SIZE = 20;
    private Grid grid;

    public Display(Grid grid) {
        this.grid = grid;
        this.setPreferredSize(new Dimension(
            grid.getWidth() * CELL_SIZE,
            grid.getHeight() * CELL_SIZE
        ));
        this.setBackground(Color.BLACK);
    }

    public void update(Grid grid) {
        this.grid = grid;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (grid == null) return;
        for (int x = 0; x < grid.getWidth(); x++) {
            for (int y = 0; y < grid.getHeight(); y++) {
                Cell cell = grid.getCell(x, y);
                if (cell != null) {
                    g.setColor(cell.getColor());
                    g.fillRect(x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                }
            }
        }
    }
}
