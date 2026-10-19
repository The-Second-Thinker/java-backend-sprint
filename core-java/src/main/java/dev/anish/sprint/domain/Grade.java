package dev.anish.sprint.domain;

/**
 * Employee grade. An enum in Java is a fixed set of named instances --
 * the closest Oracle analogue is a CHECK constraint on a VARCHAR2 column,
 * except the compiler enforces it instead of the database.
 */
public enum Grade {
    JUNIOR, MID, SENIOR, LEAD, PRINCIPAL
}
