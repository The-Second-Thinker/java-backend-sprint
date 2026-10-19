package dev.anish.sprint.day01;

import dev.anish.sprint.domain.Employee;
import dev.anish.sprint.domain.Grade;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * DAY 1 BUILD BLOCK -- this is yours to implement.
 *
 * Every method throws until you write it. Run PayrollEngineTest and work
 * down the failures in order; they escalate from a one-liner to genuinely
 * hard. Do NOT look at day01/solutions until you have attempted each one.
 *
 * Rules for this exercise:
 *   1. No `for`, no `while`, no `if` statements inside these methods.
 *      Every one is expressible as a stream pipeline. The constraint is
 *      artificial and it is the point -- it forces the switch in thinking.
 *   2. No mutable accumulator variables outside the stream.
 *   3. Money stays BigDecimal all the way through. Never route it via double.
 *
 * SQL ANALOGY, because it genuinely helps here:
 *      stream()                -> FROM
 *      .filter(...)            -> WHERE
 *      .map(...)               -> SELECT expression
 *      .sorted(...)            -> ORDER BY
 *      .limit(n)               -> FETCH FIRST n ROWS ONLY
 *      .collect(groupingBy(f)) -> GROUP BY
 *      downstream collector    -> the aggregate inside the GROUP BY
 */
public class PayrollEngine {

    private final List<Employee> employees;

    public PayrollEngine(List<Employee> employees) {
        this.employees = List.copyOf(employees);
    }

    public List<Employee> employees() {
        return employees;
    }

    // ---------------------------------------------------------------
    // 1. Headcount per department.
    //    SELECT department, COUNT(*) FROM employee GROUP BY department
    //    HINT: Collectors.groupingBy(classifier, downstream)
    //          The downstream you want is Collectors.counting().
    // ---------------------------------------------------------------
    public Map<String, Long> headcountByDepartment() {
        throw new UnsupportedOperationException("Exercise 1 -- implement me");
    }

    // ---------------------------------------------------------------
    // 2. Total salary per department.
    //    HINT: there is no Collectors.summingBigDecimal(). You need
    //          Collectors.reducing(BigDecimal.ZERO, Employee::salary, BigDecimal::add)
    //          as the downstream. This gap in the JDK is a real interview talking point.
    // ---------------------------------------------------------------
    public Map<String, BigDecimal> totalSalaryByDepartment() {
        throw new UnsupportedOperationException("Exercise 2 -- implement me");
    }

    // ---------------------------------------------------------------
    // 3. The n highest-paid employees overall, highest first.
    //    HINT: Comparator.comparing(Employee::salary).reversed(), then limit(n).
    //    THINK: does sorted() have to see every element before emitting one?
    //           What does that mean for laziness here?
    // ---------------------------------------------------------------
    public List<Employee> topEarners(int n) {
        throw new UnsupportedOperationException("Exercise 3 -- implement me");
    }

    // ---------------------------------------------------------------
    // 4. Employees grouped by grade, as a sorted list of NAMES (not Employee).
    //    Map<Grade, List<String>>, each list alphabetical.
    //    HINT: groupingBy(Employee::grade, Collectors.mapping(Employee::name,
    //              Collectors.collectingAndThen(Collectors.toList(), l -> l.stream().sorted().toList())))
    //          -- or find a cleaner route. There is more than one correct answer.
    // ---------------------------------------------------------------
    public Map<Grade, List<String>> namesByGrade() {
        throw new UnsupportedOperationException("Exercise 4 -- implement me");
    }

    // ---------------------------------------------------------------
    // 5. Split into those earning strictly more than `threshold` and the rest.
    //    HINT: Collectors.partitioningBy returns a Map<Boolean, List<T>> that
    //          ALWAYS has both keys, even when one side is empty. groupingBy
    //          would not. That difference is an interview question.
    // ---------------------------------------------------------------
    public Map<Boolean, List<Employee>> partitionBySalaryAbove(BigDecimal threshold) {
        throw new UnsupportedOperationException("Exercise 5 -- implement me");
    }

    // ---------------------------------------------------------------
    // 6. How many employees share each email domain.
    //    HINT: Employee::emailDomain already exists. groupingBy + counting.
    // ---------------------------------------------------------------
    public Map<String, Long> headcountByEmailDomain() {
        throw new UnsupportedOperationException("Exercise 6 -- implement me");
    }

    // ---------------------------------------------------------------
    // 7. Average salary per department, as a BigDecimal rounded to 2 dp
    //    (RoundingMode.HALF_UP).
    //    HINT: compute the total and the count, then divide. Collectors
    //          .collectingAndThen over toList() is one clean way.
    //    TRAP: Collectors.averagingDouble(e -> e.salary().doubleValue()) is the
    //          obvious route and it is WRONG for money. Know why before you move on.
    // ---------------------------------------------------------------
    public Map<String, BigDecimal> averageSalaryByDepartment() {
        throw new UnsupportedOperationException("Exercise 7 -- implement me");
    }

    // ---------------------------------------------------------------
    // 8. Everyone paid strictly below their OWN department's average,
    //    sorted by id ascending.
    //    HINT: two passes. Build the averages map first (reuse exercise 7),
    //          then filter against it. One pass is possible but far less readable,
    //          and readable wins.
    // ---------------------------------------------------------------
    public List<Employee> belowOwnDepartmentAverage() {
        throw new UnsupportedOperationException("Exercise 8 -- implement me");
    }

    // ---------------------------------------------------------------
    // 9. The top n earners WITHIN each department, highest first.
    //    HINT: groupingBy with a downstream that sorts and limits --
    //          Collectors.collectingAndThen(Collectors.toList(), list -> ...)
    //    This is the hardest collector shape on Day 1. Take your time.
    // ---------------------------------------------------------------
    public Map<String, List<Employee>> topEarnersByDepartment(int n) {
        throw new UnsupportedOperationException("Exercise 9 -- implement me");
    }

    // ---------------------------------------------------------------
    // 10. Median salary across everyone. Optional.empty() for no employees.
    //     Even count -> mean of the two middle values, 2 dp HALF_UP.
    //     HINT: sort the salaries, then index. This is the one method where
    //           you need the list materialised before you can finish.
    //     THINK: why can a stream not compute a median in a single pass?
    //            That question is asked in interviews about Spark too.
    // ---------------------------------------------------------------
    public Optional<BigDecimal> medianSalary() {
        throw new UnsupportedOperationException("Exercise 10 -- implement me");
    }
}
