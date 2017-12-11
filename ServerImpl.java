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
    if (creds != null && creds.equals(userCredentials)) {
      System.out.println("User \"" + username + "\" logged in.");
      return sessions.get(username);
    } else
      return null;
  }

  public boolean addUser(String username, String userCredentials) throws RemoteException {
    if (credentials.get(username) != null)
      return false;
    else {
      System.out.println("User \"" + username + "\" registered.");
      credentials.put(username, userCredentials);
      sessions.put(username, new SessionImpl(username, this));
      return true;
    }
  }

  public void addChat(Session sess) throws RemoteException {
    String username = sess.getUsername();
    Chat chat = new ChatImpl(this, username);
    try {
      sess.addChat(chat);
      System.out.println("User \"" + username + "\" created a new chat");
    } catch (Exception e) {
      System.err.println("ServerImpl, addChat exception: " + e.toString());
    }
  }

  public boolean addUserToChat(String username, Chat chat) {
    Session sess = sessions.get(username);
    if (sess == null)
      return false;
    try {
      sess.addChat(chat);
      return true;
    } catch (Exception e) {
      System.err.println("ServerImpl, addUserToChat exception: " + e.toString());
      return false;
    }
  }
}
