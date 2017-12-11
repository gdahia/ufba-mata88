import java.util.Scanner;

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
    String messageContents = inputReader.next();

    Message message = new Message(username, messageContents);

    try {
      chat.sendMessage(message);
    } catch (Exception e) {
      System.err.println("ChatHandler, sendMessage exception: " + e.toString());
    }
  }

  public void printMessage(Message message) {
    System.out.println(message.getContents());
  }

  public void fetchMessages() {
    Scanner inputReader = new Scanner(System.in);

    boolean quit = false;
    int messageIndex = 0;
    try {
      printMessage(chat.getMessage(messageIndex));
    } catch (Exception e) {
      System.err.println("ChatHandler, fetchMessages(1) exception: " + e.toString());
    }
    while (!quit && inputReader.hasNext()) {
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
      if (messageIndex < 0)
        messageIndex = 0;

      if (!quit)
        try {
          printMessage(chat.getMessage(messageIndex));
        } catch (Exception e) {
          System.err.println("ChatHandler, fetchMessages(2) exception: " + e.toString());
        }
    }
  }

  public boolean addUser() {
    Scanner inputReader = new Scanner(System.in);
    System.out.print("Username to add: ");
    String freshUsername = inputReader.next();

    try {
      return chat.addUser(freshUsername);
    } catch (Exception e) {
      System.err.println("ChatHandler, addUser exception: " + e.toString());
      return false;
    }
  }
}
