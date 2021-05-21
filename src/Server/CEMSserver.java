// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 
package Server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import entity.ActiveExam;
import entity.Course;
import entity.Exam;
import entity.ExtensionRequest;
import entity.Question;
import entity.Student;
import entity.Teacher;
import entity.User;
import gui_server.ServerFrameController;
import logic.LoggedInUser;
import logic.RequestToServer;
import logic.ResponseFromServer;
import logic.StatusMsg;
import ocsf.server.AbstractServer;
import ocsf.server.ConnectionToClient;

public class CEMSserver extends AbstractServer {
	// Class variables *************************************************

	/**
	 * The default port to listen on.
	 */
	// final public static int DEFAULT_PORT = 5555;

	// Constructors ****************************************************

	/**
	 * Constructs an instance of the echo server.
	 *
	 * @param port The port number to connect on.
	 * 
	 */
	private static DBController dbController = new DBController();
	private ServerFrameController serverFrame;
	private HashMap<Integer, User> loggedInUsers;

	public CEMSserver(int port, ServerFrameController serverUI) {
		super(port);
		this.serverFrame = serverUI;
	}

	// Instance methods ************************************************

	/**
	 * This method handles any messages received from the client.
	 *
	 * @param msg    The message received from the client.
	 * @param client The connection from which the message originated.
	 * @param
	 */

	@SuppressWarnings("null")
	public void handleMessageFromClient(Object msg, ConnectionToClient client) {
		StatusMsg status = new StatusMsg();

		serverFrame.printToTextArea("Message received: " + msg + " from " + client);

		RequestToServer req = (RequestToServer) msg;
		ResponseFromServer respon=null;

		switch (req.getRequestType()) {
		
		case "getUser": {
			// logic of login
			User user = (User) req.getRequestData();
			User userInSystem = null;
			respon=dbController.verifyLoginUser((User) user);
			
			userInSystem= (User)respon.getResponseData();
			boolean exist= loggedInUsers.containsValue(userInSystem.getId()); //check if hashMap contains this user id
			//in case this user exist in 'loggedInUsers' update isLogged to 1.
			if(exist) {
				user.setLogged(1); //set isLogged to 1.
			}

			// TODO:[V] �� ����� ���� ����� ����� �� ������� ������� �� ������:isLogged = 1
			try {
				client.sendToClient(userInSystem);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

			break;
		case "UpdateUserLoggedIn": {
			// logic of login- update logged status after Successfully login action
			
			User user = (User) req.getRequestData();
			user.setLogged(1); //set isLogged to 1.			
			loggedInUsers.put(user.getId(), user); //add new user to hashMap of all the logged users.
			//TODO:[V] loggedInUsers.add(userID, user)
			//dbController.setLoginUserLogged(user.getId(), user.isLogged());
			//�����: ������ �� ����� �� ���� ��� �DB �����??
			// ����� ���� 100 �����?
				
			
			// serverFrame.printToTextArea(??.toString());
		}
			break;
		case "UpdateUserLoggedOut": {
			User user = (User) req.getRequestData();
			//TODO:[V] loggedInUsers.remove(userID, user)
			user.setLogged(0); //set isLogged to 0. (disconnected)			
			loggedInUsers.put(user.getId(), user); //remove this user from hashMap of all the logged users.
		}
		break;
		
		case "getUserData_Login":{
			
			if( req.getRequestData() instanceof Student) {
				Student student = (Student) req.getRequestData();
				dbController.getStudentData_Logged(student);
			}
			
			if( req.getRequestData() instanceof Teacher) {		
			Teacher teacher = (Teacher) req.getRequestData();
			dbController.getTeacherData_Logged(teacher);
			}
		}
		
		case "getStudentCourses": {
			//TODO: ArrayList<Course> studentCourses = dbController.getStudentCourses(int userID);
			// send to client(studentCourses)
		}
		case "createNewQuestion": {
			createNewQuestion((Question) req.getRequestData());
		}
			break;

		case "createNewExam": {
			createNewExam((Exam) req.getRequestData());
		}
			break;

		case "addTimeToExam": {
			ActiveExam activeExam = (ActiveExam) req.getRequestData();
			ActiveExam activeExamInSystem = null;
			dbController.verifyActiveExam((ActiveExam) activeExam);			
			try {
				client.sendToClient(activeExamInSystem);
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
			break;
		case "createNewExtensionRequest": {
			dbController.createNewExtensionRequest((ExtensionRequest) req.getRequestData());
		}
			break;		
			
			//TODO: return exam id if exist
		case "isActiveExamExist": {
			//logic for 'EnterToExam'
			// logic of verify if activeExam exist at this date, set ActiveExam object if found.
			ActiveExam activeExam = (ActiveExam) req.getRequestData();
			dbController.verifyActiveExam_byDate_and_Code((ActiveExam) activeExam);	
		}
			break;		
			
		case "approvTimeExtention": {
			// update time alloted for test in active exam after the principal approves the
			// request.	
			ExtensionRequest extensionRequest =  (ExtensionRequest) req.getRequestData();
			dbController.setTimeForActiveTest(extensionRequest.getActiveExam(), extensionRequest.getAdditionalTime());
			dbController.DeleteExtenxtionRequest(extensionRequest.getActiveExam());
		}
			break;
		case "declineTimeExtention": {
			ExtensionRequest extensionRequest = (ExtensionRequest) req.getRequestData();
			dbController.DeleteExtenxtionRequest(extensionRequest.getActiveExam());
		}
			break;

		case "getStudentsByExamID":{
			try {
				
				ResponseFromServer Res=new  ResponseFromServer(null);

				client.sendToClient(dbController.SetDetailsForScoreApprovel((String)req.getRequestData()));
			} catch (IOException e) {
				e.printStackTrace();
			}
			

		}
		
		}
		
		
	}

	/**
	 * @param questionData this method creates the question ID and then inserts the
	 *                     new question into the DB.
	 */
	private void createNewQuestion(Question questionData) {
		int numOfQuestions = dbController.getNumOfQuestionsInProfession(questionData.getProfession().getProfessionID());
		String questionID = String.valueOf(numOfQuestions + 1);
		questionID = ("0000" + questionID).substring(questionID.length());
		questionID = questionData.getProfession().getProfessionID() + questionID;
		questionData.setQuestionID(questionID);
		dbController.createNewQuestion(questionData);
	}

	private void createNewExam(Exam examData) {
		// create the exam ID by number of exams in this profession and course
		int numOfExams = dbController.getNumOfExamsInCourse(examData.getCourse().getCourseID()) + 1;
		String examID = numOfExams < 10 ? "0" + String.valueOf(numOfExams) : String.valueOf(numOfExams);
		examID = examData.getProfession().getProfessionID() + examData.getCourse().getCourseID() + examID;
		examData.setExamID(examID);
		// create the new exam in DB
		dbController.createNewExam(examData);
		// add questions and scores to DB
		dbController.addQuestionsInExam(examID, examData.getQuestionScores());
	}

	/**
	 * This method overrides the one in the superclass. Called when the server
	 * starts listening for connections.
	 */
	protected void serverStarted() {
		serverFrame.printToTextArea("Server listening for connections on port " + getPort());
		dbController.connectDB(serverFrame);
	}

	/**
	 * This method overrides the one in the superclass. Called when the server stops
	 * listening for connections.
	 */
	protected void serverStopped() {
		serverFrame.printToTextArea("Server has stopped listening for connections.");
	}
}