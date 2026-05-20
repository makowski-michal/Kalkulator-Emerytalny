CREATE TABLE users (
    id          BIGSERIAL PRIMARY KEY,
    email       VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role        VARCHAR(20)  NOT NULL CHECK (role IN ('EMPLOYER','EMPLOYEE')),
    is_active   BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE TABLE companies (
    id       BIGSERIAL PRIMARY KEY,
    name     VARCHAR(255) NOT NULL,
    nip      VARCHAR(10)  UNIQUE,
    regon    VARCHAR(14),
    owner_id BIGINT NOT NULL REFERENCES users(id)
);

CREATE TABLE employees (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT UNIQUE NOT NULL REFERENCES users(id),
    company_id      BIGINT        NOT NULL REFERENCES companies(id),
    first_name      VARCHAR(100)  NOT NULL,
    last_name       VARCHAR(100)  NOT NULL,
    pesel           VARCHAR(11)   UNIQUE,
    address         TEXT,
    birth_date      DATE          NOT NULL,
    retirement_age  INTEGER       NOT NULL DEFAULT 65,
    zus_title_code  VARCHAR(6),
    gross_salary    NUMERIC(12,2) NOT NULL,
    tax_relief      BOOLEAN       NOT NULL DEFAULT TRUE
);

CREATE TABLE payslips (
    id                          BIGSERIAL PRIMARY KEY,
    employee_id                 BIGINT        NOT NULL REFERENCES employees(id),
    period_year                 INTEGER       NOT NULL,
    period_month                INTEGER       NOT NULL CHECK (period_month BETWEEN 1 AND 12),
    document_date               DATE,
    gross_salary                NUMERIC(12,2) NOT NULL,
    bonus                       NUMERIC(12,2) NOT NULL DEFAULT 0,
    allowances                  NUMERIC(12,2) NOT NULL DEFAULT 0,
    sick_leave_days             INTEGER       NOT NULL DEFAULT 0,
    unpaid_leave_days           INTEGER       NOT NULL DEFAULT 0,
    garnishment                 NUMERIC(12,2) NOT NULL DEFAULT 0,
    voluntary_deduction         NUMERIC(12,2) NOT NULL DEFAULT 0,
    pension_contrib_employee    NUMERIC(12,2) NOT NULL,
    disability_contrib_employee NUMERIC(12,2) NOT NULL,
    sickness_contrib            NUMERIC(12,2) NOT NULL,
    health_contrib              NUMERIC(12,2) NOT NULL,
    income_tax_advance          NUMERIC(12,2) NOT NULL,
    net_salary                  NUMERIC(12,2) NOT NULL,
    employer_total_cost         NUMERIC(12,2) NOT NULL,
    created_at                  TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    UNIQUE (employee_id, period_year, period_month)
);

CREATE TABLE gus_life_expectancy (
    id               BIGSERIAL PRIMARY KEY,
    year             INTEGER       NOT NULL,
    age              INTEGER       NOT NULL,
    gender           CHAR(1)       NOT NULL CHECK (gender IN ('M','F')),
    months_remaining NUMERIC(6,2)  NOT NULL,
    UNIQUE (year, age, gender)
);
