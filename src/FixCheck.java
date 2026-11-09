import java.lang.reflect.*;
import javax.swing.*;

public class FixCheck {
    public static void main(String[] a) throws Exception {
        System.setProperty("java.awt.headless", "true");
        Main sim = new Main();

        Field gf = Main.class.getDeclaredField("grid");  gf.setAccessible(true);
        Field pf = Main.class.getDeclaredField("currentPreset"); pf.setAccessible(true);
        Grid g0 = (Grid) gf.get(sim);

        g0.setWindType(WindPreset.GUST_EAST.create());
        pf.set(sim, WindPreset.GUST_EAST);

        for (int t = 0; t < 40; t++) g0.tick();

        Method reset = Main.class.getDeclaredMethod("resetSimulation");
        reset.setAccessible(true);
        reset.invoke(sim);
        Grid g1 = (Grid) gf.get(sim);

        boolean newGrid = g1 != g0;
        boolean windKept = g1.getWindType().toString().contains("Gust")
                        && g1.getWindType().toString().contains("EAST");

        Field lf = Main.class.getDeclaredField("statsLabel"); lf.setAccessible(true);
        JLabel label = (JLabel) lf.get(sim);
        String text = label.getText();
        long trees = g1.getStats().getOrDefault(TreeCell.class, 0L);
        long water = g1.getStats().getOrDefault(WaterCell.class, 0L);
        boolean statsRefreshed = text != null
                && text.contains("TreeCell: " + trees)
                && text.contains("WaterCell: " + water);

        System.out.println("newGrid=" + newGrid + " windKept=" + windKept
            + " statsRefreshed=" + statsRefreshed);
        System.out.println((newGrid && windKept && statsRefreshed)
            ? "FIXCHECK_ALL_PASS" : "FIXCHECK_FAIL");
    }
}
