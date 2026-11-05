/**
 * Small factory for the wind Strategy combo box: name -> ConcreteStrategy.
 * Keeping construction here means Main never grows a switch over wind types;
 * a new strategy is one enum entry + one factory line (OCP).
 */
public enum WindPreset {
    NONE, NORTH, SOUTH, EAST, WEST, GUST_NORTH, GUST_SOUTH, GUST_EAST, GUST_WEST;

    public WindType create() {
        return switch (this) {
            case NONE       -> new NoWind();
            case NORTH      -> new CardinalWind(WindDirection.NORTH);
            case SOUTH      -> new CardinalWind(WindDirection.SOUTH);
            case EAST       -> new CardinalWind(WindDirection.EAST);
            case WEST       -> new CardinalWind(WindDirection.WEST);
            case GUST_NORTH -> new GustWind(WindDirection.NORTH);
            case GUST_SOUTH -> new GustWind(WindDirection.SOUTH);
            case GUST_EAST  -> new GustWind(WindDirection.EAST);
            case GUST_WEST  -> new GustWind(WindDirection.WEST);
        };
    }
}
