-- Educational Institute Management System - Initial Schema
-- This script creates the initial database schema for the application

-- Create courses table
CREATE TABLE courses (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,
    description TEXT,
    duration_months INTEGER NOT NULL CHECK (duration_months >= 1 AND duration_months <= 60),
    fees DECIMAL(10,2) NOT NULL CHECK (fees > 0),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'INACTIVE', 'ARCHIVED')),
    created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create companies table
CREATE TABLE companies (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,
    industry VARCHAR(50),
    contact_person VARCHAR(100),
    email VARCHAR(100),
    phone VARCHAR(15),
    address TEXT,
    partnership_date DATE,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'INACTIVE', 'BLACKLISTED')),
    created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create employees table
CREATE TABLE employees (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    employee_code VARCHAR(20) NOT NULL UNIQUE,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(15),
    department VARCHAR(50),
    role VARCHAR(30) NOT NULL CHECK (role IN ('ADMIN', 'COUNSELLOR', 'FACULTY', 'PLACEMENT_OFFICER', 'OPERATIONS')),
    hire_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'INACTIVE', 'TERMINATED')),
    created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create users table
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    employee_id UUID NOT NULL UNIQUE REFERENCES employees(id),
    last_login TIMESTAMP,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'INACTIVE', 'LOCKED', 'SUSPENDED')),
    failed_login_attempts INTEGER NOT NULL DEFAULT 0,
    account_locked_until TIMESTAMP,
    password_changed_date TIMESTAMP,
    created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create batches table
CREATE TABLE batches (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,
    course_id UUID NOT NULL REFERENCES courses(id),
    start_date DATE NOT NULL,
    end_date DATE,
    capacity INTEGER NOT NULL CHECK (capacity >= 1 AND capacity <= 100),
    current_enrollment INTEGER NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'PLANNED' CHECK (status IN ('PLANNED', 'ACTIVE', 'COMPLETED', 'CANCELLED')),
    instructor_id UUID REFERENCES employees(id),
    created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create leads table
CREATE TABLE leads (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE,
    phone VARCHAR(15) NOT NULL,
    course_interest VARCHAR(100),
    source VARCHAR(50),
    status VARCHAR(20) NOT NULL DEFAULT 'NEW' CHECK (status IN ('NEW', 'CONTACTED', 'INTERESTED', 'NOT_INTERESTED', 'CONVERTED', 'LOST')),
    assigned_counsellor_id UUID REFERENCES employees(id),
    converted_date TIMESTAMP,
    notes TEXT,
    next_follow_up_date TIMESTAMP,
    created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create students table
CREATE TABLE students (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    enrollment_number VARCHAR(20) NOT NULL UNIQUE,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE,
    phone VARCHAR(15) NOT NULL,
    date_of_birth DATE,
    address TEXT,
    batch_id UUID REFERENCES batches(id),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'INACTIVE', 'GRADUATED', 'DROPPED_OUT', 'SUSPENDED')),
    enrollment_date DATE NOT NULL,
    lead_id UUID REFERENCES leads(id),
    graduation_date DATE,
    final_grade VARCHAR(5),
    created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create placements table
CREATE TABLE placements (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    student_id UUID NOT NULL REFERENCES students(id),
    company_id UUID NOT NULL REFERENCES companies(id),
    position VARCHAR(100) NOT NULL,
    salary DECIMAL(10,2) CHECK (salary > 0),
    placement_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PLACED' CHECK (status IN ('PLACED', 'RESIGNED', 'TERMINATED', 'COMPLETED')),
    job_type VARCHAR(20) CHECK (job_type IN ('FULL_TIME', 'PART_TIME', 'CONTRACT', 'INTERNSHIP', 'FREELANCE')),
    work_location VARCHAR(100),
    employment_type VARCHAR(20) CHECK (employment_type IN ('PERMANENT', 'TEMPORARY', 'PROBATION', 'CONSULTANT')),
    probation_period_months INTEGER CHECK (probation_period_months >= 0 AND probation_period_months <= 24),
    joining_date DATE,
    end_date DATE,
    notes TEXT,
    created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create lead_follow_ups table for embedded FollowUp entities
CREATE TABLE lead_follow_ups (
    lead_id UUID NOT NULL REFERENCES leads(id),
    follow_up_date TIMESTAMP,
    follow_up_notes TEXT,
    next_action VARCHAR(200)
);

-- Create student_status_history table for embedded StatusHistory entities
CREATE TABLE student_status_history (
    student_id UUID NOT NULL REFERENCES students(id),
    status VARCHAR(20) NOT NULL,
    change_date TIMESTAMP NOT NULL,
    notes TEXT
);

-- Create indexes for better performance
CREATE INDEX idx_courses_status ON courses(status);
CREATE INDEX idx_companies_status ON companies(status);
CREATE INDEX idx_employees_role ON employees(role);
CREATE INDEX idx_employees_status ON employees(status);
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_employee_id ON users(employee_id);
CREATE INDEX idx_batches_course_id ON batches(course_id);
CREATE INDEX idx_batches_status ON batches(status);
CREATE INDEX idx_batches_start_date ON batches(start_date);
CREATE INDEX idx_leads_status ON leads(status);
CREATE INDEX idx_leads_assigned_counsellor_id ON leads(assigned_counsellor_id);
CREATE INDEX idx_leads_created_date ON leads(created_date);
CREATE INDEX idx_students_batch_id ON students(batch_id);
CREATE INDEX idx_students_status ON students(status);
CREATE INDEX idx_students_enrollment_date ON students(enrollment_date);
CREATE INDEX idx_students_lead_id ON students(lead_id);
CREATE INDEX idx_placements_student_id ON placements(student_id);
CREATE INDEX idx_placements_company_id ON placements(company_id);
CREATE INDEX idx_placements_placement_date ON placements(placement_date);
CREATE INDEX idx_placements_status ON placements(status);

-- Add triggers to update the updated_date columns
CREATE OR REPLACE FUNCTION update_updated_date_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_date = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_courses_updated_date BEFORE UPDATE ON courses FOR EACH ROW EXECUTE FUNCTION update_updated_date_column();
CREATE TRIGGER update_companies_updated_date BEFORE UPDATE ON companies FOR EACH ROW EXECUTE FUNCTION update_updated_date_column();
CREATE TRIGGER update_employees_updated_date BEFORE UPDATE ON employees FOR EACH ROW EXECUTE FUNCTION update_updated_date_column();
CREATE TRIGGER update_users_updated_date BEFORE UPDATE ON users FOR EACH ROW EXECUTE FUNCTION update_updated_date_column();
CREATE TRIGGER update_batches_updated_date BEFORE UPDATE ON batches FOR EACH ROW EXECUTE FUNCTION update_updated_date_column();
CREATE TRIGGER update_leads_updated_date BEFORE UPDATE ON leads FOR EACH ROW EXECUTE FUNCTION update_updated_date_column();
CREATE TRIGGER update_students_updated_date BEFORE UPDATE ON students FOR EACH ROW EXECUTE FUNCTION update_updated_date_column();
CREATE TRIGGER update_placements_updated_date BEFORE UPDATE ON placements FOR EACH ROW EXECUTE FUNCTION update_updated_date_column();