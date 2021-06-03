package gui_teacher;

import java.net.URL;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.ResourceBundle;

import client.CEMSClient;
import client.ClientUI;
import entity.ActiveExam;
import entity.Exam;
import entity.Teacher;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import logic.RequestToServer;

public class CreateActiveExamController implements Initializable {

	@FXML
	private Button btnSaveActiveExam;

	@FXML
	private TextField textExamCode;

	@FXML
	private ComboBox<Time> selectTime;

	@FXML
	private ImageView imgClock;

	@FXML
	private TextField textExamID;

	@FXML
	private TextField textCourse;

	@FXML
	private TextField textProfession;

	// private static TeacherController teacherController; // check if not needed.
	private static ExamBankController examBankController;
	private static Exam exam;
	private Teacher teacher;

	private static HashMap<String, Time> startTimeForNewActiveExamtMap = new HashMap<String, Time>();
	private static ArrayList<Time> startTimeList = new ArrayList<Time>();
	private ArrayList<String> examIdList = new ArrayList<String>();

	// private Date date = null; //we need to add to table the date of the exam
	private Time selectedTime = null;

	@FXML
	void btnSaveActiveExam(ActionEvent event) {
		String examCode = textExamCode.getText().trim();
		// Checks filed conditions
		if (textExamCode.getText().isEmpty()) {
			popUp("Code field is empty");
		} else if (examCode.length() != 4) {
			popUp("Exam code must include 4 characters/ digits");
		} else if (!examCode.matches("[a-zA-Z]+") || !examCode.matches("[0-9]+")) {
			popUp("Exam code must include letters and digits");
		}

		if (selectedTime == null) {
			popUp("Please choose your exam.");
		} else {
			
			
			exam.setAuthor(teacher); // TODO: think with tean id need to delete from DB or not.
			ActiveExam newActiveExam = new ActiveExam(selectedTime, exam, examCode);
			//before we create new active exam, Request from server to check that
			//the same examID at the same time not already exist.
			boolean isAllowed= isActiveExamExist(newActiveExam);

			
			RequestToServer req = new RequestToServer("createNewActiveExam");
			req.setRequestData(newActiveExam);
			ClientUI.cems.accept(req);

			
			
			
			
			
			
			
			
			
		}

	}

	// in order to avoid from create the same Active Exam in the same time!!
	private boolean isActiveExamExist(ActiveExam activeExam) {
		ActiveExam isActiveExamExists = activeExam;
		RequestToServer req = new RequestToServer("CheckIfActiveExamAlreadyExists");
		req.setRequestData(isActiveExamExists);
		ClientUI.cems.accept(req);
		
		if (CEMSClient.responseFromServer.getStatusMsg().getStatus().equals("Create action is allowed")) {
			return true;
		}
		return false;
	}

	@FXML
	void selectTime(ActionEvent event) {

	}

	/**
	 * Receive the selected Exam from previous screen.
	 * 
	 * @param selectedExam
	 */
	public void setActiveExamState(Exam selectedExam) {
		exam = selectedExam;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		teacher = (Teacher) ClientUI.loggedInUser.getUser();
		textExamID.setText(exam.getExamID());
		textCourse.setText(exam.getCourse().getCourseID());
		textProfession.setText(exam.getProfession().getProfessionID());

		// ..
		loadExamStartTimeToCombobox();
	}

	private void loadExamStartTimeToCombobox() {
		// TODO Auto-generated method stub

	}

	/**
	 * create a popUp with a given message.
	 * 
	 * @param msg
	 */
	private void popUp(String msg) {
		final Stage dialog = new Stage();
		VBox dialogVbox = new VBox(20);
		Label lbl = new Label(msg);
		lbl.setPadding(new Insets(15));
		lbl.setAlignment(Pos.CENTER);
		lbl.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		dialogVbox.getChildren().add(lbl);
		Scene dialogScene = new Scene(dialogVbox, lbl.getMinWidth(), lbl.getMinHeight());
		dialog.setScene(dialogScene);
		dialog.show();
	}

}
