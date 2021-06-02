// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 
package Server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import entity.ActiveExam;
import entity.Exam;
import entity.ExtensionRequest;
import entity.Profession;
import entity.Question;
import entity.Student;
import entity.Teacher;
import entity.UpdateScoreRequest;
import entity.User;
import gui_server.ServerFrameController;
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
		loggedInUsers = new HashMap<Integer, User>();
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
			getUser((User) req.getRequestData(), client);

		}

			break;
		case "UpdateUserLoggedIn": {
			UpdateUserLoggedIn((User) req.getRequestData(), client);

		}
			break;

		case "UpdateUserLoggedOut": {
			UpdateUserLoggedOut((User) req.getRequestData(), client);

		}
			break;

		case "getStudentData_Login": {
			getStudentData_Login((Student) req.getRequestData(), client);

		}
			break;

		case "getTeacherData_Login": {
			getTeacherData_Login((Teacher) req.getRequestData(), client);
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
			addTimeToExam((ActiveExam) req.getRequestData(), client);
		}
			break;

		case "createNewExtensionRequest": {
			createNewExtensionRequest((ExtensionRequest) req.getRequestData(), client);
		}
			break;

		case "isActiveExamExist": {
			isActiveExamExist((ActiveExam) req.getRequestData(), client);

		}
			break;

		case "getAllActiveExamBeforEnter2Exam": {
			getAllActiveExamBeforEnter2Exam(client);
		}
			break;

		case "approvalTimeExtention": {
			approvalTimeExtention((ActiveExam)req.getRequestData(), client);
		}
			break;
		case "declineTimeExtention": {
			ExtensionRequest extensionRequest = (ExtensionRequest) req.getRequestData();
			//dbController.DeleteExtenxtionRequest(extensionRequest.getActiveExam());
		}
			break;

		case "getStudentsByExamID": {
			try {
				ResponseFromServer Res = new ResponseFromServer("SCORE APPROVAL");
				Res.setResponseData(dbController.SetDetailsForScoreApprovel((String) req.getRequestData()));
				client.sendToClient(Res);
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		break;

		case "getQuestions": {

			try {

				ResponseFromServer respond = new ResponseFromServer("TEACHER QUESTIONS");
				respond.setResponseData(dbController.GetTeacherQuestions((Integer) req.getRequestData()));

				client.sendToClient(respond);

			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		case "getEditExamData": {
			// TODO: new Exam object Exam exam=null;
			// TODO: prepared the array list of question of this exam & HashMap and
			// others....
			dbController.editExam((Exam) req.getRequestData());
			// exam= dbController.editExam(exam);
			// exam= dbController.funName(exam);
			// each one ot then returns an object of exam and give to the other until we
			// have everything for the counstr..

			// TODO: create a ResponseFromServer
			// TODO: after Exam obj ready need to client.sendToClient(....)

		}
			break;

		case "SaveEditExam": {
			// TODO: "UPDATE #### FROM exam blabla...;"
		}
			break;

		case "ClientDisconected": {
			clientDisconected(req.getRequestData(), client);

		}
			break;

		case "getExams": {

			try {

				ResponseFromServer respond = new ResponseFromServer("TEACHER EXAMS");
				respond.setResponseData(dbController.GetTeacherExams((Integer) req.getRequestData()));

				client.sendToClient(respond);

			} catch (IOException e) {
				e.printStackTrace();
			}

		}
			break;
		case "DeleteQuestion": {
			try {

				ResponseFromServer respond = new ResponseFromServer("DELETE TEACHER QUESTION");
				if (dbController.DeleteQuestion((Question) req.getRequestData()))
					respond.setResponseData("TRUE");
				else
					respond.setResponseData("FALSE");

				client.sendToClient(respond);

			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		case "DeleteExam": {
			try {

				ResponseFromServer respond = new ResponseFromServer("DELETE TEACHER EXAM");
				if (dbController.DeleteExam((Exam) req.getRequestData()))
					respond.setResponseData("TRUE");
				else
					respond.setResponseData("FALSE");

				client.sendToClient(respond);

			} catch (IOException e) {
				e.printStackTrace();
			}

		}
			break;
			
		case "getExtensionRequests": {
			getExtensionRequests(client);

		}
			break;
			
			
			
		case "Update Grade":{
			try {

				ResponseFromServer respond = new ResponseFromServer("TEACHER SCORE UPDATE");
				respond.setResponseData(dbController.UpdateScoreOfStudent((UpdateScoreRequest) req.getRequestData()));

				client.sendToClient(respond);

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		break;
		
		}

	}

	/*------------------------------------Private Methods-------------------------------------------------*/

	/**
	 * Verify if activeExam exist at this time, set ActiveExam object if found.
	 * 
	 * @param activeExam
	 * @param client
	 */
	private void isActiveExamExist(ActiveExam activeExam, ConnectionToClient client) {
		// logic for 'EnterToExam'
		ResponseFromServer response = null;
		response = dbController.verifyActiveExam_byDate_and_Code(activeExam);
		// sent to client.
		try {
			client.sendToClient(response);
		} catch (IOException e) {
			e.printStackTrace();
		}
		printMessageInLogFramServer("Message to Client:", response);

	}
	
	
	private void getAllActiveExamBeforEnter2Exam(ConnectionToClient client) {
		// logic for 'EnterToExam'
		ResponseFromServer response = null;
		response = dbController.getAllActiveExam();
		// sent to client.
		try {
			client.sendToClient(response);
		} catch (IOException e) {
			e.printStackTrace();
		}
		printMessageInLogFramServer("Message to Client:", response);
		
	}

	/**
	 * @param requestData
	 * @param client
	 */
	private void clientDisconected(Object requestData, ConnectionToClient client) {
		if (requestData instanceof User) {
			User user = (User) requestData;
			serverFrame.printToTextArea("Client disconnected --->" + user.getUserType() + " logout.");
			UpdateUserLoggedOut(user, client);
		}

		else if (requestData.equals("ClientDisconected_from_login_fram")) {
			serverFrame.printToTextArea("---> Client Disconnected <---");
			ResponseFromServer respon = new ResponseFromServer("CLIENT DISCONECT !");
			try {
				client.sendToClient(respon);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * @param str      is a message which displayed in server`s log.
	 * @param response contains the information that the server transmits to the
	 *                 client.
	 */
	private void printMessageInLogFramServer(String str, ResponseFromServer response) {
		serverFrame.printToTextArea("--->" + str + " " + response.toString());
	}

	//
	/**
	 * @param user   is who server needs to identify the details.
	 * @param client
	 */
	private void getUser(User user, ConnectionToClient client) {
		// logic of login
		User userInSystem = null;
		ResponseFromServer respon = dbController.verifyLoginUser(user);

		userInSystem = (User) respon.getResponseData();
		boolean exist = loggedInUsers.containsValue(userInSystem.getId()); // check if hashMap contains this user id
		// in case this user exist in 'loggedInUsers' update isLogged to 1.
		if (exist) {
			user.setLogged(1); // set isLogged to 1.
		}
		try {
			client.sendToClient(respon);
		} catch (IOException e) {
			e.printStackTrace();
		}
		printMessageInLogFramServer("Message to Client:", respon);// print to server log.

	}

	/**
	 * logic of login: update logged status after successfully LOGIN action.
	 * 
	 * @param user   who server needs to identify and update his details.
	 * @param client
	 */
	private void UpdateUserLoggedIn(User user, ConnectionToClient client) {
		user.setLogged(1); // set isLogged to 1.
		loggedInUsers.put(user.getId(), user); // add new user to hashMap of all the logged users.
		// Response:
		ResponseFromServer response = new ResponseFromServer("USER LOGIN !");
		printLoggedInUsersList(); // for DEBUG
		try {
			client.sendToClient(response);
		} catch (IOException e) {
			e.printStackTrace();
		}
		printMessageInLogFramServer("Message to Client: " + user.toString(), response);// print to server log.

	}

	/**
	 * logic of login: update logged status after LOGOUT action.
	 * 
	 * @param user   who server needs to identify and update his details.
	 * @param client
	 */
	private void UpdateUserLoggedOut(User user, ConnectionToClient client) {
		ResponseFromServer response = new ResponseFromServer("USER LOGOUT");
		loggedInUsers.remove(user.getId()); // remove this user from list.
		printLoggedInUsersList(); // for DEBUG- print current list.

		// sent to client.
		try {
			client.sendToClient(response);
		} catch (IOException e) {
			e.printStackTrace();
		}
		printMessageInLogFramServer("Message to Client: " + user.getUserType(), response);// print to server log.

	}

	/**
	 * logic of login- gets student`s courses after successfully LOGIN action.
	 * 
	 * @param student who server needs to identify and get his all data.
	 * @param client
	 */
	private void getStudentData_Login(Student student, ConnectionToClient client) {
		student = dbController.getStudentData_Logged(student);
		student = dbController.getStudentCourses_Logged(student);
		// create ResponseFromServer (to client) with all student data.
		ResponseFromServer response = new ResponseFromServer("STUDENT DATA");
		response.setResponseData(student);
		// sent to client.
		try {
			client.sendToClient(response);
		} catch (IOException e) {
			e.printStackTrace();
		}
		printMessageInLogFramServer("Message to Client: transferred the ", response);// print to server log.

	}

	/**
	 * @param teacher who server needs to identify and get his all data.
	 * @param client
	 */
	private void getTeacherData_Login(Teacher teacher, ConnectionToClient client) {
		// logic of login- gets teacher`s profession after successfully login action.
		ArrayList<String> professionIds = dbController.getTeacherProfessionIDs(teacher);
		ArrayList<Profession> teacherProfessions = new ArrayList<>();
		for (String id : professionIds) {
			teacherProfessions.add(dbController.getProfessionByID(id));
		}
		teacher.setProfessions(teacherProfessions);
		// create ResponseFromServer (to client) with all student data.
		ResponseFromServer response = new ResponseFromServer("TEACHER DATA");
		response.setResponseData(teacher);
		// sent to client.
		try {
			client.sendToClient(response);
		} catch (IOException e) {
			e.printStackTrace();
		}

		printMessageInLogFramServer("Message to Client: transferred the ", response);// print to server log.

	}

	/**
	 * print all loggedIn users in hashMap list.
	 */
	private void printLoggedInUsersList() {
		// for(loggedInUsers log : )
		for (Integer user : loggedInUsers.keySet()) {
			String key = loggedInUsers.toString();
			System.out.println(key);
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

	private void addTimeToExam(ActiveExam activeExam, ConnectionToClient client) {
		ResponseFromServer respon = dbController.verifyActiveExam((ActiveExam) activeExam);
		try {
			client.sendToClient(respon);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		printMessageInLogFramServer("Message to Client:", respon);// print to server log.
	}

	private void createNewExtensionRequest(ExtensionRequest extensionRequest, ConnectionToClient client) {
		ResponseFromServer respon = dbController.createNewExtensionRequest((ExtensionRequest) extensionRequest);
		try {
			client.sendToClient(respon);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		printMessageInLogFramServer("Message to Client:", respon);// print to server log.
	}
	
	private void getExtensionRequests(ConnectionToClient client) {
		ResponseFromServer respon = new ResponseFromServer("EXTENSION REQUESTS");
		try {	
			respon.setResponseData(dbController.getExtensionsRequests());
			client.sendToClient(respon);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		printMessageInLogFramServer("Message to Client:", respon);// print to server log.		
	}
	
	private void approvalTimeExtention(ActiveExam activeExam, ConnectionToClient client) {
		// update time alloted for test in active exam after the principal approves the
		// request.
		ResponseFromServer respon = new ResponseFromServer("EXTENSION APPROVED");
		try {
			if(dbController.setTimeForActiveTest(activeExam)){//succed
				if(dbController.deleteExtenxtionRequest(activeExam)) 
					respon.setResponseData((Boolean) true);
				else 
					respon.setResponseData((Boolean) false);
			}
			else
				respon.setResponseData((Boolean) false);
			client.sendToClient(respon);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		printMessageInLogFramServer("Message to Client:", respon);// print to server log.		
	}
	
	private void declineTimeExtention(ActiveExam activeExam, ConnectionToClient client) {
		ResponseFromServer respon = new ResponseFromServer("EXTENSION DECLINED");
		try {
			respon.setResponseData(dbController.deleteExtenxtionRequest(activeExam));
			client.sendToClient(respon);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		printMessageInLogFramServer("Message to Client:", respon);// print to server log.	
	}
}