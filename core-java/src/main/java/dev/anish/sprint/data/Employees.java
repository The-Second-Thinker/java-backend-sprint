package dev.anish.sprint.data;

import dev.anish.sprint.domain.Department;
import dev.anish.sprint.domain.Employee;
import dev.anish.sprint.domain.Grade;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static dev.anish.sprint.domain.Grade.*;

/**
 * The fixed dataset every Day 1 and Day 2 exercise runs against.
 *
 * 40 employees, 5 departments. The numbers are deliberately uneven so that
 * averages, medians and "top three per department" produce interesting
 * answers rather than round ones.
 *
 * List.of(...) returns an IMMUTABLE list. Calling add() on it throws
 * UnsupportedOperationException. That is intentional: it stops an exercise
 * accidentally mutating the shared dataset and breaking the next test.
 */
public final class Employees {

    private Employees() { }

    private static Employee e(long id, String name, String dept, Grade grade,
                              String salary, String joined, Long mgr) {
        String email = name.toLowerCase().replace(' ', '.') + "@" + domainFor(dept);
        return new Employee(id, name, email, dept, grade,
                new BigDecimal(salary), LocalDate.parse(joined), mgr);
    }

    private static String domainFor(String dept) {
        return switch (dept) {
            case "ENG", "OPS" -> "tech.example.com";
            case "FIN"        -> "finance.example.com";
            default           -> "corp.example.com";
        };
    }

    public static final List<Department> DEPARTMENTS = List.of(
            new Department("ENG",   "Engineering",   "Bengaluru"),
            new Department("OPS",   "Operations",    "Bengaluru"),
            new Department("FIN",   "Finance",       "Mumbai"),
            new Department("HR",    "Human Resources", "Mumbai"),
            new Department("SALES", "Sales",         "Delhi")
    );

    public static final List<Employee> ALL = List.of(
            // --- ENG : 12 people ---
            e(1,  "Asha Nair",       "ENG",   PRINCIPAL, "4800000", "2016-03-14", null),
            e(2,  "Rohit Verma",     "ENG",   LEAD,      "3200000", "2017-07-01", 1L),
            e(3,  "Neha Kulkarni",   "ENG",   LEAD,      "3050000", "2018-01-22", 1L),
            e(4,  "Imran Shaikh",    "ENG",   SENIOR,    "2400000", "2019-05-06", 2L),
            e(5,  "Divya Rao",       "ENG",   SENIOR,    "2375000", "2019-11-18", 2L),
            e(6,  "Karan Mehta",     "ENG",   SENIOR,    "2210000", "2020-02-03", 3L),
            e(7,  "Sneha Pillai",    "ENG",   MID,       "1650000", "2021-06-14", 3L),
            e(8,  "Arjun Das",       "ENG",   MID,       "1580000", "2021-09-27", 2L),
            e(9,  "Fatima Khan",     "ENG",   MID,       "1495000", "2022-01-10", 3L),
            e(10, "Vikram Singh",    "ENG",   JUNIOR,    "920000",  "2023-07-03", 4L),
            e(11, "Priya Menon",     "ENG",   JUNIOR,    "880000",  "2024-02-19", 4L),
            e(12, "Tanmay Joshi",    "ENG",   JUNIOR,    "845000",  "2024-08-05", 5L),

            // --- OPS : 8 people ---
            e(13, "Ritu Agarwal",    "OPS",   LEAD,      "2750000", "2017-04-10", 1L),
            e(14, "Sanjay Patil",    "OPS",   SENIOR,    "2050000", "2019-08-26", 13L),
            e(15, "Meera Iyer",      "OPS",   SENIOR,    "1980000", "2020-03-16", 13L),
            e(16, "Harsh Gupta",     "OPS",   MID,       "1420000", "2021-11-08", 14L),
            e(17, "Anjali Bose",     "OPS",   MID,       "1375000", "2022-05-30", 14L),
            e(18, "Nikhil Reddy",    "OPS",   JUNIOR,    "810000",  "2023-10-02", 15L),
            e(19, "Shruti Deshmukh", "OPS",   JUNIOR,    "795000",  "2024-04-15", 15L),
            e(20, "Arnav Chatterjee","OPS",   JUNIOR,    "760000",  "2025-01-13", 15L),

            // --- FIN : 7 people ---
            e(21, "Lakshmi Krishnan","FIN",   PRINCIPAL, "4200000", "2015-09-07", null),
            e(22, "Deepak Sharma",   "FIN",   LEAD,      "2900000", "2018-06-11", 21L),
            e(23, "Pooja Bhatt",     "FIN",   SENIOR,    "2150000", "2020-01-27", 22L),
            e(24, "Rahul Nanda",     "FIN",   SENIOR,    "1920000", "2020-10-19", 22L),
            e(25, "Swati Jain",      "FIN",   MID,       "1340000", "2022-03-21", 23L),
            e(26, "Gaurav Malhotra", "FIN",   MID,       "1285000", "2022-12-05", 23L),
            e(27, "Ishita Roy",      "FIN",   JUNIOR,    "865000",  "2024-06-24", 24L),

            // --- HR : 6 people ---
            e(28, "Kavita Chauhan",  "HR",    LEAD,      "2400000", "2018-02-05", null),
            e(29, "Manoj Tiwari",    "HR",    SENIOR,    "1750000", "2020-07-13", 28L),
            e(30, "Reshma Dsouza",   "HR",    MID,       "1240000", "2022-08-29", 28L),
            e(31, "Aditya Kapoor",   "HR",    MID,       "1180000", "2023-01-16", 29L),
            e(32, "Nandini Shetty",  "HR",    JUNIOR,    "740000",  "2024-09-09", 29L),
            e(33, "Varun Saxena",    "HR",    JUNIOR,    "725000",  "2025-03-17", 29L),

            // --- SALES : 7 people ---
            e(34, "Zoya Ahmed",      "SALES", LEAD,      "2600000", "2017-11-20", null),
            e(35, "Siddharth Rane",  "SALES", SENIOR,    "2080000", "2019-02-11", 34L),
            e(36, "Preeti Bansal",   "SALES", SENIOR,    "1890000", "2021-04-26", 34L),
            e(37, "Yash Thakur",     "SALES", MID,       "1460000", "2022-09-12", 35L),
            e(38, "Ananya Ghosh",    "SALES", MID,       "1310000", "2023-05-08", 35L),
            e(39, "Rakesh Yadav",    "SALES", JUNIOR,    "830000",  "2024-11-25", 36L),
            e(40, "Simran Kaur",     "SALES", JUNIOR,    "780000",  "2025-06-02", 36L)
    );
}
