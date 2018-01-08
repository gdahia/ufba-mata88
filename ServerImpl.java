import java.util.Hashtable;
import java.rmi.RemoteException;

import java.security.KeyPair;
import java.security.Key;
import java.security.PublicKey;

public class ServerImpl implements Server {
  private Hashtable<String, PublicKey> userKeys;
  private Hashtable<String, String> verificationCodes;
  private Hashtable<String, Session> sessions;
  private KeyPair keys;

  public ServerImpl() {
    userKeys = new Hashtable<String, PublicKey>();
    verificationCodes = new Hashtable<String, String>();
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

  public Session getSession(String username, String encryptedSignedVerificationCode) {
    try {
      // get corresponding stored user user keys
      PublicKey creds = userKeys.get(username);

      // get corresponding verification code
      String verificationCode = verificationCodes.get(username);

      // check user login legitimacy
      if (creds != null && verificationCode != null
          && Crypto.verifyEncryptedSignature(
                 verificationCode, encryptedSignedVerificationCode, keys.getPrivate(), creds)) {
        // remove verification code side effect
        verificationCodes.remove(username);

        System.out.println("User \"" + username + "\" logged in");

        return sessions.get(username);
      } else {
        // remove verification code side effect
        verificationCodes.remove(username);

        return null;
      }
    } catch (Exception e) {
      // remove verification code side effect
      verificationCodes.remove(username);

      System.err.println("ServerImpl, getSession exception: " + e.toString());

      return null;
    }
  }

  public boolean addUser(String username, PublicKey userCredentials) throws RemoteException {
    if (userKeys.get(username) != null)
      // do not add repeated users
      return false;
    else {
      System.out.println("User \"" + username + "\" registered");

      // store given user keys
      userKeys.put(username, userCredentials);

      // create user session
      sessions.put(username, new SessionImpl(username, this));

      return true;
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
    userKeys.remove(username);
    System.out.println("User \"" + username + "\" was deleted");
  }

  public Key getPubKey() {
    return keys.getPublic();
  }

  public String getVerificationCode(String username) {
    // generate 128 secure random string
    String verificationCode = Crypto.secureRandomString(128);

    // save random string for checking in login attempt
    verificationCodes.put(username, verificationCode);

    return verificationCode;
  }

  public String getEncryptedVerificationCode(String username, Key encryptionKey) {
    try {
      // get verification code
      String verificationCode = getVerificationCode(username);

      // encrypt verification code
      String encryptedVerificationCode = Crypto.encrypt(verificationCode, encryptionKey);

      return encryptedVerificationCode;
    } catch (Exception e) {
      System.err.println("ServerImpl, getEncryptedVerificationCode exception: " + e.toString());
      return null;
    }
  }

  public Key getUserPubKey(String username) {
    return userKeys.get(username);
  }
}
