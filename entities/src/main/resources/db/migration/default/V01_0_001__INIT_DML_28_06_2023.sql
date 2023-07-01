CREATE TYPE role as ENUM("EMPLOYEE","ADMIN");

CREATE TABLE public.employee(
    employee_id BIGINT NOT NULL AUTO_INCREMENT,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255),
    token VARCHAR(255) UNIQUE,
    role role NOT NULL,
    created_at DATE NOT NULL,
    PRIMARY KEY (employee_id)
);

CREATE TABLE public.attendance(
    attendance_id BIGINT NOT NULL AUTO_INCREMENT,
    attendance_date DATE NOT NULL,
    time_in TIME,
    PRIMARY KEY (attendance_id),
    FOREIGN KEY (employee_id) REFERENCES employee (employee_id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE

);

INSERT INTO public.employee(employee_id,first_name,last_name,email,password,token,role,created_at)
VALUES ("1","Aliu","Salaudeen", "admin@encentral.com", "admin","3rv2gy54yty54tvc4r","ADMIN","2023-06-25");