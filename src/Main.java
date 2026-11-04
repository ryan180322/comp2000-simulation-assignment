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

        JComboBox<WindDirection> windBox = new JComboBox<>();
        // Stream the enum constants into the combo instead of five addItem calls
        java.util.Arrays.stream(WindDirection.values()).forEach(windBox::addItem);
        windBox.addActionListener(e -> {
            WindDirection dir = (WindDirection) windBox.getSelectedItem();
            grid.setWindType(dir == WindDirection.NONE ? new NoWind() : new CardinalWind(dir));
        });

        statsLabel = new JLabel();
        updateStatsLabel(statsLabel);

        // Observer pattern: everyone watching the simulation subscribes to the
        // Grid. Main no longer has to remember to refresh each widget inside
        // the timer loop — the Grid pushes every tick to its observers.
        attachObservers(grid);

        controls.add(startBtn);
        controls.add(stopBtn);
        controls.add(resetBtn);
        controls.add(windBox);
        controls.add(statsLabel);

        add(display, BorderLayout.CENTER);
        add(controls, BorderLayout.SOUTH);
    }

    /** Observer pattern: subscribe every widget that watches this Grid.
     *  Called again after Reset creates a fresh Grid, so subscriptions are
     *  never lost (the memory-leak / stale-subscription pitfall). */
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
        label.setText(text);
    }


    private void startSimulation() {
        if (running) return;
        running = true;
        timer = new Timer(TICK_DELAY, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Observer pattern: one call. Display and the stats label are
                // notified by the Grid itself — Main does not poll them.
                grid.tick();
                // anyFire() short-circuits: when the last flame dies, stop the
                // timer instead of spinning on an inert forest.
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
        attachObservers(grid);
        display.update(grid);
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