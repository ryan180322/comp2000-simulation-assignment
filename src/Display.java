import javax.swing.*;
import java.awt.*;

@SuppressWarnings("unused")
public class Display extends JPanel {
    private static final int CELL_SIZE = 6;
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
}
