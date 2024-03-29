import java.util.Scanner;
import java.util.ArrayList;

public class SessionMenu {
  public SessionMenu(Session sess) {
    Scanner inputReader = new Scanner(System.in);
    boolean deleted = false;

    try {
      ArrayList<String> chats = sess.getChatList();
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
        } else if (len + 3 == opt) {
          // refresh menu
          System.out.println("Chat list up-to-date");
        } else if (len + 4 == opt) {
          // leave a given chat
          leaveChat(sess);
        } else if (len + 5 == opt) {
          // logout
          quit = true;
        } else
          System.out.println("Unrecognized option");

        // only update chat list and reprint menu if not quitted
        if (!quit) {
          chats = sess.getChatList();
          printSessionMenu(chats);
        }
      }
    } catch (Exception e) {
      System.err.println("SessionMenu, SessionMenu exception: " + e.toString());
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
    if (!opt.isEmpty() && opt.charAt(0) == 'y') {
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

  private void printSessionMenu(ArrayList<String> chats) {
    // 1st to len-th session menu options are chats
    int len = chats.size();
    for (int i = 0; i < len; i++) System.out.println((i + 1) + " - " + chats.get(i));

    // remaining options
    System.out.println((len + 1) + " - New chat");
    System.out.println((len + 2) + " - Delete account");
    System.out.println((len + 3) + " - Refresh menu");
    System.out.println((len + 4) + " - Leave chat");
    System.out.println((len + 5) + " - Log out");
  }

  private void leaveChat(Session sess) {
    try {
      ArrayList<String> chats = sess.getChatList();
      int len = chats.size();
      if (len == 0)
        // empty chat list
        System.out.println("Not currently participating in any chats");
      else {
        // get leaving chat index
        System.out.print("Type the index of the chat to leave: ");
        Scanner inputReader = new Scanner(System.in);
        int opt = inputReader.nextInt();

        if (1 > opt || opt > len)
          // out of range index
          System.out.println("Chat index out of range");
        else {
          // leave chosen chat
          sess.leaveChat(opt - 1);
          System.out.println("Successfully left chat " + opt);
        }
      }
    } catch (Exception e) {
      System.err.println("SessionMenu, leaveChat exception: " + e.toString());
    }
  }
}
