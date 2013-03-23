package hu.edudroid.blackmarkettmit.client.services;

import hu.edudroid.blackmarkettmit.client.NotLoggedInException;
import hu.edudroid.blackmarkettmit.shared.Contact;
import hu.edudroid.blackmarkettmit.shared.RecommandationRequest;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("contactRequest")
public interface ContactRequestService extends RemoteService {
	public List<RecommandationRequest> getRecommandationRequests() throws NotLoggedInException;
	public List<Contact> getContacts() throws NotLoggedInException;
	public List<Contact> newRandomContact() throws NotLoggedInException;
}