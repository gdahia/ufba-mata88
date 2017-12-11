import java.util.Scanner;
import java.util.Vector;

public class Client {
  private Server server;

  public Client(Server server) {
    this.server = server;
    mainMenu();
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
      sessionMenu(sess);
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

  private void printSessionMenu(Vector<String> chats) {
    // 1st to len-th session menu options are chats
    int len = chats.size();
    for (int i = 0; i < len; i++) System.out.println((i + 1) + " - " + chats.get(i));

    // remaining options
    System.out.println((len + 1) + " - New chat");
    System.out.println((len + 2) + " - Log out");
  }

  private void newChat(Session sess) {
    try {
      sess.newChat();
    } catch (Exception e) {
      System.err.println("Client, newChat exception: " + e.toString());
      System.out.println("Unable to create chat");
    }
  }

  private void sessionMenu(Session sess) {
    Scanner inputReader = new Scanner(System.in);

    try {
      Vector<String> chats = sess.getChatList();
      printSessionMenu(chats);

      // menu loop
      boolean quit = false;
      while (!quit && inputReader.hasNext()) {
        // handle chosen menu option
        int opt = inputReader.nextInt();

        int len = chats.size();
        if (1 <= opt && opt <= len) {
          // open specified chat
          ChatHandler chatHandler = new ChatHandler(sess.getUsername(), sess.getChat(opt - 1));
          chatMenu(chatHandler);
        } else if (len + 1 == opt) {
          // create new chat
          newChat(sess);
          System.out.println("New chat created");
        } else if (len + 2 == opt)
          quit = true;
        else
          System.out.println("Unrecognized option");

        // only update chat list and reprint menu if not quitted
        if (!quit) {
          chats = sess.getChatList();
          printSessionMenu(chats);
        }
      }
    } catch (Exception e) {
      System.err.println("Client, sessionMenu exception: " + e.toString());
    }

    System.out.println("User logged out");
  }

  private void mainMenu() {
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

  private void chatMenu(ChatHandler chatHand) {
    Scanner inputReader = new Scanner(System.in);

    // print menu options
    System.out.println(
        "1 - Send message\n2 - See message log\n3 - Add new user\n4 - Change topic\n5 - Display members\n6 - Quit");

    // menu loop
    boolean quit = false;
    while (!quit && inputReader.hasNext()) {
      // handle user chosen menu option
      int opt = inputReader.nextInt();
      switch (opt) {
        case 1:
          chatHand.sendMessage();
          break;
        case 2:
          chatHand.fetchMessages();
          break;
        case 3:
          chatHand.addUser();
          break;
        case 4:
          chatHand.changeTopic();
          break;
        case 5:
          chatHand.displayMembers();
          break;
        case 6:
          quit = true;
          break;
        default:
          System.out.println("Unrecognized option");
          break;
      }

      // only reprint menu if not quitted
      if (!quit)
        System.out.println(
            "1 - Send message\n2 - See message log\n3 - Add new user\n4 - Change topic\n5 - Display members\n6 - Quit");
    }
  }
}
