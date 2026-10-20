package dev.anish.sprint.day01;

import dev.anish.sprint.data.Employees;
import dev.anish.sprint.domain.Employee;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * DAY 1, FIRST THING. Run this before you write any code.
 *
 *   mvn -pl core-java compile exec:java -Dexec.mainClass=dev.anish.sprint.day01.StreamsExplained
 *
 * or just right-click -> Run in IntelliJ.
 *
 * Six demonstrations. Each one PROVES something that articles about streams
 * only assert. Read the output next to the code; where the output surprises
 * you, that is the thing you did not actually know.
 */
public class StreamsExplained {

    public static void main(String[] args) {
        demo1_nothingRunsWithoutATerminalOperation();
        demo2_processingIsVerticalNotHorizontal();
        demo3_operationOrderChangesHowMuchWorkHappens();
        demo4_shortCircuiting();
        demo5_aStreamIsSingleUse();
        demo6_aLambdaIsNotAnAnonymousClass();
    }

    // =================================================================
    // 1. LAZINESS
    // Intermediate operations build a pipeline. They do not execute.
    // In Oracle terms: this is the difference between parsing a cursor
    // and fetching from it. OPEN does not retrieve rows; FETCH does.
    // =================================================================
    private static void demo1_nothingRunsWithoutATerminalOperation() {
        header("1. Nothing runs without a terminal operation");

        Stream<String> pipeline = Employees.ALL.stream()
                .filter(e -> {
                    System.out.println("   filter saw " + e.name());
                    return e.department().equals("HR");
                })
                .map(e -> {
                    System.out.println("   map saw " + e.name());
                    return e.name().toUpperCase();
                });

        System.out.println("Pipeline built. Notice: not one line printed above this.");
        System.out.println("Now adding the terminal operation .count():");
        long n = pipeline.count();
        System.out.println("Result: " + n);

        System.out.println();
        System.out.println("Now the same thing WITHOUT a filter:");
        long m = Employees.ALL.stream()
                .peek(e -> System.out.println("   this line will never print"))
                .count();
        System.out.println("Count = " + m + ", and peek never ran.");
        System.out.println();
        System.out.println("Why: the source is a sized List and nothing in the pipeline");
        System.out.println("can change how many elements come out, so count() asks the");
        System.out.println("list for its size and skips the pipeline entirely. Add a");
        System.out.println("filter and it can no longer do that -- which is exactly what");
        System.out.println("you saw above. Laziness taken to its conclusion.");
        System.out.println();
        System.out.println("TRAP: this is why you must never rely on peek() for side");
        System.out.println("effects. It is a debugging tool and the JDK is allowed to");
        System.out.println("skip it.");
    }

    // =================================================================
    // 2. VERTICAL, NOT HORIZONTAL
    // The single most common misconception. People imagine filter runs
    // over the whole list, THEN map runs over the whole result. It does not.
    // Each element is pushed through the entire pipeline before the next
    // element is touched. Watch the interleaving in the output.
    // =================================================================
    private static void demo2_processingIsVerticalNotHorizontal() {
        header("2. Each element goes through the whole pipeline before the next starts");

        List<String> result = Employees.ALL.stream()
                .limit(4)
                .peek(e -> System.out.println("   [filter stage] " + e.name()))
                .filter(e -> e.salary().signum() > 0)
                .peek(e -> System.out.println("      [map   stage]  " + e.name()))
                .map(Employee::name)
                .toList();

        System.out.println("Result: " + result);
        System.out.println();
        System.out.println("If processing were horizontal you would see four 'filter stage'");
        System.out.println("lines first, then four 'map stage' lines. You do not.");
    }

    // =================================================================
    // 3. ORDER OF OPERATIONS == AMOUNT OF WORK
    // Exactly the same reasoning as pushing a predicate down in a query plan.
    // =================================================================
    private static void demo3_operationOrderChangesHowMuchWorkHappens() {
        header("3. filter-then-map does less work than map-then-filter");

        Counter mapCalls = new Counter();
        Employees.ALL.stream()
                .filter(e -> e.department().equals("HR"))
                .map(e -> { mapCalls.increment(); return e.name(); })
                .toList();
        System.out.println("filter first  -> map ran " + mapCalls.value() + " times");

        Counter mapCalls2 = new Counter();
        Employees.ALL.stream()
                .map(e -> { mapCalls2.increment(); return e; })
                .filter(e -> e.department().equals("HR"))
                .map(Employee::name)
                .toList();
        System.out.println("map first     -> map ran " + mapCalls2.value() + " times");
        System.out.println();
        System.out.println("Same answer, " + (mapCalls2.value() / (double) mapCalls.value())
                + "x the work. This is predicate pushdown, and you already");
        System.out.println("apply it every time you read an execution plan.");
    }

    // =================================================================
    // 4. SHORT-CIRCUITING
    // findFirst, anyMatch, allMatch and limit stop as soon as they can.
    // The SQL analogue is FETCH FIRST 1 ROW ONLY letting the optimiser
    // abandon the scan early.
    // =================================================================
    private static void demo4_shortCircuiting() {
        header("4. Short-circuiting stops the pipeline early");

        Counter seen = new Counter();
        Employee first = Employees.ALL.stream()
                .peek(e -> seen.increment())
                .filter(e -> e.department().equals("SALES"))
                .findFirst()
                .orElseThrow();

        System.out.println("Found " + first.name() + " after examining only "
                + seen.value() + " of " + Employees.ALL.size() + " employees.");
    }

    // =================================================================
    // 5. SINGLE USE
    // A stream is a cursor, not a collection. Once consumed, it is done.
    // =================================================================
    private static void demo5_aStreamIsSingleUse() {
        header("5. A stream cannot be reused");

        Stream<Employee> stream = Employees.ALL.stream();
        System.out.println("First terminal operation  -> count = " + stream.count());
        try {
            stream.findFirst();
        } catch (IllegalStateException ex) {
            System.out.println("Second terminal operation -> " + ex.getClass().getSimpleName()
                    + ": " + ex.getMessage());
        }
        System.out.println();
        System.out.println("If you need it twice, keep the Collection and call .stream() again,");
        System.out.println("or keep a Supplier<Stream<T>>. Exactly like re-opening a cursor.");
    }

    // =================================================================
    // 6. A LAMBDA IS NOT AN ANONYMOUS INNER CLASS
    // A near-certain interview question. An anonymous class produces a
    // real class file at compile time (StreamsExplained$1.class). A lambda
    // does not: javac emits an invokedynamic instruction and the runtime
    // spins the implementation on first call.
    // =================================================================
    private static void demo6_aLambdaIsNotAnAnonymousClass() {
        header("6. A lambda is not an anonymous inner class");

        Predicate<Employee> anonymous = new Predicate<>() {
            @Override public boolean test(Employee e) { return true; }
        };
        Predicate<Employee> lambda = e -> true;
        Function<Employee, String> methodRef = Employee::name;

        System.out.println("anonymous class : " + anonymous.getClass().getName());
        System.out.println("lambda          : " + lambda.getClass().getName());
        System.out.println("method reference: " + methodRef.getClass().getName());
        System.out.println();
        System.out.println("The anonymous one is StreamsExplained$1 - a real file on disk.");
        System.out.println("Check for yourself:  ls core-java/target/classes/dev/anish/sprint/day01/");
        System.out.println("The lambdas carry $$Lambda - generated at runtime, no class file.");
        System.out.println();
        System.out.println("Consequence that matters: a lambda has no 'this' of its own.");
        System.out.println("Inside a lambda, 'this' is the enclosing instance. Inside an");
        System.out.println("anonymous class it is the anonymous instance. That difference");
        System.out.println("causes real bugs, and it is asked about in interviews.");
    }

    /** A mutable box. Needed because a lambda may only capture effectively final variables. */
    private static final class Counter {
        private int n;
        void increment() { n++; }
        int value() { return n; }
    }

    private static void header(String title) {
        System.out.println();
        System.out.println("=".repeat(70));
        System.out.println(title);
        System.out.println("=".repeat(70));
    }
}
