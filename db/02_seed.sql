-- ============================================================================
-- Seed data. Mirrors dev.anish.sprint.data.Employees exactly, so the JDBC and
-- JPA repositories can be tested against the same expected values as the
-- in-memory Day 1 and Day 2 exercises.
-- ============================================================================

DELETE FROM employee;
DELETE FROM department;

INSERT INTO department (code, name, location) VALUES ('ENG',   'Engineering',     'Bengaluru');
INSERT INTO department (code, name, location) VALUES ('OPS',   'Operations',      'Bengaluru');
INSERT INTO department (code, name, location) VALUES ('FIN',   'Finance',         'Mumbai');
INSERT INTO department (code, name, location) VALUES ('HR',    'Human Resources', 'Mumbai');
INSERT INTO department (code, name, location) VALUES ('SALES', 'Sales',           'Delhi');

-- Managers must exist before their reports, so this is ordered, and the
-- self-referencing FK is deferred to the end for the two rows that need it.
INSERT INTO employee VALUES (1,  'Asha Nair',        'asha.nair@tech.example.com',        'ENG',   'PRINCIPAL', 4800000, DATE '2016-03-14', NULL, 0);
INSERT INTO employee VALUES (2,  'Rohit Verma',      'rohit.verma@tech.example.com',      'ENG',   'LEAD',      3200000, DATE '2017-07-01', 1,    0);
INSERT INTO employee VALUES (3,  'Neha Kulkarni',    'neha.kulkarni@tech.example.com',    'ENG',   'LEAD',      3050000, DATE '2018-01-22', 1,    0);
INSERT INTO employee VALUES (4,  'Imran Shaikh',     'imran.shaikh@tech.example.com',     'ENG',   'SENIOR',    2400000, DATE '2019-05-06', 2,    0);
INSERT INTO employee VALUES (5,  'Divya Rao',        'divya.rao@tech.example.com',        'ENG',   'SENIOR',    2375000, DATE '2019-11-18', 2,    0);
INSERT INTO employee VALUES (6,  'Karan Mehta',      'karan.mehta@tech.example.com',      'ENG',   'SENIOR',    2210000, DATE '2020-02-03', 3,    0);
INSERT INTO employee VALUES (7,  'Sneha Pillai',     'sneha.pillai@tech.example.com',     'ENG',   'MID',       1650000, DATE '2021-06-14', 3,    0);
INSERT INTO employee VALUES (8,  'Arjun Das',        'arjun.das@tech.example.com',        'ENG',   'MID',       1580000, DATE '2021-09-27', 2,    0);
INSERT INTO employee VALUES (9,  'Fatima Khan',      'fatima.khan@tech.example.com',      'ENG',   'MID',       1495000, DATE '2022-01-10', 3,    0);
INSERT INTO employee VALUES (10, 'Vikram Singh',     'vikram.singh@tech.example.com',     'ENG',   'JUNIOR',    920000,  DATE '2023-07-03', 4,    0);
INSERT INTO employee VALUES (11, 'Priya Menon',      'priya.menon@tech.example.com',      'ENG',   'JUNIOR',    880000,  DATE '2024-02-19', 4,    0);
INSERT INTO employee VALUES (12, 'Tanmay Joshi',     'tanmay.joshi@tech.example.com',     'ENG',   'JUNIOR',    845000,  DATE '2024-08-05', 5,    0);
INSERT INTO employee VALUES (13, 'Ritu Agarwal',     'ritu.agarwal@tech.example.com',     'OPS',   'LEAD',      2750000, DATE '2017-04-10', 1,    0);
INSERT INTO employee VALUES (14, 'Sanjay Patil',     'sanjay.patil@tech.example.com',     'OPS',   'SENIOR',    2050000, DATE '2019-08-26', 13,   0);
INSERT INTO employee VALUES (15, 'Meera Iyer',       'meera.iyer@tech.example.com',       'OPS',   'SENIOR',    1980000, DATE '2020-03-16', 13,   0);
INSERT INTO employee VALUES (16, 'Harsh Gupta',      'harsh.gupta@tech.example.com',      'OPS',   'MID',       1420000, DATE '2021-11-08', 14,   0);
INSERT INTO employee VALUES (17, 'Anjali Bose',      'anjali.bose@tech.example.com',      'OPS',   'MID',       1375000, DATE '2022-05-30', 14,   0);
INSERT INTO employee VALUES (18, 'Nikhil Reddy',     'nikhil.reddy@tech.example.com',     'OPS',   'JUNIOR',    810000,  DATE '2023-10-02', 15,   0);
INSERT INTO employee VALUES (19, 'Shruti Deshmukh',  'shruti.deshmukh@tech.example.com',  'OPS',   'JUNIOR',    795000,  DATE '2024-04-15', 15,   0);
INSERT INTO employee VALUES (20, 'Arnav Chatterjee', 'arnav.chatterjee@tech.example.com', 'OPS',   'JUNIOR',    760000,  DATE '2025-01-13', 15,   0);
INSERT INTO employee VALUES (21, 'Lakshmi Krishnan', 'lakshmi.krishnan@finance.example.com', 'FIN','PRINCIPAL', 4200000, DATE '2015-09-07', NULL, 0);
INSERT INTO employee VALUES (22, 'Deepak Sharma',    'deepak.sharma@finance.example.com', 'FIN',   'LEAD',      2900000, DATE '2018-06-11', 21,   0);
INSERT INTO employee VALUES (23, 'Pooja Bhatt',      'pooja.bhatt@finance.example.com',   'FIN',   'SENIOR',    2150000, DATE '2020-01-27', 22,   0);
INSERT INTO employee VALUES (24, 'Rahul Nanda',      'rahul.nanda@finance.example.com',   'FIN',   'SENIOR',    1920000, DATE '2020-10-19', 22,   0);
INSERT INTO employee VALUES (25, 'Swati Jain',       'swati.jain@finance.example.com',    'FIN',   'MID',       1340000, DATE '2022-03-21', 23,   0);
INSERT INTO employee VALUES (26, 'Gaurav Malhotra',  'gaurav.malhotra@finance.example.com','FIN',  'MID',       1285000, DATE '2022-12-05', 23,   0);
INSERT INTO employee VALUES (27, 'Ishita Roy',       'ishita.roy@finance.example.com',    'FIN',   'JUNIOR',    865000,  DATE '2024-06-24', 24,   0);
INSERT INTO employee VALUES (28, 'Kavita Chauhan',   'kavita.chauhan@corp.example.com',   'HR',    'LEAD',      2400000, DATE '2018-02-05', NULL, 0);
INSERT INTO employee VALUES (29, 'Manoj Tiwari',     'manoj.tiwari@corp.example.com',     'HR',    'SENIOR',    1750000, DATE '2020-07-13', 28,   0);
INSERT INTO employee VALUES (30, 'Reshma Dsouza',    'reshma.dsouza@corp.example.com',    'HR',    'MID',       1240000, DATE '2022-08-29', 28,   0);
INSERT INTO employee VALUES (31, 'Aditya Kapoor',    'aditya.kapoor@corp.example.com',    'HR',    'MID',       1180000, DATE '2023-01-16', 29,   0);
INSERT INTO employee VALUES (32, 'Nandini Shetty',   'nandini.shetty@corp.example.com',   'HR',    'JUNIOR',    740000,  DATE '2024-09-09', 29,   0);
INSERT INTO employee VALUES (33, 'Varun Saxena',     'varun.saxena@corp.example.com',     'HR',    'JUNIOR',    725000,  DATE '2025-03-17', 29,   0);
INSERT INTO employee VALUES (34, 'Zoya Ahmed',       'zoya.ahmed@corp.example.com',       'SALES', 'LEAD',      2600000, DATE '2017-11-20', NULL, 0);
INSERT INTO employee VALUES (35, 'Siddharth Rane',   'siddharth.rane@corp.example.com',   'SALES', 'SENIOR',    2080000, DATE '2019-02-11', 34,   0);
INSERT INTO employee VALUES (36, 'Preeti Bansal',    'preeti.bansal@corp.example.com',    'SALES', 'SENIOR',    1890000, DATE '2021-04-26', 34,   0);
INSERT INTO employee VALUES (37, 'Yash Thakur',      'yash.thakur@corp.example.com',      'SALES', 'MID',       1460000, DATE '2022-09-12', 35,   0);
INSERT INTO employee VALUES (38, 'Ananya Ghosh',     'ananya.ghosh@corp.example.com',     'SALES', 'MID',       1310000, DATE '2023-05-08', 35,   0);
INSERT INTO employee VALUES (39, 'Rakesh Yadav',     'rakesh.yadav@corp.example.com',     'SALES', 'JUNIOR',    830000,  DATE '2024-11-25', 36,   0);
INSERT INTO employee VALUES (40, 'Simran Kaur',      'simran.kaur@corp.example.com',      'SALES', 'JUNIOR',    780000,  DATE '2025-06-02', 36,   0);

COMMIT;

SELECT dept_code, COUNT(*) AS headcount, SUM(salary) AS total_salary
FROM   employee
GROUP  BY dept_code
ORDER  BY dept_code;
