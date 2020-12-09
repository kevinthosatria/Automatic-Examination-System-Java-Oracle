--DROP TABLES--

DROP TABLE STUDENT;
DROP TABLE TEACHER;
DROP TABLE SUBJECT;
DROP TABLE QUESTION;
DROP TABLE EXAM_PAPER;
DROP TABLE STUDENT_REPORT;
DROP TABLE CLASS;
DROP TABLE EXAMPAPER_QUESTIONS;
DROP TABLE BATCH;
DROP TABLE STUDENT_EXAM_SCORE;
DROP TABLE STUDENT_SUBJECT_REPORT;

--CREATE TABLES--

CREATE TABLE STUDENT (
    STUDENT_ID VARCHAR(9) NOT NULL, 
    STUDENT_NAME VARCHAR(30), 
    CLASS_ID VARCHAR(15), 
    EMAIL VARCHAR(30), 
    PASSWORD VARCHAR(30),
    CONSTRAINT student_pk PRIMARY KEY(STUDENT_ID)
);

CREATE TABLE TEACHER (
    STAFF_ID VARCHAR(9) NOT NULL, 
    STAFF_NAME VARCHAR(30), 
    EMAIL VARCHAR(30), 
    PASSWORD VARCHAR(30),
    CONSTRAINT teacher_pk PRIMARY KEY(STAFF_ID)
);

CREATE TABLE CLASS (
    CLASS_ID VARCHAR(15) NOT NULL, 
    CLASS_NAME VARCHAR(255), 
    STAFF_ID VARCHAR(9) NOT NULL
));


CREATE TABLE SUBJECT (
    SUBJECT_ID VARCHAR(12) NOT NULL,
    SUBJECT_NAME VARCHAR(50), 
    CLASS_ID VARCHAR(15),
    CONSTRAINT subject_pk PRIMARY KEY(SUBJECT_ID)
);

CREATE TABLE SUBJECT_TEACHER( -- Intermediate table for Many:Many relationship between Subject and Teacher
    SUBJECT_ID VARCHAR(12) NOT NULL,
    STAFF_ID VARCHAR(9) NOT NULL,
    CONSTRAINT fk_subject FOREIGN KEY(SUBJECT_ID) REFERENCES SUBJECT(SUBJECT_ID),
    CONSTRAINT fk_staff FOREIGN KEY(STAFF_ID) REFERENCES TEACHER(STAFF_ID)

CREATE TABLE EXAM ( -- Exams given by each subject 
    EXAM_ID VARCHAR(16) NOT NULL, 
    SUBJECT_ID VARCHAR(12) NOT NULL, 
    DAY VARCHAR(10), 
    START_TIME VARCHAR(5), 
    DURATION VARCHAR(5), 
    EXAM_MAX_SCORE NUMBER(5),
    CONSTRAINT exam_pk PRIMARY KEY (EXAM_ID),
    CONSTRAINT tested_by_subject FOREIGN KEY (SUBJECT_ID) REFERENCES SUBJECT (SUBJECT_ID)
);

CREATE TABLE EXAM_PAPER ( -- Individual Exam Papers given to each student
    PAPER_ID VARCHAR(10) NOT NULL,
    STUDENT_ID VARCHAR(9) NOT NULL,
    EXAM_ID VARCHAR(16) NOT NULL,
    SUBJECT_ID VARCHAR(12) NOT NULL,
    STUDENT_EXAM_SCORE NUMBER(5),
    TEACHER_FINISHED_GRADING NUMBER(1),
    CONSTRAINT exam_paper_pk PRIMARY KEY (PAPER_ID),
    CONSTRAINT fk_student FOREIGN KEY(STUDENT_ID) REFERENCES STUDENT(STUDENT_ID),
    CONSTRAINT fk_exam FOREIGN KEY(EXAM_ID) REFERENCES EXAM(EXAM_ID),
    CONSTRAINT fk_subject FOREIGN KEY(SUBJECT_ID) REFERENCES SUBJECT(SUBJECT_ID)
)

CREATE TABLE QUESTION ( -- Each Exam has a lot of questions, we also assume that all Exam_Papers with same Exam_ID have the same questions
    QUESTION_ID VARCHAR(10) NOT NULL,
    EXAM_ID VARCHAR(16) NOT NULL,
    QUESTION_TYPE VARCHAR(5), CONTENT VARCHAR(255), 
    GRAPHICS VARCHAR(5), AUDIO VARCHAR(5), 
    CORRECT_ANS VARCHAR(255), SCORE NUMBER(5), 
    ISCOMPULSORY VARCHAR(5),
    QUESTION_MAX_SCORE NUMBER(5),
    CONSTRAINT question_pk PRIMARY KEY (QUESTION_ID),
    CONSTRAINT part_of FOREIGN KEY (EXAM_ID) REFERENCES EXAM_PAPER (EXAM_ID)
);


CREATE TABLE STUDENT_ANSWER ( -- Each Question has a lot of student_answers from different students
    STUDENT_ID VARCHAR(9) NOT NULL, 
    QUESTION_ID VARCHAR(10) NOT NULL, 
    ANSWER VARCHAR(255),
    ALREADY_MARKED NUMBER(1),
    STUDENT_ANSWER_SCORE NUMBER(5),
    CONSTRAINT answer_by_student_for_question PRIMARY KEY (STUDENT_ID, QUESTION_ID)
);

-- CREATE TABLE STUDENT_SUBJECT_OVERALL_GRADE ( -- Overall grade for a student's subject is calculated by averaging all of his/her exams in that subject.
--     STUDENT_ID VARCHAR(9) NOT NULL, 
--     SUBJECT_ID VARCHAR(12),
--     LETTER_GRADES VARCHAR(2)
-- );   


-- DELETED OLD TABLES
-- BATCH replaced with STUDENT_ANSWER with more attributes
-- CREATE TABLE EXAMPAPER_QUESTIONS (EXAM_ID VARCHAR(16) NOT NULL, QUESTION_ID VARCHAR(10) NOT NULL);
-- CREATE TABLE STUDENT_EXAM_SCORE (STUDENT_ID VARCHAR(9) NOT NULL, EXAM_ID VARCHAR(16), EXAM_SCORE NUMBER(5), TEACHER_FINISHED_GRADING NUMBER(1)); 

--ADD DATA TO STUDENT--

INSERT INTO STUDENT VALUES('17616265d', 'JOHN', 'COMP2411_TUT001', '17616265d@mail.com', 'pwd001');
INSERT INTO STUDENT VALUES('17354345d', 'EDWARD', 'COMP2411_TUT002', '17354345d@mail.com', 'pwd002');
INSERT INTO STUDENT VALUES('17512512d', 'OLIVIA', 'COMP3235_LEC001', '17512512d@mail.com', 'pwd003');
INSERT INTO STUDENT VALUES('17512513d', 'MANDY', 'COMP3235_LEC002', '17512513d@mail.com', 'pwd004');
INSERT INTO STUDENT VALUES('17523233d', 'ANDY', 'COMP3225_LEC002', '17523233d@mail.com', 'pwd004');
INSERT INTO STUDENT VALUES('17512413d', 'TOM', 'COMP3235_LEC002', '17512413d@mail.com', 'pwd004');
INSERT INTO STUDENT VALUES('17522513d', 'TOMMY', 'COMP3235_LEC002', '17522513d@mail.com', 'pwd004');
INSERT INTO STUDENT VALUES('12512513d', 'ANDY', 'COMP2011_LEC001', '12512513d@mail.com', 'pwd034');

--ADD DATA TO TEACHER--

INSERT INTO TEACHER VALUES('16213218x', 'DENNIS', '16213218x@mail.com', 'pwd004');
INSERT INTO TEACHER VALUES('18545424x', 'VINCENT', '18545424x@mail.com', 'pwd005');
INSERT INTO TEACHER VALUES('17542544x', 'TIM', '17542544x@mail.com', 'pwd006');
INSERT INTO TEACHER VALUES('17542554x', 'TOM', '17542554x@mail.com', 'pwd007');
INSERT INTO TEACHER VALUES('19642544x', 'ALEX', '19642544x@mail.com', 'pwd008');

--ADD DATA TO SUBJECT--

INSERT INTO SUBJECT VALUES('COMP2411', 'DATABASE MANAGEMENT');
INSERT INTO SUBJECT VALUES('COMP3235', 'SOFTWARE PROJECT MANAGEMENT');
INSERT INTO SUBJECT VALUES('COMP3225', 'SOFTWARE PROJECT MANAGEMENT ELSE?');
INSERT INTO SUBJECT VALUES('COMP2011', 'DATA STRUCTURES');
INSERT INTO SUBJECT VALUES('COMP0001', 'EMPTY COURSE');

--ADD DATA TO QUESTION--

INSERT INTO QUESTION(QUESTION_ID, TYPE, CONTENT, CORRECT_ANS, SCORE, ISCOMPULSORY) VALUES('1', 'MC', '1+1=?\nA.2\nB.3\nC.11\nD.10', 'A', 2, 'TRUE');
INSERT INTO QUESTION(QUESTION_ID, TYPE, CONTENT, CORRECT_ANS, SCORE, ISCOMPULSORY) VALUES('2', 'FITB', 'He __ a boy.', 'is', 2, 'TRUE');
INSERT INTO QUESTION(QUESTION_ID, TYPE, CONTENT, CORRECT_ANS, SCORE, ISCOMPULSORY) VALUES('3', 'SFTQ', 'What is dbms?', 'dbms', 2, 'TRUE');
INSERT INTO QUESTION(QUESTION_ID, TYPE, CONTENT, CORRECT_ANS, SCORE, ISCOMPULSORY) VALUES('4', 'MC', '2+1=?\nA.2\nB.3\nC.11\nD.10', 'B', 2, 'FALSE');
INSERT INTO QUESTION(QUESTION_ID, TYPE, CONTENT, CORRECT_ANS, SCORE, ISCOMPULSORY) VALUES('5', 'FITB', 'She __ a girl.', 'is', 2, 'FALSE');
INSERT INTO QUESTION(QUESTION_ID, TYPE, CONTENT, CORRECT_ANS, SCORE, ISCOMPULSORY) VALUES('6', 'SFTQ', 'What is comm?', 'comm', 2, 'FALSE');
INSERT INTO QUESTION(QUESTION_ID, TYPE, CONTENT, CORRECT_ANS, SCORE, ISCOMPULSORY) VALUES('7', 'SFTQ', 'Empty?', 'not', 3, 'TRUE');

--ADD DATA TO EXAM_PAPER--

INSERT INTO EXAM_PAPER VALUES('COMP2411_EXAM001', 'COMP2411', '2020-12-12', '15:00', '02:00', 100);
INSERT INTO EXAM_PAPER VALUES('COMP3225_EXAM001', 'COMP3225', '2020-12-13', '12:00', '02:00', 100);
INSERT INTO EXAM_PAPER VALUES('COMP2011_EXAM001', 'COMP2011', '2020-12-10', '12:25', '02:00', 100);

--ADD DATA TO STUDENT_EXAM_GRADES--

INSERT INTO STUDENT_EXAM_SCORE VALUES('17616265d', 'COMP2411_EXAM001', 0, 0);
INSERT INTO STUDENT_EXAM_SCORE VALUES('17354345d', 'COMP2411_EXAM001', 0, 0);
INSERT INTO STUDENT_EXAM_SCORE VALUES('17512512d', 'COMP3235_EXAM001', 0, 0);
INSERT INTO STUDENT_EXAM_SCORE VALUES('17512513d', 'COMP3235_EXAM001', 0, 0);
INSERT INTO STUDENT_EXAM_SCORE VALUES('17523233d', 'COMP3235_EXAM001', 0, 0);
INSERT INTO STUDENT_EXAM_SCORE VALUES('17522513d', 'COMP3235_EXAM001', 0, 0);
INSERT INTO STUDENT_EXAM_SCORE VALUES('17512413d', 'COMP3235_EXAM001', 0, 0);
INSERT INTO STUDENT_EXAM_SCORE VALUES('12512513d', 'COMP2011_EXAM001', 0, 0);

--ADD DATA TO STUDENT_SUBJECT_REPORT--
INSERT INTO STUDENT_SUBJECT_REPORT VALUES('17616265d', 'COMP2411', '');
INSERT INTO STUDENT_SUBJECT_REPORT VALUES('17354345d', 'COMP2411', '');
INSERT INTO STUDENT_SUBJECT_REPORT VALUES('17512512d', 'COMP3235', '');
INSERT INTO STUDENT_SUBJECT_REPORT VALUES('17512513d', 'COMP3235', '');
INSERT INTO STUDENT_SUBJECT_REPORT VALUES('17523233d', 'COMP3235', '');
INSERT INTO STUDENT_SUBJECT_REPORT VALUES('17522513d', 'COMP3235', '');
INSERT INTO STUDENT_SUBJECT_REPORT VALUES('17512413d', 'COMP3235', '');
INSERT INTO STUDENT_SUBJECT_REPORT VALUES('12512513d', 'COMP2011', '');


--ADD DATA TO CLASS--

INSERT INTO CLASS VALUES('COMP2411_TUT001', 'DATABASE MANAGEMENT TUTORIAL CLASS001', 'COMP2411', '16213218x');
INSERT INTO CLASS VALUES('COMP2411_TUT002', 'DATABASE MANAGEMENT TUTORIAL CLASS002', 'COMP2411', '18545424x');
INSERT INTO CLASS VALUES('COMP3235_LEC001', 'SOFTWARE PROJECT MANAGEMENT ELSE? LECTURE CLASS001','COMP3235', '17542544x');
INSERT INTO CLASS VALUES('COMP3235_LEC002', 'SOFTWARE PROJECT MANAGEMENT ELSE? LECTURE CLASS002', 'COMP3235', '17542554x');
INSERT INTO CLASS VALUES('COMP2011_LEC001', 'DATA STRUCTURE LECTURE CLASS001', 'COMP2011', '19642544x');

--ADD DATA TO EXAMPAPER_QUESTIONS--

INSERT INTO EXAMPAPER_QUESTIONS VALUES ('COMP2411_EXAM001','1');
INSERT INTO EXAMPAPER_QUESTIONS VALUES ('COMP2411_EXAM001','2');
INSERT INTO EXAMPAPER_QUESTIONS VALUES ('COMP2411_EXAM001','3');
INSERT INTO EXAMPAPER_QUESTIONS VALUES ('COMP3225_EXAM001','1');
INSERT INTO EXAMPAPER_QUESTIONS VALUES ('COMP3225_EXAM001','2');
INSERT INTO EXAMPAPER_QUESTIONS VALUES ('COMP3225_EXAM001','3');
INSERT INTO EXAMPAPER_QUESTIONS VALUES ('COMP3225_EXAM001','4');
INSERT INTO EXAMPAPER_QUESTIONS VALUES ('COMP3225_EXAM001','5');
INSERT INTO EXAMPAPER_QUESTIONS VALUES ('COMP3225_EXAM001','6');
INSERT INTO EXAMPAPER_QUESTIONS VALUES ('COMP2011_EXAM001','1');
INSERT INTO EXAMPAPER_QUESTIONS VALUES ('COMP2011_EXAM001','2');
INSERT INTO EXAMPAPER_QUESTIONS VALUES ('COMP2011_EXAM001','3');

--COMMIT--

COMMIT;

