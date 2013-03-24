package hu.edudroid.blackmarkettmit.client.services;

import hu.edudroid.blackmarkettmit.client.NotLoggedInException;
import hu.edudroid.blackmarkettmit.shared.Contact;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("contactRequest")
public interface ContactRequestService extends RemoteService {
	public static final int PLAY_RESULT_DECLINED = -1;
	public static final int PLAY_RESULT_ACCEPTED = 0;
	public static final int PLAY_RESULT_COOPERATE = 1;
	public static final int PLAY_RESULT_HE_DEFECTED = 2;
	public static final int PLAY_RESULT_I_DEFECTED = 3;
	public static final int PLAY_RESULT_BOTH_DEFECTED = 4;
	
	public void askForRecommandation(String otherPlayerId) throws NotLoggedInException;
	public List<Contact> getContacts() throws NotLoggedInException;
	public List<Contact> newRandomContact() throws NotLoggedInException;
	public Integer play(String otherPlayerId, int choice) throws NotLoggedInException;
}