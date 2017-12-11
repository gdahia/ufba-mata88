import java.util.Scanner;
import java.io.Console;

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
      return server.addUser(username, credentials);
    } catch (Exception e) {
      return false;
    }
  }

  public Session login() {
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
