import java.util.Scanner;
import java.io.Console;

public class SessionHandler {
  private static SessionHandler instance;
  private String username;
  private String credentials;

  private SessionHandler() {}

  public static SessionHandler getInstance() {
    if (instance == null)
      instance = new SessionHandler();
    return instance;
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

  public boolean signUp(Server server) {
    try {
      return server.addUser(username, credentials);
    } catch (Exception e) {
      return false;
    }
  }

  public Session login(Server server) {
    try {
      return server.getSession(username, credentials);
    } catch (Exception e) {
      System.out.println(e.toString());
      return null;
    }
  }

  public String getUsername() {
    return username;
  }
}
