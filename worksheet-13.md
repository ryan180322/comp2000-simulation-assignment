# COMP2000 Worksheet 2 — Final Submission

**Student name:** Ryan Ouyang

**GitHub repo URL:** https://github.com/ryan180322/comp2000-simulation-assignment

---

## 1. Design Patterns

[pattern diagram](patterns.pdf)

**1.1.** Which design pattern(s) did you implement? Name them and state where each appears in your codebase.

Observer pattern and strategy pattern.

**1.2.** For each pattern, paste a code snippet showing the key interaction between participants.

Observer:

```
// Grid.java
private final List<Observer> observers = new ArrayList<>();
public void registerObserver(Observer o)  { observers.add(o); }
public void removeObserver(Observer o)    { observers.remove(o); }

public void tick() {
    ...
    notifyObservers();
}
private void notifyObservers() {
    for (Observer o : observers) o.update(this);
}

// Main.java
private void attachObservers(Grid g) {
    g.registerObserver(display);
    g.registerObserver(newGrid -> updateStatsLabel(statsLabel));
}
```

Strategy:

```
// TreeCell.java
boolean ignites = IntStream.range(0, 4)
        .filter(i -> snapGrid.inBounds(x + dx[i], y + dy[i]))
        .filter(i -> snapGrid.getCell(x + dx[i], y + dy[i]) instanceof FireCell)
        .anyMatch(i -> {
            double windMod = wind.getSpreadMultiplier(-dx[i], -dy[i]);
            return Math.random() < Math.min(fireSpreadProb * windMod, 1.0);
        });

// Grid.tick()
windType.advance();

// Main.java
WindPreset preset = (WindPreset) windBox.getSelectedItem();
if (preset != null) grid.setWindType(preset.create());
```

**1.3.** For each pattern, explain why you chose it over an alternative. What trade-off did you make?

Observer: better than Main polling every widget each tick — adding a new viewer no longer means editing Main or the reset path. Trade-off: I push the whole Grid reference instead of change events, which is fine at 625 cells.

Strategy: better than a switch over wind types inside TreeCell — adding GustWind touched zero existing classes. Trade-off: one extra indirection per neighbour test, invisible at this size.

---

## 2. Streams / Lambdas / Parallelism

**2.1.** List every file in your project that uses lambda expressions or the Streams API.

Grid.java: cellStream() Arrays.stream(cells).flatMap(Arrays::stream)

TreeCell.java: IntStream.range(0,4).filter(...).anyMatch(lambda)

Main.java: button/combo lambdas, lambda observer registration, Arrays.stream(WindPreset.values()).forEach(windBox::addItem), joining() for the stats text

**2.2.** For the most sophisticated use, paste the code and explain: what does it do, and why was a stream/lambda better than a loop?

The fire-spread decision in TreeCell.update() (snippet in 1.2): checks the 4 neighbours for fire and tests each against the wind multiplier.

The old loop used try/catch around getCell to skip out-of-bounds neighbours — filter(inBounds) makes that explicit and keeps exceptions as errors. anyMatch also short-circuits, so a tree gets one ignition decision per tick instead of up to four.

**2.3.** Did you use parallel streams, custom collectors, or the Fork/Join framework? If yes, how did you ensure thread safety? If no, where could parallelism be introduced?

Parallelism was implemented but not used.

getStats(): with a 25x25 grid, using parallelism takes longer than not.

Cell.update(): edits the grid directly based on adjacent cells. 2 workers touching adjacent cells is a data race.

---

## 3. Uniqueness and Creativity

**3.1.** What did you add to your simulation that went beyond the in-class activities? Describe the feature and what inspired it.

WaterCell lakes which never catch on fire and never grow trees.

GustWind with oscillating wind strength.

Random rain that extinguishes fires and increase tree growth.

**3.2.** Paste a code snippet or describe a design decision that demonstrates emergent behaviour, non-trivial complexity, or a genuinely surprising outcome.

WaterCell has no fire-blocking logic. Trees catch fire by looking at neighbouring FireCells. Water never catches fire.

**3.3.** If you had another 6 weeks, what would you add or change?

A chart observer showing trees/fire/empty counts over time.

Double-buffered updates plus an injectable Random seed, which would make runs reproducible and unlock parallelism at bigger grid sizes.

Terrain moisture/elevation so rain and wind vary across the map.

---

## 4. Log Book and Final Reflection

**4.1.** Attach or link your log book entries for Weeks 7–13.

logbook.md in repo

**4.2.** Reflect on the semester:

- What was the most important concept you learned?
  
  Design for change. My three late features were added after the core was done: GustWind only implements WindType, WaterCell only extends Cell, and Grid's spread logic was never edited for either.

- What design trade-off did you face, and how did you resolve it?
  
  Polling vs Observer. Polling is simpler today, Observer is cheaper for every future viewer. I resolved it by counting edit sites: adding the "(raining)" indicator and auto-pause each touched zero observers under Observer, and would each have touched the tick loop under polling.

- If you started over, what would you do differently?
  
  Snapshot-based updates from the start — the read-snapshot/write-live split would have avoided several boundary and update-order bugs. Also make the random seed injectable so runs are reproducible.

- What feedback from the Week 7 submission did you act on?
  
  Main was updating Display and stats by hand each tick — replaced with Observer (attachObservers + one notifyObservers per tick). Exceptions were used for bounds checking in the neighbour scan — replaced with inBounds + filter. The getStats() double-count bug was fixed with groupingBy.
