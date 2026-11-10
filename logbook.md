# Log Book

**COMP2000 Semester Project — Forest Fire Simulation**

---

week 10-12: https://docs.google.com/document/d/1s5Tjmb2WEJH9IAnh04tVfQ_poUYtkF619NWucBKYoN4/

## Week 7 — Patterns 1 (conference class, first graded submission)

**Class:** Conference style — students presented their assignment-one work, 15 min + 5 min Q&A. Topics were Strategy, Observer, Open/Closed, composition over inheritance.

**Reflection on my own submission (due this week):** Submitted my fork: 25x25 grid, Cell hierarchy (Tree/Empty/Fire), Swing UI with Start/Stop/Reset, wind, live stats. Comparing to my Week 3 object model: the Cell inheritance hierarchy held up — new cell types are just subclasses. Two things did not: wind behaviour is a switch inside CardinalWind (harder to extend than I expected — that is exactly the Strategy/OCP gap we just learned about), and Main refreshes Display and the stats label by hand every tick, so every new viewer means editing Main. To my Week 3 self: separate "what a cell does" from "who watches the grid" before the viewers multiply. Also found a bug — getStats() counts trees twice (stream assigns, then a loop adds again); fix logged for next iteration.

---

## Week 8 — Patterns 2 (debate class)

**Class:** Teams debated assertions about design patterns using another team's repository as evidence; 10-minute public debates, crowd vote. Topics: Decorator, Iterator, State, OCP revisited.

**Prep/reading check:** Head First chapters 3, 9 (Iterator section), 10 done. The vending-machine State exercise made me realise FireCell's burnTime countdown is an implicit state machine (burning -> ash) that we just leave as an if-statement.

**Project work this week (pattern guidance — pick one pattern and implement it):** Started a side draft of the Observer pattern for my simulation: Grid as subject, Display as observer, so Main stops polling. Also sketched renaming WindType explicitly as the Strategy with Grid as the context. The draft did not merge this week — it was written against an older model (package and parameter mismatches) — so I kept it separate and planned to port the idea, not the files. Iterator also noted: every client of Grid is forced into a nested for x / for y loop; an Iterable would fix that and unlock stream usage later.

---

## Week 9 — Behaviour Parameterisation (code golf)

**Class:** Six puzzles (Puzzle1–6), each one stream/lambda method body, scored by fewest characters with correct output, tests run before submit. Simultaneous reporting — teams presented their shortest solution and other teams asked questions.

**Project work this week (lambda-a-strategy guidance):** Checked my simulation for behaviour chosen by field or if-else chain. The candidate is WindType — it has exactly one abstract method, so it is already a functional interface. Drafted a lambda variant of the wind factory:

```java
static final WindType NONE = (dx, dy) -> 1.0;
static WindType blowing(int ux, int uy, double speed) {
    return (dx, dy) -> 1.0 + (speed - 1.0) * (dx * ux + dy * uy);
}
```

Compared the two versions: the lambda version deletes NoWind/CardinalWind and kills the direction switch (dot product covers all directions), but the class version is easier to give state to — which matters, because a time-varying wind (gusts) needs a tick counter, and a lambda holding mutable clock state gets ugly fast. Keeping both versions so the worksheet can argue the comparison: lambda replaces a simple class hierarchy cleanly, but Strategy-as-class wins as soon as the strategy needs its own lifecycle.
