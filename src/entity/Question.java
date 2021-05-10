package entity;

import java.io.Serializable;

public class Question implements Serializable{

	private String questionID;
	private String question;
	private String description;
	private String[] answers;
	private int correctAnswerIndex;
	private Profession profession;
	
	
	public Question(String questionID) {
		this.questionID = questionID;
	}
	
	public Question(String questionID, String question, String description, String[] answers, int correctAnswerIndex, Profession profession) {
		super();
		this.questionID = questionID;
		this.question = question;
		this.description = description;
		this.answers = answers;
		this.correctAnswerIndex = correctAnswerIndex;
		this.profession = profession;
	}



	public String getQuestionID() {
		return questionID;
	}

	public String getQuestion() {
		return question;
	}
	public void setQuestion(String question) {
		this.question = question;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String[] getAnswers() {
		return answers;
	}
	public void setAnswers(String[] answers) {
		this.answers = answers;
	}
	public int getCorrectAnswerIndex() {
		return correctAnswerIndex;
	}
	public void setCorrectAnswerIndex(int correctAnswerIndex) {
		this.correctAnswerIndex = correctAnswerIndex;
	}
	public Profession getProfession() {
		return profession;
	}

	public void setProfession(Profession profession) {
		this.profession = profession;
	}
	
	
}