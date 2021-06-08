package gui_principal;

import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.ResourceBundle;

import client.CEMSClient;
import client.ClientUI;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.control.Label;
import logic.RequestToServer;

public class PrincipalDisplayReporByStudentController implements Initializable {

	@FXML
	private BarChart<?, ?> ExamsHisto;

	@FXML
	private CategoryAxis ca;

	@FXML
	private NumberAxis na;

	@FXML
	private Label StudentIDLabel;

	@FXML
	private Label StudentNameLabel;

	@FXML
	private Label StudentAvgLabel;

	@FXML
	private Label StudentMedianLabel;
	
	HashMap<String, Integer> ExamGrades;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		StudentIDLabel.setText(String.valueOf(PrincipalGetReportsController.Id));
		StudentNameLabel.setText(PrincipalGetReportsController.UserName);
		RequestToServer req = new RequestToServer("getStudentGrades");
		req.setRequestData(PrincipalGetReportsController.Id);
		ClientUI.cems.accept(req);
		ExamGrades = new HashMap<String, Integer>();
		ExamGrades = (HashMap<String, Integer>) CEMSClient.responseFromServer.getResponseData();
		calcAvgAndMedian();
		XYChart.Series chart = new XYChart.Series();
		chart.setName("Score");
		for (String curr : ExamGrades.keySet())
			chart.getData().add(new XYChart.Data(curr, ExamGrades.get(curr)));
		ExamsHisto.getData().add(chart);
	}

	private void calcAvgAndMedian() {
		
		ArrayList<Integer> grades=new ArrayList<Integer>();
		for(Integer curr:ExamGrades.values())
			grades.add(curr);
		Collections.sort(grades);

		if (grades.size() % 2 == 0) {
			int first, second;
			first = grades.size() / 2 - 1;
			second = (grades.size() + 2) / 2 - 1;
			StudentMedianLabel.setText(String.valueOf(((float) grades.get(first) + grades.get(second)) / 2));
		} else
			StudentMedianLabel.setText(String.valueOf((float) grades.get((grades.size() + 1) / 2 - 1)));
		float sum=0;
		for (Integer a : grades)
			sum += a;
		sum /= grades.size();
		StudentAvgLabel.setText(new DecimalFormat("##.##").format(sum)); //way to show only 2 digits after the decimal point
	}
	

}