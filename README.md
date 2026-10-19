# Java Backend Sprint

Ten weekday mornings, 19–30 October 2026. Oracle PL/SQL developer to
production-capable Spring Boot developer, by building one system rather than
watching videos about ten.

The full curriculum, the reasoning behind the sequence, and the interview
strategy live in the plan document. This README is what you need to get the
code running.

---

## What this repository is

One application — an Enterprise Employee Management System — whose data layer
gets rewritten three times:

```
EmployeeRepository (interface)
        |
        +-- JdbcEmployeeRepository          Days 4-5   raw JDBC, hand-written
        +-- JdbcTemplateEmployeeRepository   Day 6      Spring JDBC
        +-- JpaEmployeeRepository            Day 8      Spring Data JPA
```

The service layer above never changes. That is the point: you feel what each
abstraction buys and what it costs, instead of being told.

---

## Setup — 45 minutes, once

### 1. JDK 21

In IntelliJ: **File → Project Structure → SDKs → + → Download JDK → Temurin 21**.
No PATH editing required.

### 2. Maven

You do not need to install it. IntelliJ ships its own copy — open the **Maven**
tool window (right edge) and use it, or right-click any class with a `main`
method and choose **Run**.

If you want Maven on the command line, install it separately; every `mvn`
command in this repository assumes that.

### 3. Oracle schema

Create the practice user as `SYSTEM`, connected to the pluggable database.
Use `FREEPDB1` instead of `XEPDB1` if you are on Oracle 23ai Free.

```sql
ALTER SESSION SET CONTAINER = XEPDB1;

CREATE USER javalab IDENTIFIED BY javalab
  DEFAULT TABLESPACE users
  QUOTA UNLIMITED ON users;

GRANT CONNECT, RESOURCE TO javalab;
GRANT CREATE VIEW, CREATE SEQUENCE, CREATE PROCEDURE TO javalab;
```

Then build the schema. You do not need this until Day 4, but doing it now means
no morning is lost to it:

```bash
sqlplus javalab/javalab@localhost:1521/XEPDB1 @db/01_schema.sql
sqlplus javalab/javalab@localhost:1521/XEPDB1 @db/02_seed.sql
sqlplus javalab/javalab@localhost:1521/XEPDB1 @db/03_emp_pkg.sql
```

The last script ends with a smoke test. It should print `ENG headcount = 12`.

### 4. Claude Code in IntelliJ

Install the [Claude Code CLI](https://code.claude.com/docs/en/quickstart), then
the [Claude Code plugin](https://plugins.jetbrains.com/plugin/27310-claude-code-beta-)
from the JetBrains Marketplace, then restart the IDE.

`Ctrl+Esc` opens it. Select code first and it is sent automatically — you never
paste into a chat window.

---

## Day 1 — start here

Three things, in this order.

**1. Watch streams actually behave** (15 min). Six demonstrations that prove
things most articles only assert — laziness, vertical processing, why operation
order changes how much work happens, and why a lambda is not an anonymous class.

```bash
mvn -pl core-java compile exec:java -Dexec.mainClass=dev.anish.sprint.day01.StreamsExplained
```

Read the output beside the source. Where the output surprises you, that is the
thing you did not actually know.

**2. The warm-up** (30 min). Two problems, hash map frequency pattern.

```bash
mvn -pl core-java compile exec:java -Dexec.mainClass=dev.anish.sprint.dsa.Day01Problems
```

**3. The build block** (90 min). Ten methods in `PayrollEngine`, escalating from
a one-liner to genuinely hard. Every one throws until you write it.

```bash
# zero dependencies, works before Maven has downloaded anything
mvn -pl core-java compile exec:java -Dexec.mainClass=dev.anish.sprint.day01.Day01Check

# the real suite, JUnit 5 + AssertJ
mvn -pl core-java test

# sanity-check the tests themselves against the reference implementation
mvn -pl core-java test -Dsolutions=true
```

Rules for the build block: no `for`, no `while`, no `if` inside those methods,
and money stays `BigDecimal` throughout. The constraint is artificial and it is
the point.

---

## Layout

```
db/                       Oracle schema, seed data, EMP_PKG (PL/SQL)
core-java/                Days 1-3: modern Java and concurrency
  day01/
    StreamsExplained      runnable demonstrations
    PayrollEngine         <- your build block
    Day01Check            zero-dependency smoke check
    solutions/            open only after you have tried
  dsa/                    interview warm-up problems
interview/
  questions/              20 questions per module, with answers
  java-coding/            Java-specific coding-round problems
  dsa/                    the 40-problem set, by pattern
  cheatsheets/            one per module, for the morning of an interview
  revision/               condensed notes
```

Modules for Days 4–10 are added as we reach them, so you are never reading
ahead of what you have built.

---

## The one rule

**If you have not typed it, you have not learned it.**

Reading a working implementation and nodding feels identical to understanding
and is not — which is the whole reason the Coursera certificates did not make
you job-ready. When you are given a complete implementation, delete half and
rewrite that half before moving on.

Commit at the end of every morning. By 30 October that is ten dated commits of
real work, which is portfolio evidence as much as it is practice.
