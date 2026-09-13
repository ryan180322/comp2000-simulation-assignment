# Log Book — Weeks 2–6

**COMP2000 Semester Project — Forest Fire Simulation**

---

## Week 2 — Git & Version Control

### Classroom Topics (Week 2)

- Version control systems: centralised (CVCS) vs decentralised (DVCS)
- Git internals: the three-tree model (working directory → staging area → repository)
- Commands: `git init`, `git clone`, `git add`, `git commit`, `git status`
- History inspection: `git log`, `git blame`, `git log --graph --oneline --all`
- Synchronisation: `git fetch`, `git pull`, `git push`, collaborators
- Git as a messaging system (EPIC activity)

### What I Did This Week

#### EPIC Activity: Git Discord Chat

The class exercise was to use Git as a chat server. Each commit was treated as a "chat message" — the commit message was the text, and committed files were "attachments."

This taught me that **commits are chat messages**. The `git log --graph --oneline --all` command reads bottom-to-top like a conversation timeline.

---

## Week 3 — Objects and Classes

### Classroom Topics (Week 3)

- Classes vs objects: blueprint vs concrete instance
- Java API documentation (JFrame, JPanel, Point)
- Constructors: default vs parameterised, `this` keyword
- Getters/setters, data hiding, clean interfaces
- Strings, `toString()`, `@Override` annotations
- Memory: stack vs heap, value vs reference, `null`
- Static variables and methods
- Compile-time typing vs dynamic typing
- **UML notation** for design communication

### ### What I Did This Week

#### Interface Design

I sketched the Forest Fire Simulation interface:

- A large grid panel in the centre (showing the fire simulation)
- Control buttons (Start, Stop, Reset) below the grid
- Wind direction controls (dropdown)

This directly led to the `Main extends JPanel` class with `BorderLayout` — centre for the grid display, south for controls.

#### 

---

## Week 4 — Inheritance and Overloading

### Classroom Topics (Week 4)

- Inheritance: `extends` to create class hierarchies
- Subclassing and overriding: `@Override` for specialised behaviour
- Abstract classes: contracts that subclasses must implement
- Interfaces: `implements` for behaviour contracts
- Method overloading: same name, different parameters
- **Polymorphism**: treating different subclasses through a common supertype
- The diamond problem (Java avoids it via single inheritance)



### What I Did This Week

#### Building the Cell Hierarchy

Before Week 4, all my cell types had duplicated code (x, y coordinates, update method signature). The EPIC task told me to extract shared state into a parent class.

**Before (duplicated in each class):**

```java
// In EmptyCell.java
private int x, y;
public void update(...) { ... }
public Color getColor() { ... }

// In TreeCell.java
private int x, y;
public void update(...) { ... }
public Color getColor() { ... }
```

**After (inherited from `Cell`):**

```java
public abstract class Cell {
    protected final int x, y;
    public abstract void update(...);
    public abstract Color getColor();
}

public class TreeCell extends Cell {
    @Override public void update(...) { /* tree-specific logic */ }
    @Override public Color getColor() { /* tree-specific color */ }
}
```



---

## Week 5 — Generics

### Classroom Topics (Week 5)

- Primitive types vs reference types (auto-boxing with Integer, etc.)
- Arrays of primitives vs arrays of `Object`
- **The problem**: `Object` collections require casting and are not type-safe
- **Generics syntax**: `List<T>`, `ArrayList<T>`
- **Type erasure**: generic type info is removed at compile time
- Raw types, heap pollution, unchecked warnings
- Bounded type parameters, wildcard types
- `Optional<T>`



### What I Did This Week

#### Understanding Type Erasure

The teacher asked: "What does `new ArrayList<String>().getClass()` return at runtime?" The answer: `class java.util.ArrayList` — the `<String>` part disappears! Both `ArrayList<String>` and `ArrayList<Integer>` have the same runtime class.

This means generics are a **compile-time** feature only. The compiler inserts casts automatically and checks types, but at runtime, the JVM can't tell the difference.

#### Applying Generics to The Project

**Problem:** I needed a way to collect simulation statistics (how many trees, fires, empty cells) at each tick. Initially I considered using a raw `List` or an `Object[]`.

**Solution:** Use `List<Integer>` for type-safe statistics:

```java
public List<Integer> getLiveStats() {
    List<Integer> stats = new ArrayList<>();
    stats.add(trees);    // auto-boxed to Integer
    stats.add(fires);
    stats.add(empties);
    return stats;
}
```

#### Exception Handling with Grid Bounds

While working on the grid, I found that `TreeCell.update()` checks 4 neighbours, and boundary cells sometimes go out of range. Instead of returning `null` (which causes confusing `NullPointerException`), I created `InvalidGridPositionException`:

```java
public class InvalidGridPositionException extends RuntimeException {
    public InvalidGridPositionException(int x, int y, int width, int height) {
        super(String.format("(%d,%d) outside grid bounds (0-%d, 0-%d)",
                x, y, width - 1, height - 1));
    }
}
```



---

## Week 6 — Exceptions

### Classroom Topics (Week 6)

- **Call stack**: how method calls are stacked
- **Exception propagation**: how a thrown exception travels up the stack
- **Catch placement**: where in the call chain `try-catch` executes
- **`finally`**: code that always runs, exception or not
- **Checked vs unchecked** exceptions
- **Throwing your own exceptions**
- EPIC: Barcode puzzle — trace call stack unwinding with `throw` and `catch`



### What I Did This Week

#### Tracing the Call Stack

The teacher's key exercise was: "if `chuck_a_fit()` throws and the exception is not caught in `pipe()`, does `pipe()` finish its exit print?" The answer: **no**. When an exception propagates through a method, that method's exit code is skipped — the stack frame is popped without running any code after the `throw`.

This helped me understand why `InvalidGridPositionException` needed to be caught **where the error occurs** (in `TreeCell.update()`), not higher up in the call chain. If I had let it propagate to `Grid.tick()` without catching it, the entire tick would fail and the fire wouldn't spread that round.

#### Why Catch at the Boundary?

```java
// In TreeCell.update() — catches at the source
try {
    Cell neighbour = snapGrid.getCell(x + dx[i], y + dy[i]);
    ...
} catch (InvalidGridPositionException e) {
    continue;  // just skip this neighbour
}
```

If I didn't catch it here, the exception would propagate up through:

1. `TreeCell.update()` → skipped
2. `Grid.tick()` → skipped (entire tick fails)
3. `Timer` in `Main` → app might crash

By catching at the boundary (in `TreeCell`), only that one neighbour check fails — the fire continues spreading from all other valid neighbours.

#### Reflection on Exceptions

**Checked vs Unchecked:**

- Checked exceptions (e.g., `IOException`) must be caught or declared with `throws` — good for recoverable errors
- Unchecked exceptions (e.g., `RuntimeException`) are for programming errors — things that "shouldn't happen" if the code is correct
- `InvalidGridPositionException` is unchecked because out-of-bounds access means the caller made a logic error

**Design Decision:**
I chose to extend `RuntimeException` rather than creating a checked exception because:

1. Every call to `getCell()` would need `try-catch` or `throws` — very verbose
2. The error is recoverable (skip the boundary check and continue)
3. The caller doesn't need to "handle" it — just catch and ignore at the boundary

---
