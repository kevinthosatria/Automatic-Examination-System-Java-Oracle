/*TODO:
	1. Change SQL DONE
	2. When Teacher makes a new exam, insert new STUDENT_EXAM_SCORE records for all students taking that class
	3. While Student is doing the exam, concurrently grade MCQ and FITB questions and update the STUDENT_EXAM_SCORE records -> EXAM_SCORE variable
	4. Complete Teacher's Paper Checking functionality:
		a. Update STUDENT_EXAM_SCORE records -> EXAM_SCORE variable
		b. Set STUDENT_EXAM_SCORE records -> FINISHED_GRADING to 1
	5. Complete Student's Performance Analysis functionality:
		a. Include scores of exams with FINISHED_GRADING == 1
		b. Include "predicted subject grade", which is average of all exams so far
		c. Include comment on what subject to focus improvement on
*/


import java.io.*;
import java.sql.*;
import java.time.ZoneId;
import java.util.*;

import oracle.jdbc.driver.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

import oracle.sql.*;

import javax.annotation.processing.SupportedSourceVersion;

/**
 * Main
 */
public class simpleApplication1
{
    /**
     * @param args main
     * @throws SQLException e
     * @throws IOException e
     */
    public static void main(String args[]) throws SQLException, IOException {
        try {
            String username, password;
            String Useremail;
            username = "\"18087058d\"";            // Your Oracle Account ID
            password = "dogllnff";        // Password of Oracle Account

            // Connection
            DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
            OracleConnection conn =
                    (OracleConnection) DriverManager.getConnection(
                            "jdbc:oracle:thin:@studora.comp.polyu.edu.hk:1521:dbms",
                            username, password);
            Statement stmt = conn.createStatement();

            //login system checking//
            boolean terminate = false;
            boolean login = false;
            String[] currentUser= new String[5];
            String currentUserType = null;
            int loginAttempt = 0;

            ResultSet operation;

            System.out.println("Welcome to the Automated Examination System By Group 41.");
            System.out.println("");

            while (loginAttempt < 4) {
                System.out.println("User: ");
                Scanner scan = new Scanner(System.in);
                String input = scan.nextLine();
                ResultSet loginTeach = stmt.executeQuery("SELECT * FROM TEACHER");

                while (loginTeach.next()) {
                    if (loginTeach.getString(1).equals(input)) {
                        System.out.println("Password: ");
                        scan = new Scanner(System.in);
                        input = scan.nextLine();
                        if ((loginTeach.getString(4)).equals(input)) {
                            currentUser[0] = loginTeach.getString(1);
                            currentUser[1] = loginTeach.getString(2);
                            currentUser[3] = loginTeach.getString(3);
                            currentUser[4] = loginTeach.getString(4);
                            currentUserType = "TEACHER";
                            login = true;
                            break;
                        } else {
                            loginAttempt++;
                        }
                    }
                }
                if (login) {
                    break;
                }
                ResultSet loginStudt = stmt.executeQuery("SELECT * from STUDENT");

                while (loginStudt.next()) {
                    if (loginStudt.getString(1).equals(input)) {
                        System.out.println("Password: ");
                        scan = new Scanner(System.in);
                        input = scan.nextLine();
                        if ((loginStudt.getString(5)).equals(input)) {
                            currentUser[0] = loginStudt.getString(1);
                            currentUser[1] = loginStudt.getString(2);
                            currentUser[2] = loginStudt.getString(3);
                            currentUser[3] = loginStudt.getString(4);
                            currentUser[4] = loginStudt.getString(5);
                            currentUserType = "STUDENT";
                            login = true;
                            break;
                        } else {
                            loginAttempt++;
                        }
                    }
                }
                if (login) {
                    break;
                }
                loginAttempt++;
                loginStudt.close();
                loginTeach.close();
            }


            //START CLI//
            if (login) {
                System.out.println("Welcome " + currentUserType + " " + currentUser[1] + "!");
                while (!terminate) {
                    if (currentUserType.equals("TEACHER")) {
                        System.out.println("Enter a digit for request:\n" +
                                "1. Test Designer\n" + 
                                "2. Paper Checking"
                        );

                        Scanner scan = new Scanner(System.in);
                        String input = scan.nextLine();

                        switch (input) {

                            case "1":
                                // TEST DESIGNER MODE

                                System.out.println(" ");
                                System.out.println("Test Designer:");
                                System.out.print("1. Create Exam\n" +
                                        "2. Exit Program.\n" + "Enter a digit for request: ");
                                scan = new Scanner(System.in);
                                input = scan.nextLine();


                                switch (input) {
                                    // CREATE NEW EXAM MODE

                                    case "1":
                                        // TODO A: Generate new tuple for EXAM table

                                        String[] examSetting = new String[5];   // [EXAM_ID, SUBJECT_ID, DAY, START_TIME, DURATION]

                                        System.out.println("\n Below are subjects taught by this teacher: \n");
                                        System.out.println("\n SUBJECT_ID | SUBJECT_NAME \n");            
                                        
                                        // get list of subjects taught by this teacher
                                        ResultSet op = stmt.executeQuery("SELECT SUBJECT_ID, SUBJECT_NAME FROM SUBJECT WHERE SUBJECT_ID = (SELECT SUBJECT_ID FROM SUBJECT_TEACHER WHERE STAFF_ID='"+currentUser[0]+"')");
                                        
                                        // print list of subjects taught by this teacher
                                        while (op.next()) {
                                            System.out.println(op.getString("SUBJECT_ID") + " | " + op.getString("SUBJECT_NAME"));
                                        }

                                        System.out.print("\n CREATING EXAM: \n Enter the Subject ID to create Exam for: \n");
                                        scan = new Scanner(System.in);
                                        examSetting[1] = scan.nextLine();

                                        System.out.print("Please enter the ExamID in XXXXXXXX_EXAMXXX format: \n");
                                        scan = new Scanner(System.in);
                                        examSetting[0] = scan.nextLine();

                                        System.out.print("Please enter the Exam Date in yyyy-mm-dd format: \n");
                                        scan = new Scanner(System.in);
                                        examSetting[2] = scan.nextLine();

                                        System.out.print("Please enter the Exam start-time in xx:xx format: \n");
                                        scan = new Scanner(System.in);
                                        examSetting[3] = scan.nextLine();

                                        System.out.print("Please enter the Exam time duration in xx:xx format: \n");
                                        scan = new Scanner(System.in);
                                        examSetting[4] = scan.nextLine();

                                        // TODO B: Insert new tuple to EXAM table
                                        stmt.executeUpdate("INSERT INTO EXAM VALUES('" + examSetting[0].toString() + "','" + examSetting[1].toString() + "','" + examSetting[2].toString() + "','"
                                                + examSetting[3].toString() + "','" + examSetting[4].toString()+"', 100)");
                                        
                                        // TODO C: Generate & insert EXAM_PAPER tuples for each student who is taking this subject

                                        // get list of students taking this subject (students can only belong to one class, one class can have many subjects, assume each subject can only belong to one class)
                                        ResultSet students_taking_this_subject = stmt.executeQuery("SELECT STUDENT_ID FROM STUDENT WHERE CLASS_ID = (SELECT CLASS_ID FROM SUBJECT WHERE SUBJECT_ID = '" + examSetting[1].toString() + "')");

                                        // convert ResultSet to array of STUDENT_ID called array_of_students_taking_this_subject
                                        int array_of_students_taking_this_subject_size = 0;
                                        while (students_taking_this_subject.next()){
                                            array_of_students_taking_this_subject_size++;
                                        }
                                        String array_of_students_taking_this_subject[] = new String[array_of_students_taking_this_subject_size+1];
                                        int j = 0;
                                        students_taking_this_subject.close();
                                        ResultSet students_taking_this_subject2 = stmt.executeQuery("SELECT STUDENT_ID FROM STUDENT WHERE CLASS_ID = (SELECT CLASS_ID FROM SUBJECT WHERE SUBJECT_ID = '" + examSetting[1].toString() + "')");
                                

                                        while (students_taking_this_subject2.next()){
                                            array_of_students_taking_this_subject[j] = students_taking_this_subject2.getString(1);
                                            j++;
                                        }

                                        // Insert new EXAM_PAPER tuples for each student to EXAM_PAPER table
                                        // EXAM_PAPER(STUDENT_ID, EXAM_ID, SUBJECT_ID, STUDENT_EXAM_SCORE, TEACHER_FINISHED_GRADING)
                                        for (int x=0; x<array_of_students_taking_this_subject_size; x++){
                                            stmt.executeUpdate("INSERT INTO EXAM_PAPER VALUES('" + array_of_students_taking_this_subject[x] + "', '" + examSetting[0] + "', '" + examSetting[1] + "', 0, 0)");
                                        }  

                                        // TODO D: Update the insertions for new QUESTION table 
                                        // SET QUESTIONS MODE
                                        int maxQuestionID;
                                        boolean setQuestion = true;
                                        while (setQuestion) {
                                            System.out.print("Add new Question? 1=Yes 2=No. \n");
                                            scan = new Scanner(System.in);
                                            input = scan.nextLine();
                                            switch (input) {
                                                case "1":
                                                    op = stmt.executeQuery("SELECT QUESTION_ID FROM " +
                                                            "QUESTION");
                                                    op.next();
                                                    maxQuestionID = op.getRow();
                                                    System.out.println("Select the question type: \n1. mulitple-choice\n" +
                                                            "2. fill in the blank\n3. standard full-length\n");
                                                    scan = new Scanner(System.in);
                                                    String type = scan.nextLine();
                                                    System.out.println("Question is Compulsory? 1=Yes 2=No");
                                                    scan = new Scanner(System.in);
                                                    input = scan.nextLine();
                                                    System.out.println("Enter the question: ");
                                                    scan = new Scanner(System.in);
                                                    String question = scan.nextLine();
                                                    System.out.println("Enter the correct answer: ");
                                                    scan = new Scanner(System.in);
                                                    String corrAns = scan.nextLine();
                                                    System.out.println("Enter the score: ");
                                                    scan = new Scanner(System.in);
                                                    String score = scan.nextLine();
                                                    switch (type) {
                                                        case "1":
                                                            if (input.equals("1")) {
                                                                maxQuestionID++;
                                                                stmt.executeUpdate("INSERT INTO QUESTION (QUESTION_ID, " +
                                                                        "TYPE, CONTENT, CORRECT_ANS, SCORE, " +
                                                                        "ISCOMPULSORY) VALUES ('" + (maxQuestionID) + "', " +
                                                                        "'MC', '" + question + "', '" + corrAns + "', '" + score
                                                                        + "', 'TRUE')");
                                                                stmt.executeUpdate("INSERT INTO EXAMPAPER_QUESTIONS " +
                                                                        "VALUES('" + examSetting[4] + "', '" + maxQuestionID + "')");
                                                            } else if (input.equals("2")) {
                                                                maxQuestionID++;
                                                                stmt.executeUpdate("INSERT INTO QUESTION (QUESTION_ID, " +
                                                                        "TYPE, CONTENT, CORRECT_ANS, SCORE, " +
                                                                        "ISCOMPULSORY) VALUES ('" + (maxQuestionID) + "', " +
                                                                        "'MC', '" + question + "', '" + corrAns + "', '" + score
                                                                        + "', 'FALSE')");
                                                                stmt.executeUpdate("INSERT INTO EXAMPAPER_QUESTIONS " +
                                                                        "VALUES('" + examSetting[4] + "', '" + maxQuestionID + "')");
                                                            } else {
                                                                System.out.println("Invalid Input.");
                                                            }
                                                            break;
                                                        case "2":
                                                            if (input.equals("1")) {
                                                                maxQuestionID = maxQuestionID + 1;
                                                                stmt.executeUpdate("INSERT INTO QUESTION (QUESTION_ID, " +
                                                                        "TYPE, CONTENT, CORRECT_ANS, SCORE, " +
                                                                        "ISCOMPULSORY) VALUES (" + (maxQuestionID) + ", " +
                                                                        "'FITB', '" + question + "', '" + corrAns + "', '" + score
                                                                        + "', 'TRUE')");
                                                                stmt.executeUpdate("INSERT INTO EXAMPAPER_QUESTIONS " +
                                                                        "VALUES('" + examSetting[4] + "', '" + maxQuestionID + "')");
                                                            } else if (input.equals("2")) {
                                                                maxQuestionID = maxQuestionID + 1;
                                                                stmt.executeUpdate("INSERT INTO QUESTION (QUESTION_ID, " +
                                                                        "TYPE, CONTENT, CORRECT_ANS, SCORE, " +
                                                                        "ISCOMPULSORY) VALUES (" + (maxQuestionID) + ", " +
                                                                        "'FITB', '" + question + "', '" + corrAns + "', '" + score
                                                                        +"', 'FALSE')");
                                                                stmt.executeUpdate("INSERT INTO EXAMPAPER_QUESTIONS " +
                                                                        "VALUES('" + examSetting[4] + "', '" + maxQuestionID + "')");
                                                            } else {
                                                                System.out.println("Invalid Input.");
                                                            }
                                                            break;
                                                        case "3":
                                                            if (input.equals("1")) {
                                                                maxQuestionID = maxQuestionID + 1;
                                                                stmt.executeUpdate("INSERT INTO QUESTION (QUESTION_ID, " +
                                                                        "TYPE, CONTENT, CORRECT_ANS, SCORE, " +
                                                                        "ISCOMPULSORY) VALUES ('" + (maxQuestionID) + "', " +
                                                                        "'SFTQ', '" + question + "', '" + corrAns + "', '" + score
                                                                        + "', 'TRUE')");
                                                                stmt.executeUpdate("INSERT INTO EXAMPAPER_QUESTIONS " +
                                                                        "VALUES('" + examSetting[4] + "', '" + maxQuestionID + "')");
                                                            } else if (input.equals("2")) {
                                                                maxQuestionID = maxQuestionID + 1;
                                                                stmt.executeUpdate("INSERT INTO QUESTION (QUESTION_ID, " +
                                                                        "TYPE, CONTENT, CORRECT_ANS, SCORE, " +
                                                                        "ISCOMPULSORY) VALUES (" + (maxQuestionID) + ", " +
                                                                        "'SFTQ', '" + question + "', '" + corrAns + "', '" + score
                                                                        + "', " + ", 'FALSE')");
                                                                stmt.executeUpdate("INSERT INTO EXAMPAPER_QUESTIONS " +
                                                                        "VALUES('" + examSetting[4] + "', '" + maxQuestionID + "')");
                                                            } else {
                                                                System.out.println("Invalid Input.");
                                                            }
                                                            break;
                                                        default:
                                                            break;

                                                    }
                                                    break;

                                                case "2":
                                                    setQuestion = false;
                                                    break;
                                            }   
                                        }


                                    case "2":
                                    // // TODO 2 / TODO E: Update Paper Checking functionality to fit new database design
                                    // // Get list of STUDENTIDs of students who should take this exam FROM STUDENT and CLASS tables using ExamID from above.
                                    // ResultSet students_taking_this_exam = stmt.executeQuery("SELECT STUDENT_ID FROM STUDENT, CLASS WHERE STUDENT.CLASS_ID = CLASS.CLASS_ID AND CLASS.SUBJECT_ID = '" + examSetting[0] + "'");
                                    // // Post new tuples to STUDENT_EXAM_SCORES
                                    // int studentlistsize = 0;
                                    // while (students_taking_this_exam.next()){
                                    //     studentlistsize++;
                                    // }
                                    // String studentlist[] = new String[studentlistsize+1];
                                    // int i = 0;
                                    // students_taking_this_exam.close();
                                    // ResultSet students_taking_this_exam2 = stmt.executeQuery("SELECT STUDENT_ID FROM STUDENT, CLASS WHERE STUDENT.CLASS_ID = CLASS.CLASS_ID AND CLASS.SUBJECT_ID = '" + examSetting[0] + "'");
                                    // // Post new tuples to STUDENT_EXAM_SCORES
                                    // while (students_taking_this_exam2.next()){
                                    //     studentlist[i] = students_taking_this_exam2.getString(1);
                                    //     i++;
                                    // }
                                    // for (int x = 0; x < studentlistsize; x++){
                                    //     stmt.executeUpdate("INSERT INTO STUDENT_EXAM_SCORE VALUES('" + studentlist[x] + "','" + examSetting[4] + "',0,0)");
                                    // }

                                    // students_taking_this_exam2.close();

                                    // System.out.println("\n \n \n \n");
                                    break;

                                    default:
                                        terminate = true;
                                        break;
                                }
                            case "2":
                                break;
                        }
                    } else {
                        // STUDENT MODE

                        List<String> answerList = new ArrayList<>();
                        System.out.println("Exams that are coming up: ");
                        ResultSet Exam = stmt.executeQuery("SELECT * FROM EXAM WHERE SUBJECT_ID IN" +
                                "(SELECT SUBJECT_ID FROM SUBJECT WHERE CLASS_ID=(SELECT CLASS_ID FROM STUDENT WHERE CLASS_ID='" +
                                currentUser[2] + "'))");
                        while (Exam.next()) {
                            System.out.println("ExamID: "+Exam.getString(1));
                            System.out.print("SubjectID: "+Exam.getString(2));
                            System.out.print("\nDate: "+Exam.getString(3));
                            System.out.print(" Start_time: "+Exam.getString(4));
                            System.out.print(" Duration: "+Exam.getString(5));
                            System.out.print("\nMax Score: "+Exam.getString(6));
                        }

                        System.out.println("Enter a digit for request:\n" +
                                "1. Participate to available exam\n " +
                                "2. View Student Performance Analysis \n");

                        Scanner scan = new Scanner(System.in);
                        String input = scan.nextLine();

                        switch (input) {

                            case "1":
                                // PARTICIPTE TO AVAILABLE EXAM
                                int tempscore = 0;
                                Exam.close();
                                Exam = stmt.executeQuery("SELECT * FROM EXAM WHERE SUBJECT_ID IN" +
                                        "(SELECT SUBJECT_ID FROM SUBJECT WHERE SUBJECT.CLASS_ID=(SELECT CLASS_ID FROM " +
                                        "STUDENT WHERE STUDENT.CLASS_ID='" + currentUser[2] + "'))");
                                if (Exam.next()) {
                                    String ExamDate = Exam.getString(3) + " " + Exam.getString(4);
                                    String ExamIDCur = Exam.getString(1);
                                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                                    LocalDateTime currentTime = LocalDateTime.now();
                                    System.out.println(ExamIDCur);
                                    try {
                                        Date date = formatter.parse(ExamDate);
                                        Date now = Date.from(currentTime.atZone(ZoneId.systemDefault()).toInstant());
                                        boolean OK = true;
                                        if (date.compareTo(now) < 0) {
                                            OK = false;
                                        }
                                        ResultSet questions = stmt.executeQuery("SELECT * FROM QUESTION WHERE EXAM_ID = '" + ExamIDCur + "'");
                                        while (OK) {
                                            int i = 1;
                                            while (questions.next()) {
                                                System.out.println("Q" + i++ + ": ");
                                                String[] q = new String[8];
                                                q[0] = questions.getString(1);
                                                q[1] = questions.getString(2);
                                                q[2] = questions.getString(3);
                                                q[3] = questions.getString(4);
                                                q[4] = questions.getString(5);
                                                q[5] = questions.getString(6);
                                                q[6] = questions.getString(7);

                                                int thisMark = questions.getInt("SCORE");
                                                System.out.print(q[3] + " Score: " + thisMark +
                                                        " Compulsory: '" + q[6]);
                                                System.out.println("\nPlease Enter \"NULL\" to skip optional questions\n");
                                                System.out.println("Your Answer: ");
                                                scan = new Scanner(System.in);
                                                String answer = scan.nextLine();
                                                if ((q[1].equals("MC")) || (q[1].equals("FITB"))) {
                                                    if (answer.equals(q[5])) {
                                                        ResultSet updateMark = stmt.executeQuery("SELECT STUDENT_EXAM_SCORE " +
                                                                "FROM EXAM_PAPER WHERE STUDENT_ID = '" + currentUser[0] + "' " +
                                                                "AND EXAM_ID = '" + ExamIDCur + "'");
                                                        updateMark.next();
                                                        int mark = updateMark.getInt(1);
                                                        mark = mark + thisMark;
                                                        stmt.executeUpdate("UPDATE EXAM_PAPER SET STUDENT_EXAM_SCORE" +
                                                                " = " + mark + " WHERE EXAM_ID = '" + ExamIDCur + "'");
                                                    }
                                                } else {
                                                    if (q[1].equals("SFTQ")) {
                                                        stmt.executeUpdate("INSERT INTO STUDENT_ANSWER VALUES ('"
                                                                + currentUser[0] + "','" + q[0] + "','" + answer + "', 0, " + thisMark + ")");
                                                    }
                                                }
                                            }
                                            OK = false;
                                        }
                                    } catch (Exception e) {
                                        System.out.print("Date conversion error");
                                    }
                                }

                                break;

                            case "2":
                                // STUDENT REPORT
                                ResultSet Reports = stmt.executeQuery("SELECT * FROM EXAM_PAPER WHERE STUDENT_ID='"+currentUser[0]+"' AND TEACHER_FINISHED_GRADING=1");
                                while(Reports.next()){
                                    System.out.println("ExamID: "+Reports.getString(3) + " Score:" + Reports.getInt(5));
                                }
                                Reports.close();

                                String tempSub;
                                List<String> Subject = new ArrayList<String>();
                                String lowest = "test";
                                int lowestScore=90000;
                                Reports = stmt.executeQuery("SELECT SUBJECT_ID FROM SUBJECT WHERE " +
                                        "SUBJECT.SUBJECT_ID IN (SELECT EXAM_PAPER.SUBJECT_ID FROM EXAM_PAPER WHERE" +
                                        " STUDENT_ID = '"+currentUser[0]+"')");
                                while(Reports.next()){
                                    tempSub = Reports.getString(1);
                                    if(!Subject.contains(tempSub)){
                                        Subject.add(tempSub);
                                    }
                                }

                                System.out.print("Predicted Subject Grade: ");

                                int n, score;

                                for(int x=0; x<Subject.size(); x++){
                                        Reports = stmt.executeQuery("SELECT AVG(STUDENT_EXAM_SCORE) FROM EXAM_PAPER " +
                                                "WHERE STUDENT_ID='"+currentUser[0]+"' AND TEACHER_FINISHED_GRADING=1 AND SUBJECT_ID='"+Subject.get(x)+"'");
                                        Reports.next();
                                        score = Reports.getInt(1);
                                        n = score;
                                        String grade;
                                        if (n > 94) {
                                            grade = "A+";
                                        } else if (n > 89) {
                                            grade = "A";
                                        } else if (n > 84) {
                                            grade = "A-";
                                        } else if (n > 79) {
                                            grade = "B+";
                                        } else if (n > 74) {
                                            grade = "B";
                                        } else if (n > 69) {
                                            grade = "B-";
                                        } else if (n > 64) {
                                            grade = "C+";
                                        } else if (n > 59) {
                                            grade = "C";
                                        } else if (n > 54) {
                                            grade = "C-";
                                        } else if (n > 49) {
                                            grade = "D+";
                                        } else if (n > 44) {
                                            grade = "D";
                                        } else if (n > 39) {
                                            grade = "D-";
                                        } else {
                                            grade = "F";
                                        }
                                        if(score<lowestScore){
                                            lowestScore = score;
                                            lowest = Subject.get(x);
                                        }
                                        System.out.print(Subject.get(x) + ": " + grade + "\n");

                                }

                                System.out.println("Subject: "+lowest+" needs to be improved most.");
                                break;

                            default:
                                terminate = true;
                                break;
                        }
                    }
                }
            }

            conn.close();
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
}
