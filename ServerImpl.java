import java.util.Hashtable;
import java.util.Vector;
import java.rmi.RemoteException;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.Key;
import java.util.Base64;
import javax.crypto.Cipher;

public class ServerImpl implements Server {
  private Hashtable<String, String> credentials;
  private Hashtable<String, Session> sessions;
  private Vector<Chat> chats;
  private Key pubKey;
  private Key privKey;

  public ServerImpl() {
    credentials = new Hashtable<String, String>();
    sessions = new Hashtable<String, Session>();
    chats = new Vector<Chat>();

    // creates server RSA 2048-bit key pair
    try {
      KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
      keyPairGenerator.initialize(2048);
      KeyPair keyPair = keyPairGenerator.genKeyPair();
      pubKey = keyPair.getPublic();
      privKey = keyPair.getPrivate();
    } catch (Exception e) {
      System.err.println("ServerImpl, ServerImpl exception: " + e.toString());
      System.out.println("Unable to generate key-pair");
    }
  }

  public Session getSession(String username, String cryptUserCredentials) {
    // decrypt credentials
    String userCredentials = decrypt(cryptUserCredentials);

    // get corresponding stored user credentials
    String creds = credentials.get(username);

    // check if given credentials match stored ones
    if (creds != null && creds.equals(userCredentials)) {
      System.out.println("User \"" + username + "\" logged in");
      return sessions.get(username);
    } else
      return null;
  }

  public boolean addUser(String username, String cryptUserCredentials) throws RemoteException {
    // decrypt credentials
    String userCredentials = decrypt(cryptUserCredentials);

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

  public Key getPubKey() {
    return pubKey;
  }

  private String decrypt(String message) {
    try {
      Cipher cipher = Cipher.getInstance("RSA");
      cipher.init(Cipher.DECRYPT_MODE, privKey);
      return new String(cipher.doFinal(Base64.getDecoder().decode(message)));
    } catch (Exception e) {
      System.err.println("ServerImpl, decrypt exception: " + e.toString());
      return null;
    }
  }
}
