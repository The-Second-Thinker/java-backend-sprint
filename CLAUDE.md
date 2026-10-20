# Java Backend Sprint

I'm an Oracle PL/SQL developer learning Java backend development in 10 weekday
mornings (19-30 Oct 2026). Goal: job-ready on Java, JDBC, Spring Boot, JPA.
Target: backend roles; I need to pass Java and coding interviews.

## How to help me
- I learn by writing code. Review and diagnose; do NOT hand me full solutions.
- When I'm stuck, explain WHY it fails and give a hint first.
- Prefer comparisons to Oracle/PL/SQL concepts I already know.
- Call out production traps (e.g. singleton state, BigDecimal vs double, @Transactional proxies).
- Do not read or open anything under `solutions/` unless I ask.
- Never run git commit or git push unless I ask.

## Project
- Maven multi-module, JDK 21, package dev.anish.sprint
- core-java/: Days 1-3 (streams, collections, concurrency)
- db/: Oracle schema (user javalab), seed data, PL/SQL package EMP_PKG
- Same EmployeeRepository interface is implemented three times: JDBC, JdbcTemplate, JPA
- Money is always BigDecimal. Packages: no COMMIT inside PL/SQL; the Java caller owns the transaction.
- Oracle: localhost:1521, service XEPDB1 (or FREEPDB1), user javalab/javalab

## Plan
Day 1 streams, 2 collections/generics/Optional, 3 concurrency, 4-5 JDBC+Oracle,
6 Spring Core, 7 Spring Boot REST, 8 JPA/Hibernate, 9 validation/logging/testing,
10 capstone: Employee Management System.