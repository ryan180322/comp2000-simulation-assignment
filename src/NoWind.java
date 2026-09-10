public class NoWind implements WindType {
    @Override
    public double getSpreadMultiplier(int dx, int dy) {
        return 1.0;
    }

    @Override
    public String toString() {
        return "No wind";
    }
}