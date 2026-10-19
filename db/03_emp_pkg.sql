-- ============================================================================
-- EMP_PKG -- the PL/SQL you will call from Java on Day 5.
--
-- Everything here is deliberately chosen to exercise a different JDBC
-- CallableStatement pattern:
--
--   get_by_dept      -> OUT SYS_REFCURSOR consumed as a ResultSet
--   headcount        -> a FUNCTION, called as {? = call ...}
--   raise_salary     -> IN params + an OUT param + RAISE_APPLICATION_ERROR
--   bulk_raise       -> SQL%ROWCOUNT returned through an OUT param
--   transfer_dept    -> a multi-statement unit of work, no COMMIT inside
--
-- NOTE ON COMMIT: none of these procedures commit. The transaction boundary
-- belongs to the caller. This is the single most important convention when
-- PL/SQL is called from a Java application -- a COMMIT inside the package
-- would silently break Spring's @Transactional on Day 6.
-- ============================================================================

CREATE OR REPLACE PACKAGE emp_pkg AS

    -- Raised when a salary increase would breach the grade ceiling.
    e_salary_ceiling EXCEPTION;
    PRAGMA EXCEPTION_INIT(e_salary_ceiling, -20001);

    PROCEDURE get_by_dept(p_dept_code IN VARCHAR2, p_result OUT SYS_REFCURSOR);

    FUNCTION headcount(p_dept_code IN VARCHAR2) RETURN NUMBER;

    PROCEDURE raise_salary(p_employee_id IN  NUMBER,
                           p_percent     IN  NUMBER,
                           p_new_salary  OUT NUMBER);

    PROCEDURE bulk_raise(p_dept_code IN  VARCHAR2,
                         p_percent   IN  NUMBER,
                         p_affected  OUT NUMBER);

    PROCEDURE transfer_department(p_employee_id   IN NUMBER,
                                  p_new_dept_code IN VARCHAR2);

END emp_pkg;
/

CREATE OR REPLACE PACKAGE BODY emp_pkg AS

    -- Grade ceilings. In a real system these would be a table; a constant
    -- function keeps the example self-contained.
    FUNCTION ceiling_for(p_grade IN VARCHAR2) RETURN NUMBER IS
    BEGIN
        RETURN CASE p_grade
                   WHEN 'JUNIOR'    THEN 1200000
                   WHEN 'MID'       THEN 1900000
                   WHEN 'SENIOR'    THEN 2800000
                   WHEN 'LEAD'      THEN 3600000
                   WHEN 'PRINCIPAL' THEN 6000000
                   ELSE 0
               END;
    END ceiling_for;

    PROCEDURE get_by_dept(p_dept_code IN VARCHAR2, p_result OUT SYS_REFCURSOR) IS
    BEGIN
        OPEN p_result FOR
            SELECT id, name, email, dept_code, grade, salary, joined_on, manager_id, version
            FROM   employee
            WHERE  dept_code = p_dept_code
            ORDER  BY salary DESC, id;
    END get_by_dept;

    FUNCTION headcount(p_dept_code IN VARCHAR2) RETURN NUMBER IS
        v_count NUMBER;
    BEGIN
        SELECT COUNT(*) INTO v_count
        FROM   employee
        WHERE  dept_code = p_dept_code;
        RETURN v_count;
    END headcount;

    PROCEDURE raise_salary(p_employee_id IN  NUMBER,
                           p_percent     IN  NUMBER,
                           p_new_salary  OUT NUMBER) IS
        v_salary  employee.salary%TYPE;
        v_grade   employee.grade%TYPE;
        v_ceiling NUMBER;
    BEGIN
        IF p_percent <= 0 OR p_percent > 50 THEN
            RAISE_APPLICATION_ERROR(-20002,
                'Percent must be between 0 and 50, got ' || p_percent);
        END IF;

        SELECT salary, grade INTO v_salary, v_grade
        FROM   employee
        WHERE  id = p_employee_id
        FOR UPDATE;                      -- pessimistic lock; Day 8 revisits this

        p_new_salary := ROUND(v_salary * (1 + p_percent / 100), 2);
        v_ceiling    := ceiling_for(v_grade);

        IF p_new_salary > v_ceiling THEN
            RAISE_APPLICATION_ERROR(-20001,
                'Raise would breach the ' || v_grade || ' ceiling of ' || v_ceiling);
        END IF;

        UPDATE employee
        SET    salary  = p_new_salary,
               version = version + 1
        WHERE  id = p_employee_id;

    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            RAISE_APPLICATION_ERROR(-20003, 'No employee with id ' || p_employee_id);
    END raise_salary;

    PROCEDURE bulk_raise(p_dept_code IN  VARCHAR2,
                         p_percent   IN  NUMBER,
                         p_affected  OUT NUMBER) IS
    BEGIN
        UPDATE employee
        SET    salary  = ROUND(salary * (1 + p_percent / 100), 2),
               version = version + 1
        WHERE  dept_code = p_dept_code
        AND    ROUND(salary * (1 + p_percent / 100), 2) <= ceiling_for(grade);

        -- SQL%ROWCOUNT is the PL/SQL equivalent of what executeUpdate()
        -- returns in JDBC. Day 5 reads this value back through the OUT param.
        p_affected := SQL%ROWCOUNT;
    END bulk_raise;

    PROCEDURE transfer_department(p_employee_id   IN NUMBER,
                                  p_new_dept_code IN VARCHAR2) IS
        v_exists NUMBER;
    BEGIN
        SELECT COUNT(*) INTO v_exists
        FROM   department
        WHERE  code = p_new_dept_code;

        IF v_exists = 0 THEN
            RAISE_APPLICATION_ERROR(-20004, 'Unknown department ' || p_new_dept_code);
        END IF;

        UPDATE employee
        SET    dept_code  = p_new_dept_code,
               manager_id = NULL,          -- a transferred employee loses their manager
               version    = version + 1
        WHERE  id = p_employee_id;

        IF SQL%ROWCOUNT = 0 THEN
            RAISE_APPLICATION_ERROR(-20003, 'No employee with id ' || p_employee_id);
        END IF;

        -- Deliberately no COMMIT. The Java caller owns the transaction.
    END transfer_department;

END emp_pkg;
/

SHOW ERRORS;

-- Smoke test, so you know the package works before Java is involved.
SET SERVEROUTPUT ON
DECLARE
    v_count NUMBER;
BEGIN
    v_count := emp_pkg.headcount('ENG');
    DBMS_OUTPUT.PUT_LINE('ENG headcount = ' || v_count || ' (expected 12)');
END;
/
