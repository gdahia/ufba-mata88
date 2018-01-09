import java.util.Scanner;
import java.security.KeyPair;

public class SessionHandler {
  private Server server;
  private String username;
  private KeyPair keys;

  public SessionHandler(Server server) {
    this.server = server;
    fetchUsername();
    fetchUserCredentials();
  }

  public void fetchUsername() {
    Scanner inputReader = new Scanner(System.in);
    System.out.print("Username: ");
    username = inputReader.next();
  }

  public void fetchUserCredentials() {
    // read keypair folder from standard input
    Scanner inputReader = new Scanner(System.in);
    System.out.print("Path to key pair folder: ");
    String credentialsPath = inputReader.nextLine();

    // retrieve keypair
    try {
      keys = Crypto.getKeys(credentialsPath);
    } catch (Exception e) {
      System.err.println("SessionHandler, fetchUserCredentials exception: " + e.toString());
      System.out.println("Unable to generate key-pair. Exiting");
      System.exit(1);
    }
  }

  public boolean signUp() {
    try {
      return server.addUser(username, keys.getPublic());
    } catch (Exception e) {
      System.err.println("SessionHandler, signUp exception: " + e.toString());
      return false;
    }
  }

  public Session login() {
    try {
      // get encrypted verification code
      String encryptedVerificationCode = server.getEncryptedVerificationCode(username);

      // decrypt verification code using private key
      String verificationCode = Crypto.decrypt(encryptedVerificationCode, keys.getPrivate(), "RSA");

      // encrypt verification code using server public key
      String reencryptedVerificationCode =
          Crypto.encrypt(verificationCode, server.getPubKey(), "RSA");

      return server.getSession(username, reencryptedVerificationCode);
    } catch (Exception e) {
      System.err.println("SessionHandler, login exception: " + e.toString());
      return null;
    }
  }

  public String getUsername() {
    return username;
  }

  public KeyPair getCredentials() {
    return keys;
  }
}
