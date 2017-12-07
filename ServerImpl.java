import java.util.Hashtable;
import java.util.Vector;
import java.rmi.RemoteException;

public class ServerImpl implements Server {
  private Hashtable<String, String> credentials;
  private Hashtable<String, Session> sessions;
  private Vector<Chat> chats;

  public ServerImpl() {
    credentials = new Hashtable<String, String>();
    sessions = new Hashtable<String, Session>();
    chats = new Vector<Chat>();
  }

  public Session getSession(String username, String userCredentials) {
    String creds = credentials.get(username);
    if (creds != null && creds.equals(userCredentials))
      return sessions.get(username);
    else
      return null;
  }

  public boolean addUser(String username, String userCredentials) throws RemoteException {
    if (credentials.get(username) != null)
      return false;
    else {
      credentials.put(username, userCredentials);
      sessions.put(username, new SessionImpl(this));
      return true;
    }
  }
}
