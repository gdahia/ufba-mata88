import java.util.Scanner;
import java.io.Console;

import java.security.Key;
import java.util.Base64;
import javax.crypto.Cipher;

public class SessionHandler {
  private Server server;
  private String username;
  private String credentials;

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
    Console console = System.console();
    credentials = new String(console.readPassword("Password: "));
  }

  public boolean signUp() {
    try {
      // encrypt credentials using server public key
      String cryptCredentials = encrypt(credentials, server.getPubKey());

      return server.addUser(username, cryptCredentials);
    } catch (Exception e) {
      System.err.println("SessionHandler, signUp exception: " + e.toString());
      return false;
    }
  }

  public Session login() {
    try {
      // encrypt credentials using server public key
      String cryptCredentials = encrypt(credentials, server.getPubKey());

      return server.getSession(username, cryptCredentials);
    } catch (Exception e) {
      System.err.println("SessionHandler, login exception: " + e.toString());
      return null;
    }
  }

  public String getUsername() {
    return username;
  }

  public String encrypt(String message, Key pubKey) {
    try {
      Cipher cipher = Cipher.getInstance("RSA");
      cipher.init(Cipher.ENCRYPT_MODE, pubKey);
      return Base64.getEncoder().encodeToString(cipher.doFinal(message.getBytes()));
    } catch (Exception e) {
      System.err.println("SessionHandler, encrypt exception: " + e.toString());
      return null;
    }
  }
}
