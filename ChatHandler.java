import java.util.Scanner;
import java.util.Vector;

public class ChatHandler {
  private String username;
  private Chat chat;

  public ChatHandler(String username, Chat chat) {
    this.username = username;
    this.chat = chat;
  }

  public void sendMessage() {
    // get message from stdio
    Scanner inputReader = new Scanner(System.in);
    String messageContents = inputReader.nextLine();

    Message message = new Message(username, messageContents);

    try {
      chat.sendMessage(message);
    } catch (Exception e) {
      System.err.println("ChatHandler, sendMessage exception: " + e.toString());
    }
  }

  public void printMessage(Message message) {
    System.out.println(message.getAuthor() + ": " + message.getContents());
  }

  public void fetchMessages() {
    Scanner inputReader = new Scanner(System.in);

    // attempt to print current indexed message
    int messageIndex = 1;
    try {
      printMessage(chat.getMessage(messageIndex));
    } catch (Exception e) {
      System.err.println("ChatHandler, fetchMessages(1) exception: " + e.toString());
    }

    // message log loop
    boolean quit = false;
    while (!quit && inputReader.hasNext()) {
      // handle chosen vimlike option
      String command = inputReader.next();
      switch (command) {
        case "j":
          messageIndex--;
          break;
        case "k":
          messageIndex++;
          break;
        case "q":
          quit = true;
          break;
        default:
          System.out.println("Unrecognized command");
          break;
      }

      // handle past most recent message
      if (messageIndex < 0)
        messageIndex = 0;
      try{
        int numMessages = chat.getNumMessages();
        if (messageIndex > numMessages)
          messageIndex = numMessages;
      }
      catch (Exception e) {
        System.err.println("ChatHandler, fetchMessages(2) exception: " + e.toString());
      }

      // only reprint current message if not quitted
      if (!quit)
        try {
          printMessage(chat.getMessage(messageIndex));
        } catch (Exception e) {
          System.err.println("ChatHandler, fetchMessages(3) exception: " + e.toString());
        }
    }
  }

  public void changeTopic() {
    // get new chat topic from stdio
    Scanner inputReader = new Scanner(System.in);
    System.out.print("Enter new chat topic: ");
    String topic = inputReader.next();

    // update chat topic
    try {
      chat.setTopic(topic);
    } catch (Exception e) {
      System.err.println("ChatHandler, changeTopic exception: " + e.toString());
    }
  }

  public void displayMembers() {
    try {
      Vector<String> usernames = chat.getUsernames();
      System.out.println("Members:");
      for (String username : usernames) System.out.println(username);
    } catch (Exception e) {
      System.err.println("ChatHandler, displayMembers exception: " + e.toString());
    }
  }

  public void addUser() {
    // fetch username to add to chat
    Scanner inputReader = new Scanner(System.in);
    System.out.print("Username to add: ");
    String freshUsername = inputReader.next();

    try {
      // attempt to add user to chat
      if (chat.addUser(freshUsername))
        System.out.println(
            "User \"" + freshUsername + "\" added to chat \"" + chat.getTopic() + "\"");
      else
        System.out.println(
            "Unable to add \"" + freshUsername + "\" to chat \"" + chat.getTopic() + "\"");
    } catch (Exception e) {
      System.err.println("ChatHandler, addUser exception: " + e.toString());
    }
  }
}
