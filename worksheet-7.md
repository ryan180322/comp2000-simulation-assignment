# COMP2000 Worksheet 1 — Mid-Semester Submission

**Student name:**

**Student ID:**

**GitHub repo URL:**

---

## 1. Version Control

**1.1.** Paste the first 10 lines of the output of `git log --graph --oneline --all` from your repository:

```



```

**1.2.** Describe your workflow. Did you use branches? Pull requests?

I forked the team repository and worked from there.

**1.3.** Estimate the percentage of commits you contributed relative to the total in your repository.

I did a different simulation to my team.

---

## 2. Program Design

**2.1.** List every class in your project and write 1–2 sentences describing its responsibility.

Main - controls UI elements
Display - draws grid for Main to display
Grid - contains a 2D array of Cells, convert EmptyCells into TreeCells
Cell - abstract method
EmptyCell - does nothing by itself
TreeCell - turns into FireCell if near another FireCell. Also has a low chance to turn into FireCell randomly(lightning strike)
InvalidGridPositionException - exception for TreeCell's FireCell detection going out of range
FireCell - turns into EmptyCell after a while
NoWind/CardinalWind - modifier for the rate of fire spreading depending on direction

**2.2.** Identify any inheritance relationships. For each parent–child pair, list what the child inherits and what it overrides.

EmptyCell, TreeCell, FireCell all inherit the abstract class Cell. They override basically everything.

Main and Display inherit JPanel as they have UI elements.

InvalidGridPositionException inherits RuntimeException.

**2.3.** Pick the class that you think has the best design. Explain why.



**2.4.** Paste one code snippet that demonstrates your use of polymorphism or encapsulation.  Include an explanation of _how_ this demonstrates polymorphim or encapsulation.  Give a reference to a provided reading that talks about this type of polymorphism or encapsulation.

```
public abstract class Cell {
    protected final int x;
    protected final int y;

    public Cell(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() { return x; }
    public int getY() { return y; }

    public abstract void update(Grid snapGrid, Grid grid, WindType wind);
    ...
}

public class FireCell extends Cell {
    private int burnTime;
    private static final int MAX_BURN_TIME = 10;

    public FireCell(int x, int y) {
        super(x, y);
        this.burnTime = MAX_BURN_TIME;
    }

    @Override
    public void update(Grid snapGrid, Grid grid, WindType wind) {
        ...
    }
...
```


---

## 3. Generics and Exceptions

**3.1.** List every place your code uses generics (e.g. `ArrayList<Actor>`, `Optional<Cell>`, `HashMap<String, Team>`). If you deliberately used none, explain why.

Used in Grid and Main to return a list of Integer stats for JLabel.

**3.2.** List every place your code handles exceptions (try/catch, throws, custom exception classes). What error is each protecting against?

InvalidGridPositionException when trying to access squares out of range.

**3.3.** Paste a code snippet showing either a generic class/method or a try/catch block.

```
for (int i = 0; i < 4; i++) {
    try {
        Cell neighbour = snapGrid.getCell(x + dx[i], y + dy[i]);
        if (neighbour instanceof FireCell) {
            double windMod = wind.getSpreadMultiplier(-dx[i], -dy[i]);
            double adj = Math.min(fireSpreadProb * windMod, 1.0);
            if (Math.random() < adj) {
                grid.setCell(x, y, new FireCell(x, y));
            }
        }
    } catch (InvalidGridPositionException e) {
        continue;
    }
}
```

---

## 4. Log Book

**4.1.** Attach or link your log book entries for Weeks 1–6.



**4.2.** Which week's activity taught you the most? What did you learn?



---

## 5. Uniqueness and Creativity

**5.1.** List everything you added to the project that was not part of the in-class activities.

**5.2.** Which feature required the most independent research or problem-solving? What did you learn from it?

**5.3.** Paste one code snippet that you are especially proud of. Explain why it goes beyond what was done in class.
