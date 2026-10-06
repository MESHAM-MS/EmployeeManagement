-- Optional: Hibernate (ddl-auto=update) creates these tables automatically.
-- Use this script if you prefer to create the schema manually.

CREATE DATABASE IF NOT EXISTS employee_management;
USE employee_management;

CREATE TABLE IF NOT EXISTS departments (
    department_id   BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    department_name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS designations (
    designation_id   BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    designation_name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS employees (
    employee_id    BIGINT        NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name           VARCHAR(100)  NOT NULL,
    email          VARCHAR(150)  NOT NULL UNIQUE,
    salary         DECIMAL(12,2) NOT NULL,
    department_id  BIGINT        NOT NULL,
    designation_id BIGINT        NOT NULL,
    CONSTRAINT fk_emp_department  FOREIGN KEY (department_id)  REFERENCES departments (department_id),
    CONSTRAINT fk_emp_designation FOREIGN KEY (designation_id) REFERENCES designations (designation_id)
);

CREATE TABLE IF NOT EXISTS employee_addresses (
    address_id  BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    employee_id BIGINT       NOT NULL UNIQUE,
    city        VARCHAR(100) NOT NULL,
    state       VARCHAR(100) NOT NULL,
    country     VARCHAR(100) NOT NULL,
    CONSTRAINT fk_addr_employee FOREIGN KEY (employee_id) REFERENCES employees (employee_id) ON DELETE CASCADE
);

-- Sample data
INSERT INTO departments (department_name) VALUES ('Engineering'), ('Human Resources'), ('Finance');
INSERT INTO designations (designation_name) VALUES ('Software Engineer'), ('Team Lead'), ('Manager');
