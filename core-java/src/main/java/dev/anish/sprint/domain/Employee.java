package dev.anish.sprint.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

/**
 * A record: an immutable data carrier.
 *
 * Writing `record Employee(long id, String name, ...)` generates, at compile time:
 *   - a private final field per component
 *   - a public accessor per component, named `id()` not `getId()`
 *   - a canonical constructor
 *   - equals(), hashCode() and toString() based on ALL components
 *
 * This is the closest Java has to a PL/SQL %ROWTYPE record, with one
 * important difference: a record is immutable. There is no setter. To
 * "change" an employee you build a new one. That property is what makes
 * records safe to share across threads on Day 3.
 *
 * Note salary is BigDecimal, never double. See the Day 1 notes for why.
 * managerId is a boxed Long because it is genuinely nullable -- the CEO
 * has no manager. A primitive long could not express that.
 */
public record Employee(
        long id,
        String name,
        String email,
        String department,
        Grade grade,
        BigDecimal salary,
        LocalDate joinedOn,
        Long managerId
) {

    /**
     * A compact constructor. It runs before the fields are assigned and is
     * the place to validate. Notice there is no `this.name = name` -- the
     * compiler adds the assignments after this block.
     */
    public Employee {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name is required");
        }
        if (salary == null || salary.signum() < 0) {
            throw new IllegalArgumentException("salary must be zero or positive");
        }
    }

    /**
     * Exposing a nullable field as Optional at the boundary.
     * Optional is a RETURN type. Note the field itself stays a plain Long --
     * never declare a field as Optional.
     */
    public Optional<Long> manager() {
        return Optional.ofNullable(managerId);
    }

    /** The part of the email after '@'. Used by exercise 9. */
    public String emailDomain() {
        return email.substring(email.indexOf('@') + 1);
    }
}
