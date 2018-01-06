import java.util.Hashtable;
import java.rmi.RemoteException;

import java.security.KeyPair;
import java.security.Key;
import java.security.PublicKey;

public class ServerImpl implements Server {
  private Hashtable<String, PublicKey> credentials;
  private Hashtable<String, Session> sessions;
  private KeyPair keys;

  public ServerImpl() {
    credentials = new Hashtable<String, PublicKey>();
    sessions = new Hashtable<String, Session>();

    // creates server RSA 2048-bit key pair
    try {
      keys = Crypto.getKeys(null);
    } catch (Exception e) {
      System.err.println("ServerImpl, ServerImpl exception: " + e.toString());
      System.out.println("Unable to generate key-pair");
      System.exit(0);
    }
  }

  public Session getSession(String username, String cryptSignature) {
    try {
      // get corresponding stored user credentials
      PublicKey creds = credentials.get(username);

      // check if user signature is legitimate
      if (creds != null
          && Crypto.verifyEncryptedSignature(cryptSignature, keys.getPrivate(), creds)) {
        System.out.println("User \"" + username + "\" logged in");
        return sessions.get(username);
      } else
        return null;
    } catch (Exception e) {
      System.err.println("ServerImpl, getSession exception: " + e.toString());
      return null;
    }
  }

  public boolean addUser(String username, PublicKey userCredentials) throws RemoteException {
    if (credentials.get(username) != null)
      // do not add repeated users
      return false;
    else {
      System.out.println("User \"" + username + "\" registered");

      // store given credentials
      credentials.put(username, userCredentials);

      // create user session
      sessions.put(username, new SessionImpl(username, this));

      return true;
    }
  }

  public void addChat(Session sess) throws RemoteException {
    // get chat creator username
    String username = sess.getUsername();

    // create new chat
    Chat chat = new ChatImpl(this, username);

    try {
      // add chat to creator session
      sess.addChat(chat);

      System.out.println("User \"" + username + "\" created a new chat");
    } catch (Exception e) {
      System.err.println("ServerImpl, addChat exception: " + e.toString());
    }
  }

  public boolean addUserToChat(String username, Chat chat) {
    // attempt to get user session corresponding to given username
    Session sess = sessions.get(username);

    if (sess == null)
      // do not add nonexistent users to chats
      return false;

    try {
      // add chat to user session
      sess.addChat(chat);

      return true;
    } catch (Exception e) {
      System.err.println("ServerImpl, addUserToChat exception: " + e.toString());
      return false;
    }
  }

  public void removeUser(String username) {
    sessions.remove(username);
    credentials.remove(username);
    System.out.println("User \"" + username + "\" was deleted");
  }

  public Key getPubKey() {
    return keys.getPublic();
  }
}
