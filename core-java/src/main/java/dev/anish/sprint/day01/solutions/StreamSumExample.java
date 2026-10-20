package dev.anish.sprint.day01.solutions;
import dev.anish.sprint.data.Employees;
import dev.anish.sprint.domain.Employee;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class StreamSumExample {
    public static void main(String[] args) {

        Map<String, BigDecimal> salaryByDept =
                Employees.ALL.stream()
                        .collect(Collectors.groupingBy(
                                Employee::department,
                                Collectors.reducing(
                                        BigDecimal.ZERO,
                                        Employee::salary,
                                        BigDecimal::add
                                )
                        ));

        System.out.println(salaryByDept);
    }
}
