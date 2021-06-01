package entity;

import java.io.Serializable;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;

//Entity class - define Active Exam in the CEMS system.
public class ActiveExam implements Serializable {

	private Calendar date; // including start time
	private Exam exam;
	private String examCode;
	private int timeAllotedForTest;
	private String activeExamType = null; //{manual / computerized}
	private Time time;
	private Time endTimeToTakeExam;

	public ActiveExam(Exam exam) {
		this.exam = exam;
		timeAllotedForTest = exam.getTimeOfExam();
	}

	public ActiveExam(Calendar date, Exam exam, String examCode) {
		this.date = date;
		this.exam = exam;
		this.examCode = examCode;
		timeAllotedForTest = exam.getTimeOfExam();
	}
	//for old enter to exam
	public ActiveExam(Calendar date, String examCode) {
		this.date = date;
		this.examCode = examCode;
	}
	//for new!!! enter to exam
	public ActiveExam(Time time, Time endTimeToTakeExam, String examCode) { //ok??debug it
		this.time = time;
		this.endTimeToTakeExam = endTimeToTakeExam;
		this.examCode = examCode;
	}
	
	public int getTimeOfExam() {
		return timeAllotedForTest;
	}

	public void setTimeOfExam(String timeOfExam) {
		this.timeAllotedForTest = Integer.parseInt(timeOfExam);
	}

	public Calendar getDate() {
		return date;
	}

	public void setDate(Calendar date) {
		this.date = date;
	}

	public Exam getExam() {
		return exam;
	}

	public void setExam(Exam exam) {
		this.exam = exam;
	}

	public String getExamCode() {
		return examCode;
	}

	public void setExamCode(String examCode) {
		this.examCode = examCode;
	}
	
	public String getActiveExamType() {
		return activeExamType;
	}

	public void setActiveExamType(String activeExamType) {
		this.activeExamType = activeExamType;
	}

	// maybe should be moved to controller????
	// this method calculate the end time by doing: start time + time of exam
	public Calendar getEndTime() {
		Calendar endTime = date.getInstance();
		int hoursOfExam = exam.getTimeOfExam() / 60;
		int minutesOfExam = exam.getTimeOfExam() % 60;
		endTime.add(Calendar.HOUR, hoursOfExam);
		endTime.add(Calendar.MINUTE, minutesOfExam);
		return endTime;
	}
//	//still nor used:
//	public String getActiveExamStartTime() {
//		Calendar startTime = date.getInstance();
//		int startH= date.HOUR;
//		int startM= this.date.MINUTE;
//		startTime.add(startH, 0);
//		startTime.add(startM, 0);
//		SimpleDateFormat sdf = new SimpleDateFormat("h:mm"); //For example 12:08 
//		String formattedTime = sdf.format(startTime);
//		System.out.println(formattedTime);
//		return formattedTime;
//	}
//	//still nor used:
//	public String getActiveExamStartTimeFORMAT_DB() {
//		return date.HOUR_OF_DAY + ":" + date.MINUTE + ":" + date.SECOND;
//	}
	
	public Time getTime() {
		return time;
	}

	public void setTime(Time time) {
		this.time = time;
	}
	
	public Time getEndTimeToTakeExam() {
		return endTimeToTakeExam;
	}

	public void setEndTimeToTakeExam(Time endTimeToTakeExam) {
		this.endTimeToTakeExam = endTimeToTakeExam;
	}
}
