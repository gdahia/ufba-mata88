import java.util.Scanner;
import java.util.Vector;

public class SessionMenu {
  public SessionMenu(Session sess) {
    Scanner inputReader = new Scanner(System.in);
    boolean deleted = false;

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
          new ChatMenu(chatHandler);
        } else if (len + 1 == opt) {
          // create new chat
          newChat(sess);
          System.out.println("New chat created");
        } else if (len + 2 == opt) {
          // delete user account prompt
          if (deleteAccount(sess)) {
            deleted = true;
            quit = true;
          }
        } else if (len + 3 == opt)
          quit = true;
        else if (len + 4 != opt)
          System.out.println("Unrecognized option");

        // only update chat list and reprint menu if not quitted
        if (!quit) {
          chats = sess.getChatList();
          printSessionMenu(chats);
        }
      }
    } catch (Exception e) {
      System.err.println("Client, sessionMenu exception(2): " + e.toString());
    }

    // do not print logout message if account was deleted
    if (!deleted)
      System.out.println("User logged out");
  }

  private void newChat(Session sess) {
    try {
      sess.newChat();
    } catch (Exception e) {
      System.err.println("Client, newChat exception: " + e.toString());
      System.out.println("Unable to create chat");
    }
  }

  private boolean deleteAccount(Session sess) {
    // check if user really wants to delete account
    System.out.println("Are you sure you want to delete your account? This is irreversible");
    Scanner inputReader = new Scanner(System.in);
    String opt = inputReader.nextLine();
    if (!opt.isEmpty() || opt.charAt(0) == 'y') {
      // delete account
      try {
        sess.delete();
        System.out.println("Account deleted");
        return true;
      } catch (Exception e) {
        System.err.println("SessionMenu, delete exception: " + e.toString());
        System.out.println("Unable to delete account");
      }
    }

    return false;
  }

  private void printSessionMenu(Vector<String> chats) {
    // 1st to len-th session menu options are chats
    int len = chats.size();
    for (int i = 0; i < len; i++) System.out.println((i + 1) + " - " + chats.get(i));

    // remaining options
    System.out.println((len + 1) + " - New chat");
    System.out.println((len + 2) + " - Delete account");
    System.out.println((len + 3) + " - Log out");
    System.out.println((len + 4) + " - Refresh menu");
  }
}
