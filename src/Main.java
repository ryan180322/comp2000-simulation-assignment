import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Main extends JPanel {
    private Grid grid;
    private Display display;
    private boolean running = false;
    private Timer timer;
    private static final int TICK_DELAY = 100;

    public Main() {
        setLayout(new BorderLayout());

        grid = new Grid(100, 100);
        display = new Display(grid);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton startBtn = new JButton("Start");
        JButton stopBtn = new JButton("Stop");
        JButton resetBtn = new JButton("Reset");

        startBtn.addActionListener(e -> startSimulation());
        stopBtn.addActionListener(e -> stopSimulation());
        resetBtn.addActionListener(e -> resetSimulation());

        controls.add(startBtn);
        controls.add(stopBtn);
        controls.add(resetBtn);

        add(display, BorderLayout.CENTER);
        add(controls, BorderLayout.SOUTH);
    }

    private void startSimulation() {
        if (running) return;
        running = true;
        timer = new Timer(TICK_DELAY, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                grid.tick();
                display.update(grid);
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
        grid = new Grid(100, 100);
        display.update(grid);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Forest Fire Simulation");
        Main sim = new Main();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(sim);
        frame.setResizable(false);
        frame.setSize(500, 600);
        //frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}