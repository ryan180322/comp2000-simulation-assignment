public interface WindType {
    double getSpreadMultiplier(int dx, int dy);

    default void advance() {}
}
