# Module 1 cheat sheet — read this on the morning of an interview

## Stream pipeline = SQL

| Stream | SQL |
| --- | --- |
| `.stream()` | `FROM` |
| `.filter(p)` | `WHERE` |
| `.map(f)` | `SELECT expr` |
| `.flatMap(f)` | un-nest a collection column |
| `.distinct()` | `SELECT DISTINCT` |
| `.sorted(c)` | `ORDER BY` |
| `.limit(n)` / `.skip(n)` | `FETCH FIRST n` / `OFFSET n` |
| `.collect(groupingBy(f))` | `GROUP BY` |
| downstream collector | the aggregate inside the `GROUP BY` |
| `.anyMatch(p)` | `EXISTS` |
| `.count()` | `COUNT(*)` |

## The four functional interfaces

```java
Function<T,R>   r = t -> ...;   // takes T, returns R      .apply()
Predicate<T>    p = t -> ...;   // takes T, returns boolean .test()
Consumer<T>     c = t -> ...;   // takes T, returns nothing .accept()
Supplier<T>     s = ()  -> ...; // takes nothing, returns T .get()
```

Plus `BiFunction<T,U,R>`, `UnaryOperator<T>` (a `Function<T,T>`),
`BinaryOperator<T>`, and the primitive forms (`IntPredicate`) that avoid boxing.

## Collectors worth memorising

```java
toList()  toSet()  toUnmodifiableList()
toMap(keyFn, valFn)                     // throws on duplicate key
toMap(keyFn, valFn, mergeFn)            // merge instead of throwing
joining(", ", "[", "]")
counting()
groupingBy(classifier)
groupingBy(classifier, downstream)
groupingBy(classifier, TreeMap::new, downstream)
partitioningBy(predicate)               // ALWAYS both true and false keys
mapping(mapper, downstream)
collectingAndThen(downstream, finisher)
reducing(identity, mapper, op)          // the BigDecimal sum
summarizingDouble(fn)                   // count, sum, min, max, average at once
teeing(c1, c2, merger)                  // two collectors, one pass (Java 12+)
```

## Things that will catch you out

| Trap | Reality |
| --- | --- |
| `toMap` with a duplicate key | throws `IllegalStateException` — pass a merge function |
| `toMap` with a null value | throws `NullPointerException`, unlike `HashMap.put` |
| `groupingBy(e -> boolean)` | missing key when nothing matches — use `partitioningBy` |
| `.reversed()` after `thenComparing` | reverses the **whole** chain, not the last clause |
| `peek()` for side effects | may be skipped entirely — it is a debugging tool |
| `Collectors.averagingDouble` on money | binary floating point error — use `BigDecimal` |
| reusing a stream | `IllegalStateException` |
| `Optional.get()` | use `orElseThrow()` so the failure is explicit |
| `List.of(...).add(...)` | `UnsupportedOperationException` — it is immutable |
| `Arrays.asList(...).add(...)` | also throws — fixed size, but `set()` works |

## Collection internals, one line each

- `ArrayList` — array; O(1) get, O(n) insert in the middle, grows by 1.5x
- `LinkedList` — doubly linked; O(1) insert given a node, O(n) get, poor cache locality. Rarely the right answer.
- `HashMap` — bucket array, chaining, treeify at 8 entries in a bucket with table ≥ 64. Resizes at capacity × 0.75
- `LinkedHashMap` — `HashMap` plus insertion order; with `accessOrder=true` and `removeEldestEntry` it is an LRU cache in ten lines
- `TreeMap` — red-black tree, sorted, O(log n), lets you ask for ranges
- `ArrayDeque` — the right choice for a stack or queue; faster than `Stack` and `LinkedList`
- `ConcurrentHashMap` — lock-free reads, per-bin write locks, no nulls

## equals / hashCode contract

1. Equal objects must have equal hash codes
2. Unequal objects may share a hash code
3. Both must be consistent while the object is in a collection
4. Records generate both from all components — prefer them for keys and DTOs

## BigDecimal rules for money

```java
new BigDecimal("1234.56")        // ALWAYS the String constructor
BigDecimal.valueOf(1234.56)      // acceptable; goes via Double.toString
new BigDecimal(1234.56)          // NEVER - captures the binary error
a.compareTo(b) == 0              // value equality
a.equals(b)                      // ALSO compares scale: 1.0 != 1.00
a.divide(b, 2, RoundingMode.HALF_UP)   // division WITHOUT a scale can throw
```

## Exceptions

- Checked: compiler-enforced. Unchecked: `RuntimeException` and below.
- try-with-resources closes in reverse order and records suppressed exceptions
- Never catch and swallow. Never `catch (Exception e) { e.printStackTrace(); }`
- Catch the specific type, add context, rethrow or handle — one of the three

## Java 8 → 21, what actually gets used

| Version | Feature you will use |
| --- | --- |
| 8 | lambdas, streams, `Optional`, `java.time`, default methods |
| 9 | `List.of`, `Stream.iterate` with predicate, `Optional.stream` |
| 10 | `var` for local variables |
| 11 | `String.isBlank/strip/lines`, `Files.readString`, HTTP client |
| 14 | records, helpful `NullPointerException` messages |
| 16 | pattern matching for `instanceof` |
| 17 | sealed classes, switch expressions |
| 21 | virtual threads, pattern matching for `switch`, record patterns |
