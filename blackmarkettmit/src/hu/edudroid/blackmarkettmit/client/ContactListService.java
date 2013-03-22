package hu.edudroid.blackmarkettmit.client;
import hu.edudroid.blackmarkettmit.shared.Contact;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("stock")
public interface ContactListService extends RemoteService {
  public void requestRandomContact() throws NotLoggedInException;
  public Contact[] getPlayerIds() throws NotLoggedInException;
}