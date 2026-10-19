# Module 1 — Modern Java: 20 interview questions

Answers are written at the depth an interviewer wants to hear, not the depth a
blog post gives. Where there is a follow-up question they will almost certainly
ask, it is marked **Follow-up**.

---

### 1. Is a lambda the same as an anonymous inner class?

No. An anonymous inner class is a real class, compiled to its own `.class` file
(`Outer$1.class`), and instantiated with `new`. A lambda compiles to an
`invokedynamic` instruction; the JVM uses `LambdaMetafactory` to spin the
implementing class at first execution, so no class file exists on disk.

Three consequences that matter:

- A lambda does not have its own `this`. Inside a lambda, `this` refers to the
  enclosing instance; inside an anonymous class it refers to the anonymous
  instance. This causes real bugs.
- A stateless lambda can be cached and reused by the JVM; a `new` anonymous
  class allocates every time.
- Lambdas cannot have instance fields or an initialiser block.

**Follow-up:** *Why did they do it this way?* Because emitting a class per
lambda would have exploded the class count in a codebase using streams heavily,
and `invokedynamic` leaves the strategy to the runtime so it can change without
recompiling your code.

Run `StreamsExplained` demo 6 and show them the output if you have a laptop.

---

### 2. What is a functional interface?

An interface with exactly one abstract method, so a lambda can implement it.
`default` and `static` methods do not count, and neither do public methods that
override `Object` (`equals`, `hashCode`, `toString`).

`@FunctionalInterface` is optional and only makes the compiler enforce the rule.
Use it anyway — it turns a breaking change into a compile error.

The four you must know: `Function<T,R>`, `Predicate<T>`, `Consumer<T>`,
`Supplier<T>`. Everything else in `java.util.function` is a variation on arity
(`BiFunction`) or a primitive specialisation that avoids boxing (`IntPredicate`).

---

### 3. Why are streams lazy?

Intermediate operations (`filter`, `map`, `sorted`, `limit`) only build a
pipeline description. Nothing executes until a terminal operation
(`collect`, `forEach`, `count`, `reduce`, `findFirst`) runs.

Laziness buys three things: operations fuse into a single pass, short-circuiting
becomes possible, and infinite streams (`Stream.iterate`) become usable.

The database analogy is exact: parsing a cursor does not fetch rows; `FETCH`
does.

---

### 4. Does `filter` run over the whole collection before `map` starts?

No, and this is the most common misconception. Processing is **vertical**: one
element travels through the entire pipeline before the next element is touched.

This is why `filter` before `map` does strictly less work than `map` before
`filter` — the same reasoning as predicate pushdown in a query plan.

The exception is **stateful** intermediate operations. `sorted()` and
`distinct()` must buffer the whole stream before they can emit anything.

---

### 5. Can you reuse a stream?

No. After a terminal operation the stream is consumed and any further operation
throws `IllegalStateException: stream has already been operated upon or closed`.

If you need two traversals, keep the source collection and call `.stream()`
again, or hold a `Supplier<Stream<T>>`.

---

### 6. `map` versus `flatMap`?

`map` applies a one-to-one transform: `Stream<T>` to `Stream<R>`.
`flatMap` applies a one-to-many transform and then flattens one level:
each element becomes a `Stream<R>`, and those streams are concatenated.

The case where only `flatMap` works: you have `List<Department>`, each holding
`List<Employee>`, and you want every employee. `map(Department::employees)`
gives you `Stream<List<Employee>>` — a stream of lists, which is not what you
asked for. `flatMap(d -> d.employees().stream())` gives `Stream<Employee>`.

`Optional` has `flatMap` for the same reason: to avoid `Optional<Optional<T>>`.

---

### 7. What happens internally when two keys collide in a `HashMap`?

`HashMap` holds an array of buckets. The key's `hashCode()` is spread (the high
bits are XORed into the low bits) and then masked to an index.

On collision the entry is appended to that bucket's linked list. When one bucket
reaches **8** entries **and** the table has at least **64** buckets, that bucket
is converted to a red-black tree, so worst-case lookup degrades to O(log n)
rather than O(n). It reverts to a list below 6 entries.

Default capacity 16, load factor 0.75, so it resizes at 12 entries — and
resizing rehashes everything.

**Follow-up:** *What if `hashCode()` always returns the same value?* Every entry
lands in one bucket. The map still works but lookup becomes O(log n) after
treeification, and before Java 8 it was O(n) — which was an actual denial-of-service
vector.

---

### 8. What breaks if `equals` and `hashCode` disagree?

Every hash-based collection. Two objects that are `equals` but have different
hash codes land in different buckets, so `map.get(key)` returns `null` for a key
that is logically present, and a `HashSet` happily holds both.

The contract: equal objects must have equal hash codes. Unequal objects *may*
share a hash code.

**Follow-up:** *What if you mutate a field used in `hashCode` after inserting
into a `HashMap`?* The entry is lost — it sits in the bucket matching the old
hash and is looked up at the new one. This is why map keys should be immutable,
and why records make good keys.

---

### 9. `Comparable` versus `Comparator`?

`Comparable` defines one natural ordering, implemented by the class itself
(`compareTo`). `Comparator` defines any number of alternative orderings, outside
the class.

Modern form, which they want to see:

```java
Comparator.comparing(Employee::salary)
          .thenComparing(Employee::name)
          .reversed();
```

**Trap:** `.reversed()` applies to the whole chain built so far, not just the
last `thenComparing`. Getting this wrong is a common bug.

---

### 10. Why is `String` immutable?

Four reasons, and interviewers want more than "for safety":

- **String pool.** Literals are interned and shared. If one reference could
  mutate the value, every other holder would see the change.
- **Hash caching.** `String` caches its hash code, which is only valid because
  the contents cannot change. This is what makes `String` fast as a map key.
- **Security.** File paths, URLs and credentials are passed as strings. A mutable
  string could be changed between a security check and its use.
- **Thread safety.** Immutable objects need no synchronisation.

---

### 11. What is type erasure?

Generic type information exists at compile time and is removed from the bytecode.
`List<String>` and `List<Integer>` are both `List` at runtime.

What this prevents: `new T[]`, `instanceof List<String>`, overloading on
`List<String>` versus `List<Integer>`, and catching a generic exception type.

Why they did it: migration compatibility. Pre-generics code had to keep running
on the new JVM without recompilation.

---

### 12. Explain PECS.

**Producer `extends`, Consumer `super`.**

`? extends T` — the structure produces `T`s for you. You can read `T` out; you
cannot write into it (except `null`), because the compiler does not know the
actual subtype.

`? super T` — the structure consumes `T`s. You can write `T` in; reads come back
as `Object`.

The canonical signature:

```java
public static <T> void copy(List<? extends T> source, List<? super T> target)
```

Source produces, target consumes. If both were plain `List<T>` you could not
copy `List<Integer>` into `List<Number>`.

---

### 13. When should a method return `Optional`?

When absence is a normal, expected outcome and the caller must be forced to deal
with it — typically a lookup that may find nothing.

**Never** use it as a field type (it is not serialisable and adds an allocation
per instance), **never** as a method parameter (overload or pass null instead),
and never return `Optional<Collection>` — return an empty collection.

Calling `.get()` without `isPresent()` reintroduces the exact failure you were
avoiding. Use `orElseThrow()`, `orElse()`, `map`, `filter`.

---

### 14. Checked versus unchecked exceptions, and why has modern Java moved away from checked?

`Exception` and its subclasses except `RuntimeException` are checked: the
compiler forces you to catch or declare them. `RuntimeException` and `Error` are
unchecked.

The argument against checked exceptions: they do not compose with lambdas (a
`Function` cannot throw one), they leak implementation detail up the call stack,
and in practice they produce `catch (SQLException e) { throw new RuntimeException(e); }`
noise rather than real handling.

Spring made this concrete: `JdbcTemplate` converts every checked `SQLException`
into the unchecked `DataAccessException` hierarchy, and translates vendor error
codes so that ORA-00001 becomes `DuplicateKeyException`.

---

### 15. What does try-with-resources actually do?

It compiles to a `try`/`finally` that calls `close()` on anything implementing
`AutoCloseable`, in reverse order of declaration.

The part people miss: if the body throws *and* `close()` throws, the body's
exception propagates and the close exception is attached via
`addSuppressed()` — reachable through `getSuppressed()`. Hand-written
`finally` blocks lose the original exception instead, which is why
try-with-resources is not just syntax sugar.

---

### 16. `Collectors.groupingBy` versus `partitioningBy`?

`groupingBy` takes a classifier returning any key type. `partitioningBy` takes a
`Predicate` and returns exactly `Map<Boolean, List<T>>`.

The difference that gets asked: **`partitioningBy` always contains both `true`
and `false` keys**, even when one side is empty. `groupingBy(e -> predicate)`
omits a key with no matches, so `get(true)` returns `null` and your next line
throws.

---

### 17. How do you sum a `BigDecimal` stream?

There is no `Collectors.summingBigDecimal`. The JDK only provides
`summingInt`, `summingLong`, `summingDouble`.

```java
stream.map(Employee::salary).reduce(BigDecimal.ZERO, BigDecimal::add)
```

or, as a downstream collector:

```java
Collectors.reducing(BigDecimal.ZERO, Employee::salary, BigDecimal::add)
```

**Follow-up:** *Why not `summingDouble(e -> e.salary().doubleValue())`?* Because
binary floating point cannot represent most decimal fractions exactly, so money
accumulates error. `0.1 + 0.2 != 0.3` in `double`. Use `BigDecimal` with an
explicit scale and `RoundingMode` for anything financial.

---

### 18. `HashMap` versus `ConcurrentHashMap` versus `Collections.synchronizedMap`?

`HashMap` — not thread-safe. Concurrent writes can corrupt it; before Java 8 a
concurrent resize could produce an infinite loop in `get()`.

`Collections.synchronizedMap` — wraps every method in a single lock on the whole
map. Correct, but all threads serialise, and iteration still needs manual
synchronisation.

`ConcurrentHashMap` — lock-free reads, and writes lock only the affected bin.
Iterators are weakly consistent: they never throw
`ConcurrentModificationException` but may not see concurrent updates.
`null` keys and values are forbidden, because `get()` returning `null` would be
ambiguous without an atomic `containsKey`.

---

### 19. What is the difference between `Stream.iterate` and `Stream.generate`?

`iterate(seed, f)` produces an ordered sequence where each element derives from
the previous one. `generate(supplier)` produces unordered elements with no
relationship.

Both are infinite unless bounded by `limit()` — or, for `iterate`, by the
three-argument form `iterate(seed, hasNext, next)` added in Java 9, which is the
functional equivalent of a `for` loop.

---

### 20. When is a parallel stream actually faster?

Rarely, and the honest answer impresses more than enthusiasm.

It needs all of: a large dataset, a cheap splitting source (an array or
`ArrayList`, not a `LinkedList`), a CPU-bound operation, no shared mutable state,
and no ordering requirement.

Against it: parallel streams use the common `ForkJoinPool` by default, so a
blocking operation in one starves every other parallel stream in the JVM. In a
web application the container is already using all cores to serve concurrent
requests, so there is usually no spare parallelism to exploit.

**The answer they want:** measure it. If you cannot show a benchmark, do not
use it.
