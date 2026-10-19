package dev.anish.sprint.day01;

import dev.anish.sprint.data.Employees;
import dev.anish.sprint.day01.solutions.PayrollEngineSolution;
import dev.anish.sprint.domain.Employee;
import dev.anish.sprint.domain.Grade;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

/**
 * DAY 1 -- run this first. Every test fails until you implement PayrollEngine.
 *
 *   mvn -pl core-java test                      <- run against YOUR code
 *   mvn -pl core-java test -Dsolutions=true     <- run against the reference
 *
 * The second form exists so you can confirm the tests themselves are correct
 * before you start blaming them. Use it once, then stop using it.
 */
class PayrollEngineTest {

    /**
     * Returns your implementation, or the reference one when -Dsolutions=true.
     * This is also, incidentally, a tiny example of the Factory pattern and of
     * exactly the substitution that Spring's dependency injection automates on Day 6.
     */
    private static PayrollEngine engine() {
        return Boolean.getBoolean("solutions")
                ? new PayrollEngineSolution(Employees.ALL)
                : new PayrollEngine(Employees.ALL);
    }

    private static BigDecimal money(String v) {
        return new BigDecimal(v);
    }

    @Test
    @DisplayName("sanity: the dataset is the one the tests were written against")
    void dataset_is_intact() {
        assertThat(Employees.ALL).hasSize(40);
        assertThat(Employees.DEPARTMENTS).hasSize(5);
    }

    @Nested
    @DisplayName("Exercise 1 - headcountByDepartment")
    class Headcount {
        @Test
        void counts_every_department() {
            assertThat(engine().headcountByDepartment())
                    .containsOnly(
                            entry("ENG", 12L),
                            entry("OPS", 8L),
                            entry("FIN", 7L),
                            entry("HR", 6L),
                            entry("SALES", 7L));
        }
    }

    @Nested
    @DisplayName("Exercise 2 - totalSalaryByDepartment")
    class TotalSalary {
        @Test
        void sums_without_losing_precision() {
            Map<String, BigDecimal> actual = engine().totalSalaryByDepartment();
            assertThat(actual).hasSize(5);
            assertThat(actual.get("ENG")).isEqualByComparingTo(money("25405000"));
            assertThat(actual.get("OPS")).isEqualByComparingTo(money("11940000"));
            assertThat(actual.get("FIN")).isEqualByComparingTo(money("14660000"));
            assertThat(actual.get("HR")).isEqualByComparingTo(money("8035000"));
            assertThat(actual.get("SALES")).isEqualByComparingTo(money("10950000"));
        }

        @Test
        void grand_total_matches_the_sum_of_the_groups() {
            BigDecimal grandTotal = engine().totalSalaryByDepartment().values().stream()
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            assertThat(grandTotal).isEqualByComparingTo(money("70990000"));
        }
    }

    @Nested
    @DisplayName("Exercise 3 - topEarners")
    class TopEarners {
        @Test
        void returns_the_three_highest_paid_in_order() {
            assertThat(engine().topEarners(3))
                    .extracting(Employee::name)
                    .containsExactly("Asha Nair", "Lakshmi Krishnan", "Rohit Verma");
        }

        @Test
        void asking_for_more_than_exist_returns_everyone() {
            assertThat(engine().topEarners(500)).hasSize(40);
        }

        @Test
        void asking_for_zero_returns_empty() {
            assertThat(engine().topEarners(0)).isEmpty();
        }
    }

    @Nested
    @DisplayName("Exercise 4 - namesByGrade")
    class NamesByGrade {
        @Test
        void groups_and_sorts_names() {
            Map<Grade, List<String>> actual = engine().namesByGrade();
            assertThat(actual).hasSize(5);
            assertThat(actual.get(Grade.PRINCIPAL))
                    .containsExactly("Asha Nair", "Lakshmi Krishnan");
            assertThat(actual.get(Grade.LEAD)).containsExactly(
                    "Deepak Sharma", "Kavita Chauhan", "Neha Kulkarni",
                    "Ritu Agarwal", "Rohit Verma", "Zoya Ahmed");
            assertThat(actual.get(Grade.SENIOR)).hasSize(10).isSorted();
            assertThat(actual.get(Grade.MID)).hasSize(11).isSorted();
            assertThat(actual.get(Grade.JUNIOR)).hasSize(11).isSorted();
        }
    }

    @Nested
    @DisplayName("Exercise 5 - partitionBySalaryAbove")
    class Partition {
        @Test
        void splits_on_the_threshold() {
            Map<Boolean, List<Employee>> actual =
                    engine().partitionBySalaryAbove(money("2000000"));
            assertThat(actual.get(true)).hasSize(14);
            assertThat(actual.get(false)).hasSize(26);
        }

        @Test
        @DisplayName("both keys exist even when one side is empty - this is why partitioningBy, not groupingBy")
        void both_keys_always_present() {
            Map<Boolean, List<Employee>> actual =
                    engine().partitionBySalaryAbove(money("99999999"));
            assertThat(actual).containsKeys(true, false);
            assertThat(actual.get(true)).isEmpty();
            assertThat(actual.get(false)).hasSize(40);
        }
    }

    @Nested
    @DisplayName("Exercise 6 - headcountByEmailDomain")
    class EmailDomains {
        @Test
        void counts_each_domain() {
            assertThat(engine().headcountByEmailDomain())
                    .containsOnly(
                            entry("tech.example.com", 20L),
                            entry("finance.example.com", 7L),
                            entry("corp.example.com", 13L));
        }
    }

    @Nested
    @DisplayName("Exercise 7 - averageSalaryByDepartment")
    class AverageSalary {
        @Test
        void averages_to_two_decimal_places_half_up() {
            Map<String, BigDecimal> actual = engine().averageSalaryByDepartment();
            assertThat(actual.get("ENG")).isEqualByComparingTo(money("2117083.33"));
            assertThat(actual.get("OPS")).isEqualByComparingTo(money("1492500.00"));
            assertThat(actual.get("FIN")).isEqualByComparingTo(money("2094285.71"));
            assertThat(actual.get("HR")).isEqualByComparingTo(money("1339166.67"));
            assertThat(actual.get("SALES")).isEqualByComparingTo(money("1564285.71"));
        }

        @Test
        @DisplayName("scale is exactly 2 - averagingDouble would not give you this")
        void scale_is_exactly_two() {
            assertThat(engine().averageSalaryByDepartment().values())
                    .allSatisfy(v -> assertThat(v.scale()).isEqualTo(2));
        }
    }

    @Nested
    @DisplayName("Exercise 8 - belowOwnDepartmentAverage")
    class BelowAverage {
        @Test
        void finds_everyone_below_their_own_department_mean() {
            assertThat(engine().belowOwnDepartmentAverage())
                    .extracting(Employee::id)
                    .containsExactly(7L, 8L, 9L, 10L, 11L, 12L,
                            16L, 17L, 18L, 19L, 20L,
                            24L, 25L, 26L, 27L,
                            30L, 31L, 32L, 33L,
                            37L, 38L, 39L, 40L);
        }

        @Test
        void nobody_is_compared_against_the_wrong_department() {
            Map<String, BigDecimal> averages = engine().averageSalaryByDepartment();
            assertThat(engine().belowOwnDepartmentAverage())
                    .allSatisfy(e -> assertThat(e.salary())
                            .isLessThan(averages.get(e.department())));
        }
    }

    @Nested
    @DisplayName("Exercise 9 - topEarnersByDepartment")
    class TopPerDepartment {
        @Test
        void returns_the_top_two_in_every_department() {
            Map<String, List<Employee>> actual = engine().topEarnersByDepartment(2);
            assertThat(actual).hasSize(5);
            assertThat(actual.get("ENG")).extracting(Employee::name)
                    .containsExactly("Asha Nair", "Rohit Verma");
            assertThat(actual.get("OPS")).extracting(Employee::name)
                    .containsExactly("Ritu Agarwal", "Sanjay Patil");
            assertThat(actual.get("FIN")).extracting(Employee::name)
                    .containsExactly("Lakshmi Krishnan", "Deepak Sharma");
            assertThat(actual.get("HR")).extracting(Employee::name)
                    .containsExactly("Kavita Chauhan", "Manoj Tiwari");
            assertThat(actual.get("SALES")).extracting(Employee::name)
                    .containsExactly("Zoya Ahmed", "Siddharth Rane");
        }

        @Test
        void a_department_smaller_than_n_returns_all_of_it() {
            assertThat(engine().topEarnersByDepartment(99).get("HR")).hasSize(6);
        }
    }

    @Nested
    @DisplayName("Exercise 10 - medianSalary")
    class Median {
        @Test
        void even_sized_set_averages_the_two_middle_values() {
            assertThat(engine().medianSalary())
                    .hasValueSatisfying(v -> assertThat(v).isEqualByComparingTo(money("1537500.00")));
        }

        @Test
        void empty_input_gives_an_empty_optional() {
            PayrollEngine empty = Boolean.getBoolean("solutions")
                    ? new PayrollEngineSolution(List.of())
                    : new PayrollEngine(List.of());
            assertThat(empty.medianSalary()).isEqualTo(Optional.empty());
        }
    }
}
