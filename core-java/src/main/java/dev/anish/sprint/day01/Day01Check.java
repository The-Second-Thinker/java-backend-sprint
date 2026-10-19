package dev.anish.sprint.day01;

import dev.anish.sprint.data.Employees;
import dev.anish.sprint.domain.Grade;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A zero-dependency smoke check for Day 1.
 *
 * The real test suite is PayrollEngineTest (JUnit 5 + AssertJ) and that is
 * what you should normally run. This exists for one reason: it needs no
 * downloads, so it works the moment you clone, before Maven has fetched
 * anything. Use it on the first morning, then switch to the JUnit suite.
 *
 *   mvn -pl core-java compile exec:java -Dexec.mainClass=dev.anish.sprint.day01.Day01Check
 *
 * Exit code 0 = all green.
 */
public class Day01Check {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        PayrollEngine p = new PayrollEngine(Employees.ALL);

        check("1  headcountByDepartment",
                () -> Objects.equals(p.headcountByDepartment(),
                        Map.of("ENG", 12L, "OPS", 8L, "FIN", 7L, "HR", 6L, "SALES", 7L)));

        check("2  totalSalaryByDepartment",
                () -> eq(p.totalSalaryByDepartment().get("ENG"), "25405000")
                   && eq(p.totalSalaryByDepartment().get("SALES"), "10950000"));

        check("3  topEarners(3)",
                () -> p.topEarners(3).stream().map(e -> e.name()).toList()
                        .equals(List.of("Asha Nair", "Lakshmi Krishnan", "Rohit Verma")));

        check("4  namesByGrade",
                () -> p.namesByGrade().get(Grade.PRINCIPAL)
                        .equals(List.of("Asha Nair", "Lakshmi Krishnan"))
                   && p.namesByGrade().get(Grade.JUNIOR).size() == 11);

        check("5  partitionBySalaryAbove(2000000)",
                () -> p.partitionBySalaryAbove(new BigDecimal("2000000")).get(true).size() == 14
                   && p.partitionBySalaryAbove(new BigDecimal("2000000")).get(false).size() == 26);

        check("6  headcountByEmailDomain",
                () -> Objects.equals(p.headcountByEmailDomain(),
                        Map.of("tech.example.com", 20L,
                               "finance.example.com", 7L,
                               "corp.example.com", 13L)));

        check("7  averageSalaryByDepartment",
                () -> eq(p.averageSalaryByDepartment().get("ENG"), "2117083.33")
                   && p.averageSalaryByDepartment().get("HR").scale() == 2);

        check("8  belowOwnDepartmentAverage",
                () -> p.belowOwnDepartmentAverage().size() == 23
                   && p.belowOwnDepartmentAverage().get(0).id() == 7L);

        check("9  topEarnersByDepartment(2)",
                () -> p.topEarnersByDepartment(2).get("FIN").stream().map(e -> e.name()).toList()
                        .equals(List.of("Lakshmi Krishnan", "Deepak Sharma")));

        check("10 medianSalary",
                () -> p.medianSalary().isPresent() && eq(p.medianSalary().get(), "1537500.00"));

        System.out.println();
        System.out.printf("%d passed, %d failed%n", passed, failed);
        if (failed > 0) {
            System.out.println("Keep going. Open PayrollEngine and work down the list.");
            System.exit(1);
        }
        System.out.println("Day 1 build block complete. Now run the JUnit suite, then Day 2.");
    }

    private static boolean eq(BigDecimal actual, String expected) {
        return actual != null && actual.compareTo(new BigDecimal(expected)) == 0;
    }

    private static void check(String label, BooleanCheck body) {
        try {
            if (body.run()) {
                passed++;
                System.out.println("  PASS  " + label);
            } else {
                failed++;
                System.out.println("  FAIL  " + label + "   (wrong answer)");
            }
        } catch (UnsupportedOperationException ex) {
            failed++;
            System.out.println("  TODO  " + label + "   (" + ex.getMessage() + ")");
        } catch (Exception | AssertionError ex) {
            failed++;
            System.out.println("  ERROR " + label + "   " + ex);
        }
    }

    @FunctionalInterface
    private interface BooleanCheck {
        boolean run() throws Exception;
    }
}
