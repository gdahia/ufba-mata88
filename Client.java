import java.util.Scanner;
import java.util.Vector;

public class Client {
  private Server server;

  public Client(Server server) {
    this.server = server;
    mainMenu();
  }

  private void login() {
    SessionHandler id = new SessionHandler(server);
    Session sess = id.login();
    if (sess == null)
      System.out.println("Unable to sign in");
    else {
      System.out.println("Welcome, " + id.getUsername());
      sessionMenu(sess);
    }
  }

  private void signUp() {
    SessionHandler id = new SessionHandler(server);
    if (id.signUp())
      System.out.println("User \"" + id.getUsername() + "\" registered successfully");
    else
      System.out.println("Unable to sign up");
  }

  private void printSessionMenu(Vector<String> chats) {
    int len = chats.size();
    for (int i = 0; i < len; i++) System.out.println((i + 1) + " - " + chats.get(i));
    System.out.println((len + 1) + " - New chat");
    System.out.println((len + 2) + " - Quit");
  }

  private void newChat(Session sess) {
    // read user to whom will chat
    System.out.print("Enter username to chat with: ");
    Scanner inputReader = new Scanner(System.in);
    String username = inputReader.next();

    try {
      if (sess.newChat(username))
        System.out.println("Succesfully created chat with " + username);
      else
        System.out.println("Unable to create chat");
    } catch (Exception e) {
      System.out.println("Unable to create chat");
    }
  }

  private void sessionMenu(Session sess) {
    Scanner inputReader = new Scanner(System.in);
    boolean quit = false;

    try {
      Vector<String> chats = sess.getChatList();

      printSessionMenu(chats);
      while (!quit && inputReader.hasNext()) {
        // handle chosen menu option
        int opt = inputReader.nextInt();
        int len = chats.size();
        if (1 <= opt && opt <= len) {
          // todo: get chat and start chat menu
        } else if (len + 1 == opt)
          newChat(sess);
        else if (len + 2 == opt)
          quit = true;
        else
          System.out.println("Unrecognized option");

        // handle quit
        if (!quit) {
          chats = sess.getChatList();
          printSessionMenu(chats);
        }
      }
    } catch (Exception e) {
    }

    System.out.println("User logged out");
  }

  private void mainMenu() {
    Scanner inputReader = new Scanner(System.in);
    boolean quit = false;
    System.out.println("1 - Sign in\n2 - Sign up\n3 - Quit");
    while (!quit && inputReader.hasNext()) {
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
      if (!quit)
        System.out.println("1 - Sign in\n2 - Sign up\n3 - Quit");
    }
  }
}
