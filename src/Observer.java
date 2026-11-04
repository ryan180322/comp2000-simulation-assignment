/**
 * Observer interface — GoF Observer pattern.
 *
 * Any object that wants to react to the Grid changing implements this and
 * registers itself with the Grid. The Grid only knows the Observer
 * abstraction, never the concrete classes (Display, StatsLabel, ...),
 * which keeps the model decoupled from everything watching it.
 */
public interface Observer {
    /** Called by the Grid (Subject) after every simulation tick. */
    void update(Grid grid);
}
