CREATE TABLE absences (
    id          BIGSERIAL PRIMARY KEY,
    employee_id BIGINT      NOT NULL REFERENCES employees(id),
    type        VARCHAR(30) NOT NULL CHECK (type IN ('SICK_LEAVE','UNPAID_LEAVE','PAID_LEAVE','MATERNITY')),
    date_from   DATE        NOT NULL,
    date_to     DATE        NOT NULL,
    days_count  INTEGER     NOT NULL,
    note        TEXT,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
