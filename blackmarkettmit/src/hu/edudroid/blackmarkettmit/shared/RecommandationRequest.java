package hu.edudroid.blackmarkettmit.shared;

import java.io.Serializable;
import java.util.Date;

public class RecommandationRequest implements Serializable{

	private static final long serialVersionUID = -5345059499117492529L;
	private String requestKey;
	private String text;
	private Date requestDate;
	private boolean answered;
	
	public String getRequestKey() {
		return requestKey;
	}
	public void setRequestKey(String requestKey) {
		this.requestKey = requestKey;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public Date getRequestDate() {
		return requestDate;
	}
	public void setRequestDate(Date requestDate) {
		this.requestDate = requestDate;
	}
	public boolean isAnswered() {
		return answered;
	}
	public void setAnswered(boolean answered) {
		this.answered = answered;
	}
}
