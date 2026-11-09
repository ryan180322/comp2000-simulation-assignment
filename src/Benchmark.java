import java.util.Map;
import java.util.stream.IntStream;

public class Benchmark {

    private static final int WIDTH = 25, HEIGHT = 25;

    private static long runOnce(WindPreset preset, int ticks) {
        Grid g = new Grid(WIDTH, HEIGHT);
        g.setWindType(preset.create());
        for (int t = 0; t < ticks && g.anyFire(); t++) {
            g.tick();
        }
        Map<Class<?>, Long> stats = g.getStats();
        // Empty cells only ever appear by burning, so they measure the burnt area.
        return stats.getOrDefault(EmptyCell.class, 0L);
    }

    private static long[] sweepSequential(WindPreset preset, int reps, int ticks) {
        return IntStream.range(0, reps)
                .mapToLong(i -> runOnce(preset, ticks))
                .toArray();
    }

    private static long[] sweepParallel(WindPreset preset, int reps, int ticks) {
        return IntStream.range(0, reps).parallel()
                .mapToLong(i -> runOnce(preset, ticks))
                .toArray();
    }

    private static void report(String mode, long[] burnt, long millis) {
        double mean = java.util.stream.LongStream.of(burnt).average().orElse(0);
        long min = java.util.stream.LongStream.of(burnt).min().orElse(0);
        long max = java.util.stream.LongStream.of(burnt).max().orElse(0);
        System.out.printf("  %-11s burnt area: mean %6.1f  min %4d  max %4d   (%d ms)%n",
                mode, mean, min, max, millis);
    }

    public static void main(String[] args) {
        int reps  = args.length > 0 ? Integer.parseInt(args[0]) : 16;
        int ticks = args.length > 1 ? Integer.parseInt(args[1]) : 400;
        System.out.printf("Forest-fire parameter sweep — %dx%d grid, %d ticks max, %d reps per preset%n%n",
                WIDTH, HEIGHT, ticks, reps);

        for (WindPreset preset : new WindPreset[]{
                WindPreset.NONE, WindPreset.NORTH, WindPreset.EAST, WindPreset.GUST_NORTH}) {

            long t0 = System.nanoTime();
            long[] seq = sweepSequential(preset, reps, ticks);
            long seqMs = (System.nanoTime() - t0) / 1_000_000;

            long t1 = System.nanoTime();
            long[] par = sweepParallel(preset, reps, ticks);
            long parMs = (System.nanoTime() - t1) / 1_000_000;

            System.out.printf("%s%n", preset);
            report("sequential", seq, seqMs);
            report("parallel", par, parMs);
            System.out.printf("  speed-up: %.2fx  <- at 625 cells the split/merge overhead is what you pay for%n%n",
                    (double) seqMs / Math.max(1, parMs));
        }

        System.out.println("Conclusion: the SWEEP parallelises for free (zero shared state);");
        System.out.println("tick() at game size does not - measure before you parallel() (Week 12).");
    }
}
