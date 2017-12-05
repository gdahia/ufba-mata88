import java.util.Scanner;

public class Client {
  private Server server;

  public Client(Server server) {
    this.server = server;
    mainMenu();
  }

  private String getUsername() {
    Scanner inputReader = new Scanner(System.in);
    System.out.print("username: ");
    String username = inputReader.next();
    return username;
  }

  private String getUserCredentials() {
    Scanner inputReader = new Scanner(System.in);
    System.out.print("password: ");
    String password = inputReader.next();
    return password;
  }

  private boolean attemptUserRegistry(String username, String credentials) {
    try {
      return server.addUser(username, credentials);
    } catch (Exception e) {
      return false;
    }
  }

  private boolean attemptLogin(String username, String credentials) {
    try {
      return server.verifyCredentials(username, credentials);
    } catch (Exception e) {
      return false;
    }
  }

  private void signIn() {
    String username = getUsername();
    String credentials = getUserCredentials();
    if (attemptLogin(username, credentials))
      System.out.println("Welcome, " + username);
    else
      System.out.println("Unable to sign in");
  }

  private void signUp() {
    String username = getUsername();
    String credentials = getUserCredentials();
    if (attemptUserRegistry(username, credentials))
      System.out.println("User \"" + username + "\" registered successfully");
    else
      System.out.println("Unable to sign up");
  }

  private void mainMenu() {
    Scanner inputReader = new Scanner(System.in);
    boolean quit = false;
    System.out.println("1 - Sign in\n2 - Sign up\n3 - Quit");
    while (!quit && inputReader.hasNext()) {
      int opt = inputReader.nextInt();
      switch (opt) {
        case 1:
          signIn();
          break;
        case 2:
          signUp();
          break;
        case 3:
          quit = true;
          break;
      }
      if (!quit)
        System.out.println("1 - Sign in\n2 - Sign up\n3 - Quit");
    }
  }
}
