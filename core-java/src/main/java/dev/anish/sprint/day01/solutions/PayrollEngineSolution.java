package dev.anish.sprint.day01.solutions;

import dev.anish.sprint.day01.PayrollEngine;
import dev.anish.sprint.domain.Employee;
import dev.anish.sprint.domain.Grade;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Reference solutions. Open this only after you have attempted the exercise.
 *
 * Read the comments as much as the code -- several of these show a choice
 * between two correct answers, and the reason for the choice is the lesson.
 */
public class PayrollEngineSolution extends PayrollEngine {

    public PayrollEngineSolution(List<Employee> employees) {
        super(employees);
    }

    @Override
    public Map<String, Long> headcountByDepartment() {
        return employees().stream()
                .collect(Collectors.groupingBy(Employee::department, Collectors.counting()));
    }

    @Override
    public Map<String, BigDecimal> totalSalaryByDepartment() {
        // There is no summingBigDecimal in the JDK: summingInt/Long/Double only.
        // reducing(identity, mapper, op) is the general-purpose replacement.
        return employees().stream()
                .collect(Collectors.groupingBy(
                        Employee::department,
                        Collectors.reducing(BigDecimal.ZERO, Employee::salary, BigDecimal::add)));
    }

    @Override
    public List<Employee> topEarners(int n) {
        // sorted() is a STATEFUL intermediate operation: it must consume the
        // whole stream before it can emit its first element. limit(n) after it
        // therefore does not save the sort any work -- it only trims the output.
        return employees().stream()
                .sorted(Comparator.comparing(Employee::salary).reversed())
                .limit(n)
                .toList();
    }

    @Override
    public Map<Grade, List<String>> namesByGrade() {
        return employees().stream()
                .collect(Collectors.groupingBy(
                        Employee::grade,
                        Collectors.mapping(
                                Employee::name,
                                Collectors.collectingAndThen(
                                        Collectors.toList(),
                                        names -> names.stream().sorted().toList()))));
    }

    @Override
    public Map<Boolean, List<Employee>> partitionBySalaryAbove(BigDecimal threshold) {
        // partitioningBy guarantees both TRUE and FALSE keys exist.
        // groupingBy(e -> predicate) would omit a key when no element matches,
        // which turns a safe get() into a null. That is the whole difference.
        return employees().stream()
                .collect(Collectors.partitioningBy(e -> e.salary().compareTo(threshold) > 0));
    }

    @Override
    public Map<String, Long> headcountByEmailDomain() {
        return employees().stream()
                .collect(Collectors.groupingBy(Employee::emailDomain, Collectors.counting()));
    }

    @Override
    public Map<String, BigDecimal> averageSalaryByDepartment() {
        // averagingDouble would silently convert money to binary floating point.
        // For 40 rows you would probably not notice; for a payroll run you would.
        return employees().stream()
                .collect(Collectors.groupingBy(
                        Employee::department,
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                PayrollEngineSolution::mean)));
    }

    @Override
    public List<Employee> belowOwnDepartmentAverage() {
        Map<String, BigDecimal> averages = averageSalaryByDepartment();
        return employees().stream()
                .filter(e -> e.salary().compareTo(averages.get(e.department())) < 0)
                .sorted(Comparator.comparingLong(Employee::id))
                .toList();
    }

    @Override
    public Map<String, List<Employee>> topEarnersByDepartment(int n) {
        return employees().stream()
                .collect(Collectors.groupingBy(
                        Employee::department,
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                list -> list.stream()
                                        .sorted(Comparator.comparing(Employee::salary).reversed())
                                        .limit(n)
                                        .toList())));
    }

    @Override
    public Optional<BigDecimal> medianSalary() {
        List<BigDecimal> sorted = employees().stream()
                .map(Employee::salary)
                .sorted()
                .toList();

        if (sorted.isEmpty()) {
            return Optional.empty();
        }

        int size = sorted.size();
        int mid = size / 2;

        // A median needs the whole sorted set, so it cannot be computed in a
        // single streaming pass over unsorted data. Same reason a distributed
        // engine computes an approximate median unless you ask it not to.
        BigDecimal median = (size % 2 == 1)
                ? sorted.get(mid)
                : sorted.get(mid - 1).add(sorted.get(mid))
                        .divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);

        return Optional.of(median.setScale(2, RoundingMode.HALF_UP));
    }

    private static BigDecimal mean(List<Employee> group) {
        BigDecimal total = group.stream()
                .map(Employee::salary)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return total.divide(BigDecimal.valueOf(group.size()), 2, RoundingMode.HALF_UP);
    }
}
