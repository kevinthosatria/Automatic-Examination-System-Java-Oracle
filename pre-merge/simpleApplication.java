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
		c. Include comment on what subject to focus improvement 
*/


import java.io.*;
import java.sql.*;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import oracle.jdbc.driver.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.time.LocalDateTime;
import oracle.sql.*;

import javax.annotation.processing.SupportedSourceVersion;

/**
 * Main
 */
public class simpleApplication
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


			//start CLI//
			if (login) {
				System.out.println("Welcome " + currentUserType + " " + currentUser[1] + "!");
				while (!terminate) {
					if (currentUserType.equals("TEACHER")) {
						System.out.println("Enter a digit for request:\n" +
								"1. Test Designer\n" + "2. Paper Checking");

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
										// Set SubjectID, Exam Date, Exam Start-Time, Exam Time Duration, ExamID

										String[] examSetting = new String[5];
										System.out.println("");
										System.out.print("List of Subject ID: \n");
										ResultSet op = stmt.executeQuery("SELECT SUBJECT_ID, SUBJECT_NAME FROM SUBJECT");
										while (op.next()) {
											System.out.println(op.getString("SUBJECT_ID") + ". " + op.getString("SUBJECT_NAME"));
										}
										op.close();
										System.out.print("Enter the Subject ID: \n");
										scan = new Scanner(System.in);
										examSetting[0] = scan.nextLine();

										System.out.print("Please enter the Exam Date in yyyy-mm-dd format: \n");
										scan = new Scanner(System.in);
										examSetting[1] = scan.nextLine();

										System.out.print("Please enter the Exam start-time in xx:xx format: \n");
										scan = new Scanner(System.in);
										examSetting[2] = scan.nextLine();

										System.out.print("Please enter the Exam time duration in xx:xx format: \n");
										scan = new Scanner(System.in);
										examSetting[3] = scan.nextLine();

										System.out.print("Please enter the ExamID XXXXXXXX_EXAMXXX format: \n");
										scan = new Scanner(System.in);
										examSetting[4] = scan.nextLine();
										int maxQuestionID;

										stmt.executeUpdate("INSERT INTO EXAM_PAPER VALUES('" + examSetting[4].toString() + "','" + examSetting[0].toString() + "','" + examSetting[1].toString() + "','"
												+ examSetting[2].toString() + "','" + examSetting[3].toString()+"', '100')");




										// SET QUESTIONS MODE
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
										// TODO 2: 
										// Get list of STUDENTIDs of students who should take this exam FROM STUDENT and CLASS tables using ExamID from above.
										ResultSet students_taking_this_exam = stmt.executeQuery("SELECT STUDENT_ID FROM STUDENT, CLASS WHERE STUDENT.CLASS_ID = CLASS.CLASS_ID AND CLASS.SUBJECT_ID = '" + examSetting[0] + "'");
										// Post new tuples to STUDENT_EXAM_SCORES
										int studentlistsize = 0;
										while (students_taking_this_exam.next()){
											studentlistsize++;
										}
										String studentlist[] = new String[studentlistsize+1];
										int i = 0;
										students_taking_this_exam.close();
										ResultSet students_taking_this_exam2 = stmt.executeQuery("SELECT STUDENT_ID FROM STUDENT, CLASS WHERE STUDENT.CLASS_ID = CLASS.CLASS_ID AND CLASS.SUBJECT_ID = '" + examSetting[0] + "'");
										// Post new tuples to STUDENT_EXAM_SCORES
										while (students_taking_this_exam2.next()){
											studentlist[i] = students_taking_this_exam2.getString(1);
											i++;
										}
										for (int x = 0; x < studentlistsize; x++){
											stmt.executeUpdate("INSERT INTO STUDENT_EXAM_SCORE VALUES('" + studentlist[x] + "','" + examSetting[4] + "',0,0)");
										}

										students_taking_this_exam2.close();

										System.out.println("\n \n \n \n");	
										break;

									case "2":


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
						ResultSet Exam = stmt.executeQuery("SELECT * FROM EXAM_PAPER WHERE SUBJECT_ID=" +
								"(SELECT SUBJECT_ID FROM CLASS WHERE CLASS_ID=(SELECT CLASS_ID FROM STUDENT WHERE CLASS_ID='" +
								currentUser[2] + "'))");
						while (Exam.next()) {
							System.out.println(Exam.getString(1));
							System.out.print(" "+Exam.getString(2));
							System.out.print(" "+Exam.getString(3));
							System.out.print(" "+Exam.getString(4));
							System.out.print(" "+Exam.getString(5));
							System.out.print(" "+Exam.getString(6));
						}
						Exam.close();
						System.out.println("Enter a digit for request:\n" +
								"1. Participate to available exam\n " +
								"2. View Student Performance Analysis \n");

						Scanner scan = new Scanner(System.in);
						String input = scan.nextLine();

						switch (input) {

							case "1":
							// PARTICIPTE TO AVAILABLE EXAM
								int tempscore = 0;
								Exam = stmt.executeQuery("SELECT * FROM EXAM_PAPER WHERE SUBJECT_ID=" +
										"(SELECT SUBJECT_ID FROM CLASS WHERE CLASS_ID=(SELECT CLASS_ID FROM STUDENT WHERE CLASS_ID='" +
										currentUser[2] + "'))");
								Exam.next();
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
									ResultSet questions = stmt.executeQuery("SELECT * FROM QUESTION WHERE QUESTION_ID IN(SELECT QUESTION_ID FROM EXAMPAPER_QUESTIONS WHERE EXAM_ID='" + ExamIDCur+"')");
									while (OK) {
										int i = 1;
										while (questions.next()) {
											System.out.println("Q" + i++ + ":");
											String[] q = new String[9]; q[0]=questions.getString(1);q[1]=questions.getString(2);q[2]=questions.getString(3);q[3]=questions.getString(4);q[4]=questions.getString(5);q[5]=questions.getString(6);q[6]=questions.getString(7);q[7]=questions.getString(8);
											System.out.println(q[2] + " Score: " + q[6] +
													" Compulsory: '" + q[7]);
											System.out.println("Please Enter \"NULL\" to skip optional questions");
											System.out.println("Your Answer: ");
											scan = new Scanner(System.in);
											String answer = scan.nextLine();
											if (q[1] != "MC" || q[1] != "FITB") {
												stmt.executeQuery("INSERT INTO BATCH VALUES ('" + currentUser[0]
														+ "', '" + q[0] + "', '" +
														q[2] + "', '" + answer + "')");
											} else {
												if (q[5].equals(answer)) {
													tempscore += questions.getInt(8);
												}
											}
										}
										questions.close();
										stmt.executeQuery("UPDATE STUDENT_REPORT SET TEMP_SCORE = " + tempscore +
												" WHERE STUDENT_ID= '" + currentUser[0]+"'");
										break;
									}
								} catch (Exception e) {
									System.out.print("Date conversion error");
								}

								break;

							case "2":
								// STUDENT REPORT
								ResultSet Reports = stmt.executeQuery("SELECT * FROM STUDENT_REPORT");
								while(Reports.next()){
									System.out.println(Reports.getString(1) + " " + Reports.getString(2) + Reports.getString(3));
								}
								Reports.close();
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