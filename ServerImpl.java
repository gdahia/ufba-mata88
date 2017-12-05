import java.util.Hashtable;

public class ServerImpl implements Server {
  private Hashtable<String, String> credentials;

  public ServerImpl() {
    credentials = new Hashtable<String, String>();
  }

  public boolean verifyCredentials(String username, String userCredentials) {
    String creds = credentials.get(username);
    return creds != null && creds.equals(userCredentials);
  }

  public boolean addUser(String username, String userCredentials) {
    if (credentials.get(username) != null)
      return false;
    else {
      credentials.put(username, userCredentials);
      return true;
    }
  }
}
