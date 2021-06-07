package gui_teacher;

import java.net.URL;
import java.sql.Time;
import java.util.ResourceBundle;

import client.CEMSClient;
import client.ClientUI;
import entity.ActiveExam;
import entity.Exam;
import entity.ExamStatus;
import entity.Teacher;
import gui_cems.GuiCommon;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import logic.RequestToServer;

public class CreateActiveExamController extends GuiCommon implements Initializable {

	@FXML
	private Button btnSaveActiveExam;

	@FXML
	private TextField textExamCode;

	@FXML
	private ComboBox<String> selectTime;

	@FXML
	private ImageView imgClock;

	@FXML
	private TextField textExamID;

	@FXML
	private TextField textCourse;

	@FXML
	private TextField textProfession;

	@FXML
	private RadioButton selectManual;

	@FXML
	private RadioButton selectComputerized;

	private static Exam exam;
	private static String activeExamType;
	private String[] startTimeArr = { "08:00:00", "08:30:00", "09:00:00", "09:30:00", "10:00:00", "10:30:00",
			"11:00:00", "11:30:00", "12:00:00", "12:30:00", "13:00:00", "13:30:00", "14:00:00", "14:30:00", "15:00:00",
			"15:30:00", "16:00:00", "16:30:00", "17:00:00", "17:30:00" };

	private static boolean toggleFlag = false;
	// TODO: private Date date = null; //we need to add to table the date of the
	// exam ??
	private Time selectedTime;

	/**
	 * @param event that occurs when the teacher press on create active exam button
	 */
	@FXML
	void btnSaveActiveExam(ActionEvent event) {
		String examCode = textExamCode.getText().trim();

		if (checkConditionToSaveActiveExam(examCode)) {

			// exam.setAuthor(teacher); // TODO: think with team if need to delete from DB

			ActiveExam newActiveExam = new ActiveExam(selectedTime, exam, examCode, activeExamType,
					exam.getTimeOfExam());
			// before we create new active exam, Request from server to check that
			// the same examID at the same time not already exist.
			boolean isAllowed = isActiveExamExist(newActiveExam);

			if (isAllowed) {
				// Request from server to insert new active exam into DB.
				RequestToServer reqCreateExam = new RequestToServer("createNewActiveExam");
				reqCreateExam.setRequestData(newActiveExam);
				ClientUI.cems.accept(reqCreateExam);
				
				if (CEMSClient.responseFromServer.getStatusMsg().getStatus().equals("NEW ACTIVE EXAM CREATED")) {
					// Request from server to update status filed for this exam: [ENUM('active')].
					RequestToServer reqUpdate = new RequestToServer("updateExamStatus");
					newActiveExam.getExam().setExamStatus(ExamStatus.active);
					reqUpdate.setRequestData(newActiveExam);
					ClientUI.cems.accept(reqUpdate); // send back status message.

					if (CEMSClient.responseFromServer.getStatusMsg().getStatus().equals("EXAM STATUS UPDATED")) {
						newActiveExam.getExam().setExamStatus(ExamStatus.active); //update status in entity.
						popUp("New active exam has been successfully created in the system.");

						displayNextScreen((Teacher) ClientUI.loggedInUser.getUser(), "ExamBank.fxml"); 
					}
				}

			} else {
				popUp("This exam: " + newActiveExam.getExam().getExamID()
						+ " already created as active in the same start time.\n This exam can be created as new active only after finished.");

			}

		}

	}

	/**
	 * @param examCode
	 * @return Returns true if all editable and selectable details are correct.
	 *         Otherwise, displays a message and returns a false.
	 */
	private boolean checkConditionToSaveActiveExam(String examCode) {
		boolean selectCompExam = selectComputerized.isSelected();
		boolean selectManualExam = selectManual.isSelected();

		boolean flag = true;
		StringBuilder strBuilder = new StringBuilder();

		strBuilder.append("Error:\n");
		if (examCode.length() != 4 || textExamCode.getText().isEmpty()) {
			strBuilder.append("Exam code must include 4 characters and digits.\n");
			flag = false;
		}
		if (examCode.matches("[a-zA-Z]+") || examCode.matches("[0-9]+")) {
			strBuilder.append("Exam code must include letters and digits.\n");
			flag = false;
		}
		if (selectedTime == null) {
			strBuilder.append("Please choose start time for this exam.\n");
			flag = false;
		}
		if (!selectCompExam && !selectManualExam) {
			strBuilder.append("You need to select exam type.\n");
			flag = false;
		}

		if (!flag) {
			popUp(strBuilder.toString());
		}
		return flag;
	}

	// in order to avoid from create the same Active Exam in the same time!!

	/**
	 * @param activeExam
	 * @return true if active create action this exam as new active exam is allowed
	 */
	private boolean isActiveExamExist(ActiveExam activeExam) {
		RequestToServer req = new RequestToServer("CheckIfActiveExamAlreadyExists");
		req.setRequestData(activeExam);
		ClientUI.cems.accept(req);

		if (CEMSClient.responseFromServer.getStatusMsg().getStatus().equals("CREATE ACTION ALLOWED")) {
			return true;
		}
		return false;
	}

	/**
	 * @param event that occurs when the teacher chooses an exam type.
	 */
	@FXML
	void selectComputerized(MouseEvent event) {
		activeExamType = "computerized";
		selectManual.setDisable(toggleFlagStatus());

	}

	/**
	 * @param event that occurs when the teacher chooses an exam type.
	 */
	@FXML
	void selectManual(MouseEvent event) {
		activeExamType = "manual";
		selectComputerized.setDisable(toggleFlagStatus());

	}

	/**
	 * //Allows you to select a single type of exam
	 * 
	 * @return Returns the opposite value of the boolean variable that was.
	 */
	private boolean toggleFlagStatus() {
		if (toggleFlag == false)
			return toggleFlag = true;
		else
			return toggleFlag = false;
	}

	/**
	 * Receive the selected Exam from previous screen.
	 * 
	 * @param selectedExam
	 */
	public static void setActiveExamState(Exam selectedExam) {
		exam = selectedExam;
	}

	/**
	 * The function initializes the relevant fields at the moment the screen loads.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// teacher = (Teacher) ClientUI.loggedInUser.getUser();
		selectedTime = null;
		// TODO: get proffe..Name and set into examID label teacher.getProfessions()
		// the same for Course.
		textExamID.setText(exam.getExamID());
		textCourse.setText(exam.getCourse().getCourseID());
		textProfession.setText(exam.getProfession().getProfessionID());
		selectTime.setItems(FXCollections.observableArrayList(startTimeArr)); // load time to combo Box.

	}

	/**
	 * @param event that occurs when the teacher chooses start time from combo box.
	 */
	@FXML
	void selectTime(ActionEvent event) {
		selectedTime = java.sql.Time.valueOf(selectTime.getValue());
	}

}
