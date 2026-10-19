-- ============================================================================
-- Java Backend Sprint -- practice schema
-- Run as the JAVALAB user (see README for how to create it).
--
--   sqlplus javalab/javalab@localhost:1521/XEPDB1 @db/01_schema.sql
--
-- On Oracle 23ai Free the service name is FREEPDB1, not XEPDB1.
-- ============================================================================

-- Dropped in dependency order. The first run prints ORA-00942; that is fine.
BEGIN EXECUTE IMMEDIATE 'DROP TABLE employee CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE department CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP SEQUENCE emp_seq'; EXCEPTION WHEN OTHERS THEN NULL; END;
/

CREATE TABLE department (
    code        VARCHAR2(10)  NOT NULL,
    name        VARCHAR2(100) NOT NULL,
    location    VARCHAR2(100) NOT NULL,
    CONSTRAINT pk_department PRIMARY KEY (code)
);

CREATE TABLE employee (
    id          NUMBER(19)     NOT NULL,
    name        VARCHAR2(120)  NOT NULL,
    email       VARCHAR2(160)  NOT NULL,
    dept_code   VARCHAR2(10)   NOT NULL,
    grade       VARCHAR2(20)   NOT NULL,
    salary      NUMBER(12,2)   NOT NULL,
    joined_on   DATE           NOT NULL,
    manager_id  NUMBER(19),
    version     NUMBER(10)     DEFAULT 0 NOT NULL,
    CONSTRAINT pk_employee        PRIMARY KEY (id),
    CONSTRAINT uq_employee_email  UNIQUE (email),
    CONSTRAINT fk_employee_dept   FOREIGN KEY (dept_code) REFERENCES department (code),
    CONSTRAINT fk_employee_mgr    FOREIGN KEY (manager_id) REFERENCES employee (id),
    CONSTRAINT ck_employee_salary CHECK (salary >= 0),
    CONSTRAINT ck_employee_grade  CHECK (grade IN ('JUNIOR','MID','SENIOR','LEAD','PRINCIPAL'))
);

CREATE INDEX ix_employee_dept   ON employee (dept_code);
CREATE INDEX ix_employee_salary ON employee (salary);

-- INCREMENT BY 50 is deliberate and it matters.
-- Hibernate's @GeneratedValue(strategy = SEQUENCE) defaults to allocationSize 50
-- and assumes the sequence increments by the same amount. Leave this at 1 and
-- set allocationSize = 50 on the entity, and you get duplicate-key errors.
-- Day 8 covers this; the sequence is declared here so the trap is already set up.
CREATE SEQUENCE emp_seq START WITH 1000 INCREMENT BY 50 NOCACHE;

-- version is here for Day 8's @Version optimistic locking.
COMMENT ON COLUMN employee.version IS 'JPA optimistic-lock counter (Day 8)';
