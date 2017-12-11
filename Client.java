import java.util.Scanner;

public class Client {
  private Server server;

  public Client(Server server) {
    this.server = server;

    Scanner inputReader = new Scanner(System.in);

    // print menu options
    System.out.println("1 - Sign in\n2 - Sign up\n3 - Quit");

    // menu loop
    boolean quit = false;
    while (!quit && inputReader.hasNext()) {
      // handle user chosen menu option
      int opt = inputReader.nextInt();
      switch (opt) {
        case 1:
          login();
          break;
        case 2:
          signUp();
          break;
        case 3:
          quit = true;
          break;
        default:
          System.out.println("Unrecognized option");
          break;
      }

      // only print menu if not quitted
      if (!quit)
        System.out.println("1 - Sign in\n2 - Sign up\n3 - Quit");
    }
  }

  private void login() {
    // fetch user name and credentials
    SessionHandler id = new SessionHandler(server);

    // attempt to login
    Session sess = id.login();
    if (sess == null)
      System.out.println("Unable to sign in");
    else {
      System.out.println("Welcome, " + id.getUsername());
      new SessionMenu(sess);
    }
  }

  private void signUp() {
    // fetch user name and credentials
    SessionHandler id = new SessionHandler(server);

    // attempt signup
    if (id.signUp())
      System.out.println("User \"" + id.getUsername() + "\" registered successfully");
    else
      System.out.println("Unable to sign up");
  }
}
