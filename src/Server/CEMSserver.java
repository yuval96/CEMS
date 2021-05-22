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
		ResponseFromServer respon = null;

		switch (req.getRequestType()) {

		case "getUser": {
			// logic of login
			User user = (User) req.getRequestData();
			User userInSystem = null;
			respon = dbController.verifyLoginUser((User) user);

			userInSystem = (User) respon.getResponseData();
			boolean exist = loggedInUsers.containsValue(userInSystem.getId()); // check if hashMap contains this user id
			// in case this user exist in 'loggedInUsers' update isLogged to 1.
			if (exist) {
				user.setLogged(1); // set isLogged to 1.
			}
			try {
				client.sendToClient(userInSystem);
			} catch (IOException e) {
				e.printStackTrace();
			}

			// serverFrame.printToTextArea(??.toString());
		}

			break;
		case "UpdateUserLoggedIn": {
			// logic of login- update logged status after successfully LOGIN action.
			User user = (User) req.getRequestData();
			user.setLogged(1); // set isLogged to 1.
			loggedInUsers.put(user.getId(), user); // add new user to hashMap of all the logged users.

		}
			break;

		case "UpdateUserLoggedOut": {
			// logic of login- update logged status after LOGOUT action.
			User user = (User) req.getRequestData();
			user.setLogged(0); // set isLogged to 0. (disconnected)
			loggedInUsers.put(user.getId(), user); // remove this user from hashMap of all the logged users.
		}
			break;

		case "getStudentrData_Login": {
			// logic of login- gets student`s courses after successfully LOGIN action.
			Student student = (Student) req.getRequestData();
			student = dbController.getStudentData_Logged(student);
			student = dbController.getStudentCourses_Logged(student);
			// create ResponseFromServer (to client) with all student data.
			ResponseFromServer respond = new ResponseFromServer("STUDENT DATA");
			respond.setResponseData(student);
			// sent to client.
			try {
				client.sendToClient(student);
			} catch (IOException e) {
				e.printStackTrace();
			}

			// serverFrame.printToTextArea(??.toString());
		}
			break;

		case "getTeacherData_Login": {
			// logic of login- gets teacher`s profession after successfully login action.
			Teacher teacher = (Teacher) req.getRequestData();
			teacher = dbController.getTeacherProfession_Logged(teacher);
			// create ResponseFromServer (to client) with all student data.
			ResponseFromServer respond = new ResponseFromServer("TEACHER DATA");
			respond.setResponseData(teacher);
			// sent to client.
			try {
				client.sendToClient(teacher);
			} catch (IOException e) {
				e.printStackTrace();
			}

			// serverFrame.printToTextArea(??.toString());
		}
			break;

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

		case "isActiveExamExist": {
			// logic for 'EnterToExam'
			// verify if activeExam exist at this date, set ActiveExam object if found.
			ActiveExam activeExam = (ActiveExam) req.getRequestData();
			activeExam = dbController.verifyActiveExam_byDate_and_Code((ActiveExam) activeExam);
			// sent to client.
			try {
				client.sendToClient(activeExam);
			} catch (IOException e) {
				e.printStackTrace();
			}
			// serverFrame.printToTextArea(??.toString());
		}
			break;

		case "approvTimeExtention": {
			// update time alloted for test in active exam after the principal approves the
			// request.
			ExtensionRequest extensionRequest = (ExtensionRequest) req.getRequestData();
			dbController.setTimeForActiveTest(extensionRequest.getActiveExam(), extensionRequest.getAdditionalTime());
			dbController.DeleteExtenxtionRequest(extensionRequest.getActiveExam());
		}
			break;
		case "declineTimeExtention": {
			ExtensionRequest extensionRequest = (ExtensionRequest) req.getRequestData();
			dbController.DeleteExtenxtionRequest(extensionRequest.getActiveExam());
		}
			break;

		case "getStudentsByExamID": {
			try {
				ResponseFromServer Res = new ResponseFromServer(null);

				client.sendToClient(dbController.SetDetailsForScoreApprovel((String) req.getRequestData()));
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		
        case "EditExam":{ 
			
			dbController.editExam((Exam) req.getRequestData());
		}
		    break;


		}

	}

	/*------------------------------------Private Methods-------------------------------------------------*/

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

	/**
	 * TODO: add comment
	 * 
	 * @param examData
	 */
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