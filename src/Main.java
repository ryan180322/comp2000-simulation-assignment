import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
//import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Main extends JPanel {
    private Grid grid;
    private Display display;
    private boolean running = false;
    private Timer timer;
    private JLabel statsLabel;
    private WindPreset currentPreset = WindPreset.NONE;
    private static final int WIDTH = 25;
    private static final int HEIGHT = 25;
    private static final int TICK_DELAY = 100;

    public Main() {
        setLayout(new BorderLayout());

        grid = new Grid(WIDTH, HEIGHT);
        display = new Display(grid);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton startBtn = new JButton("Start");
        JButton stopBtn = new JButton("Stop");
        JButton resetBtn = new JButton("Reset");

        startBtn.addActionListener(e -> startSimulation());
        stopBtn.addActionListener(e -> stopSimulation());
        resetBtn.addActionListener(e -> resetSimulation());

        JComboBox<WindPreset> windBox = new JComboBox<>();
        // Stream the enum constants into the combo instead of nine addItem calls
        java.util.Arrays.stream(WindPreset.values()).forEach(windBox::addItem);
        windBox.addActionListener(e -> {
            // Factory enum: the combo item knows how to build its own Strategy,
            // so Main has no switch over wind types at all.
            WindPreset preset = (WindPreset) windBox.getSelectedItem();
            if (preset != null) {
                grid.setWindType(preset.create());
                currentPreset = preset;
            }
        });

        statsLabel = new JLabel();
        updateStatsLabel(statsLabel);

        attachObservers(grid);

        controls.add(startBtn);
        controls.add(stopBtn);
        controls.add(resetBtn);
        controls.add(windBox);
        controls.add(statsLabel);

        add(display, BorderLayout.CENTER);
        add(controls, BorderLayout.SOUTH);
    }

    private void attachObservers(Grid g) {
        g.registerObserver(display);
        g.registerObserver(newGrid -> updateStatsLabel(statsLabel));
    }

    private void updateStatsLabel(JLabel label) {
        Map<Class<?>, Long> stats = grid.getStats();
        if (stats.size() == 0) {
            label.setText("");
            return;
        }
        // Stream the entries straight into the display string:
        // entry -> "Name: count", joined with " | ". No StringBuilder loop.
        String text = stats.entrySet().stream()
                .map(e -> String.format(" %s: %d", e.getKey().getSimpleName(), e.getValue()))
                .collect(Collectors.joining(" | ", " | ", " | "));
        if (grid.isRaining()) text += "  (raining)";
        label.setText(text);
    }


    private void startSimulation() {
        if (running) return;
        running = true;
        timer = new Timer(TICK_DELAY, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                grid.tick();
                if (!grid.anyFire()) stopSimulation();
            }
        });
        timer.start();
    }

    private void stopSimulation() {
        if (timer != null && timer.isRunning()) timer.stop();
        running = false;
    }

    private void resetSimulation() {
        stopSimulation();
        grid = new Grid(WIDTH, HEIGHT);

        grid.setWindType(currentPreset.create());
        attachObservers(grid);
        display.update(grid);
        updateStatsLabel(statsLabel);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Forest Fire Simulation");
        Main sim = new Main();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(sim);
        frame.setResizable(false);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}