import java.lang.reflect.*;
import javax.swing.*;

public class FixCheck {
    public static void main(String[] a) throws Exception {
        System.setProperty("java.awt.headless", "true");
        Main sim = new Main();

        Field gf = Main.class.getDeclaredField("grid");  gf.setAccessible(true);
        Field pf = Main.class.getDeclaredField("currentPreset"); pf.setAccessible(true);
        Grid g0 = (Grid) gf.get(sim);

        Field wf = Main.class.getDeclaredField("windBox"); wf.setAccessible(true);
        @SuppressWarnings("unchecked")
        JComboBox<WindPreset> box = (JComboBox<WindPreset>) wf.get(sim);
        box.setSelectedItem(WindPreset.GUST_EAST); // fires the listener -> sets grid wind + currentPreset
        boolean windSetLive = g0.getWindType().toString().contains("Gust");

        for (int t = 0; t < 40; t++) g0.tick();

        Method reset = Main.class.getDeclaredMethod("resetSimulation");
        reset.setAccessible(true);
        reset.invoke(sim);
        Grid g1 = (Grid) gf.get(sim);

        boolean newGrid = g1 != g0;
        Field lf = Main.class.getDeclaredField("statsLabel"); lf.setAccessible(true);
        JLabel label = (JLabel) lf.get(sim);
        String text = label.getText();
        long trees = g1.getStats().getOrDefault(TreeCell.class, 0L);
        long water = g1.getStats().getOrDefault(WaterCell.class, 0L);
        boolean statsRefreshed = text != null
                && text.contains("TreeCell: " + trees)
                && text.contains("WaterCell: " + water);
        boolean windKept = g1.getWindType().toString().contains("Gust")
                        && g1.getWindType().toString().contains("EAST");

        for (int t = 0; t < 5000 && g1.anyFire(); t++) g1.tick();
        boolean fireOut = !g1.anyFire();
        Method start = Main.class.getDeclaredMethod("startSimulation");
        start.setAccessible(true);
        start.invoke(sim);
        Field rf = Main.class.getDeclaredField("running"); rf.setAccessible(true);
        boolean startIgnored = !(boolean) rf.get(sim);

        reset.invoke(sim);
        start.invoke(sim);
        boolean startWorks = (boolean) rf.get(sim);
        Method stop = Main.class.getDeclaredMethod("stopSimulation");
        stop.setAccessible(true);
        stop.invoke(sim);

        System.out.println("windSetLive=" + windSetLive
            + " newGrid=" + newGrid + " windKept=" + windKept
            + " statsRefreshed=" + statsRefreshed
            + " fireOut=" + fireOut + " startIgnoredWhenDead=" + startIgnored
            + " startWorksAfterReset=" + startWorks);
        System.out.println((windSetLive && newGrid && windKept && statsRefreshed
                            && fireOut && startIgnored && startWorks)
            ? "FIXCHECK_ALL_PASS" : "FIXCHECK_FAIL");
    }
}
