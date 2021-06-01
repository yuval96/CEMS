package gui_principal;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;
import client.ClientUI;
import entity.ExtensionRequest;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import logic.RequestToServer;

/**
 * @author Matar
 *
 */

public class ApprovalTimeExtentionController extends PrincipalController implements Initializable {

	@FXML
	private ComboBox<String> selectExamExtension;

	@FXML
	private Label lblAdditionalTime;

	@FXML
	private TextArea textReasonField;

	@FXML
	private Button btnDecline;

	@FXML
	private Button btnApprove;

	private static HashMap<String, ExtensionRequest> extensionRequestMap = new HashMap<String, ExtensionRequest>();
    private static ArrayList<ExtensionRequest> extensionRequestList = new ArrayList<ExtensionRequest>();
    private ArrayList<String> examIdList = new ArrayList<String>();
	private ExtensionRequest selectedExtensionRequest;
	private int timeOfExam;
	
	/** 
	 * @param event that occurs when clicking on 'Approve' button
	 * @throws IOException if failed.
	 */
	@FXML
	void btnApprove(ActionEvent event) {
		//When no test is selected
		if (selectedExtensionRequest == null) {
			popUp("Please choose a exam extension.");
		}
		//When a test is selected
		else {
			//Adding the time required for the test time
			timeOfExam = selectedExtensionRequest.getActiveExam().getExam().getTimeOfExam();
			timeOfExam += Integer.parseInt(selectedExtensionRequest.getAdditionalTime());
			selectedExtensionRequest.getActiveExam().getExam().setTimeOfExam(timeOfExam);
			RequestToServer req = new RequestToServer("approvTimeExtention");
			req.setRequestData(selectedExtensionRequest);
			ClientUI.cems.accept(req);
		}
	}

	/**
	 * @param event that occurs when clicking on 'Decline' button
	 * @throws IOException if failed.
	 */
	@FXML
	void btnDecline(ActionEvent event) {
		//When no test is selected
		if (selectedExtensionRequest == null) {
			popUp("Please choose a exam extension.");
		}
		//When a test is selected
		else {
			RequestToServer req = new RequestToServer("declineTimeExtention");
			req.setRequestData(selectedExtensionRequest);
			ClientUI.cems.accept(req);
		}
	}

	/**
	 * @param event that occurs when clicking on 'selectExamExtension' ComboBox
	 * @throws IOException if failed.
	 */
	@FXML
	void selectExamExtension(ActionEvent event) {
		if (extensionRequestMap.containsKey(selectExamExtension.getValue())) {
			selectedExtensionRequest = extensionRequestMap.get(selectExamExtension.getValue());
			lblAdditionalTime.setText(selectedExtensionRequest.getAdditionalTime());
			textReasonField.setText(selectedExtensionRequest.getReason());
		}
	}
	public void loadExamExtensionsToCombobox() {
		setExtensionRequestMap(extensionRequestList);
		 for (ExtensionRequest ex : extensionRequestList) 
			 examIdList.add(ex.getActiveExam().getExam().getExamID());
		 selectExamExtension.setItems(FXCollections.observableList(examIdList));
			
			//selectExamExtension.setItems(extensionRequestList.get(i).getActiveExam().getExam().getExamID());
		//	selectExamExtension.setId(extensionRequestList.get(i).getActiveExam().getExamCode() + " ");//hadar
			//selectExamExtension.setItems(FXCollections.observableArrayList(extensionRequestMap.keySet()));//hadar

		selectExamExtension.setDisable(false);//hadar

		//selectExamExtension.setItems((ObservableList<ExtensionRequest>) extensionRequestLIstnew); //hadar
		//selectExamExtension.setId(extensionRequestLIstnew.get(0).getActiveExam().getExamCode() + " ");//hadar
		//selectExamExtension.setItems(FXCollections.observableArrayList(extensionRequestMap.keySet()));//hadar
		//selectExamExtension.setDisable(false);//hadar
	
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		selectedExtensionRequest = null;
		loadExamExtensionsToCombobox(); 
		lblAdditionalTime.setText("");
		textReasonField.setText("");
	}

	public static void setExtensionRequestMap(ArrayList<ExtensionRequest> extensionRequestList) {
		 for (ExtensionRequest ex : extensionRequestList) {
			extensionRequestMap.put(ex.getActiveExam().getExam().getExamID(), ex);
		}
	}

	/**
	 * this method create a popup with a message.
	 * 
	 * @param str
	 */
	public void popUp(String str) {
		final Stage dialog = new Stage();
		VBox dialogVbox = new VBox(20);
		Label lbl = new Label(str);
		lbl.setPadding(new Insets(5));
		lbl.setAlignment(Pos.CENTER);
		lbl.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		dialogVbox.getChildren().add(lbl);
		Scene dialogScene = new Scene(dialogVbox, lbl.getMinWidth(), lbl.getMinHeight());
		dialog.setScene(dialogScene);
		dialog.show();
	}

	public static void setExtensionRequestList(ArrayList<ExtensionRequest> extensionRequest) {
		extensionRequestList = extensionRequest;
	}

}
