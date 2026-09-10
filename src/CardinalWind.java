public class CardinalWind implements WindType {
    private final WindDirection direction;
    private final double speedMultiplier;

    public CardinalWind(WindDirection direction) {
        this(direction, 2.0);
    }

    public CardinalWind(WindDirection direction, double speedMultiplier) {
        this.direction = direction;
        this.speedMultiplier = Math.max(1.0, speedMultiplier);
    }

    @Override
    public double getSpreadMultiplier(int dx, int dy) {
        switch (direction) {
            case NONE:
                return 1.0;
            case NORTH:
                if (dy == -1) return speedMultiplier;           // with wind
                if (dy == 1)  return 1.0 / speedMultiplier;     // against wind
                return 1.0; // perpendicular
            case SOUTH:
                if (dy == 1)  return speedMultiplier;
                if (dy == -1) return 1.0 / speedMultiplier;
                return 1.0;
            case EAST:
                if (dx == 1)  return speedMultiplier;
                if (dx == -1) return 1.0 / speedMultiplier;
                return 1.0;
            case WEST:
                if (dx == -1) return speedMultiplier;
                if (dx == 1)  return 1.0 / speedMultiplier;
                return 1.0;
            default:
                return 1.0;
        }
    }

    @Override
    public String toString() {
        return String.format("Wind %s (x%.1f)", direction, speedMultiplier);
    }
}
