import java.util.Random;

/**
 * GustWind - a wind whose strength OSCILLATES over time.
 */
public class GustWind implements WindType {
    private final WindDirection direction;
    private final double amplitude;   // peak extra multiplier
    private final double period;      // ticks per full gust cycle
    private int tick;
    private final Random rng;

    public GustWind(WindDirection direction) {
        this(direction, 1.5, 12.0);
    }

    public GustWind(WindDirection direction, double amplitude, double period) {
        this.direction = direction == WindDirection.NONE ? WindDirection.NORTH : direction;
        this.amplitude = Math.max(0.0, amplitude);
        this.period = Math.max(2.0, period);
        this.tick = 0;
        this.rng = new Random();
    }

    /** Called by the simulation clock (Main) once per tick. */
    public void advance() { tick++; }

    /** Current gust level shared by all cells this tick: sine + jitter. */
    private double gustLevel() {
        double wave = Math.sin(2 * Math.PI * tick / period);   // -1 .. 1
        double jitter = rng.nextDouble() * 0.2;                 // 0 .. 0.2
        return 1.0 + amplitude * wave + jitter;                 // roughly 1 +/- amplitude
    }

    @Override
    public double getSpreadMultiplier(int dx, int dy) {
        double level = Math.max(0.1, gustLevel());

        boolean along = switch (direction) {
            case NORTH -> dy == -1;
            case SOUTH -> dy == 1;
            case EAST  -> dx == 1;
            case WEST  -> dx == -1;
            default    -> false;
        };
        boolean against = switch (direction) {
            case NORTH -> dy == 1;
            case SOUTH -> dy == -1;
            case EAST  -> dx == -1;
            case WEST  -> dx == 1;
            default    -> false;
        };
        if (along) return level;
        if (against) return 1.0 / level;
        return 1.0;
    }

    @Override
    public String toString() {
        return String.format("Gust %s (amp %.1f)", direction, amplitude);
    }
}
