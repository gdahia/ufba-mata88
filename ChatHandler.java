import java.util.Scanner;
import java.util.Vector;
import java.security.Key;

public class ChatHandler {
  private String username;
  private Chat chat;
  private Key chatKey;

  public ChatHandler(String username, Key userKey, Chat chat) throws Exception {
    this.username = username;
    this.chat = chat;

    // get user chat key
    chatKey = (Key) Crypto.unsealObject(chat.getUserEncryptedChatKey(username), userKey);
  }

  public void sendMessage() {
    // get message from stdio
    Scanner inputReader = new Scanner(System.in);
    String messageContents = inputReader.nextLine();

    Message message = new Message(username, messageContents, true);

    try {
      chat.sendMessage(message);
    } catch (Exception e) {
      System.err.println("ChatHandler, sendMessage exception: " + e.toString());
    }
  }

  public void replyMessage(int messageIndex) {
    try {
      int numMessages = chat.getNumMessages();
      // handle reply of bottom/top messages
      if (messageIndex == 0 || messageIndex == numMessages)
        System.out.println("Unable to reply: no message selected");
      else {
        // get message from stdio
        Scanner inputReader = new Scanner(System.in);
        String messageContents = inputReader.nextLine();

        Message messageToReply = chat.getMessage(messageIndex);
        String replyInformation = " replies to \"" + messageToReply.getContents() + "\" from "
            + messageToReply.getAuthor();
        Message message = new Message(username, messageContents, replyInformation);
        chat.sendMessage(message);
      }

    } catch (Exception e) {
      System.err.println("ChatHandler, replyMessage exception: " + e.toString());
    }
  }

  public void printMessage(Message message) {
    System.out.println(
        message.getAuthor() + message.getReplyInformation() + ": " + message.getContents());
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
        case "r":
          replyMessage(messageIndex);
          break;
        case "q":
          quit = true;
          break;
        case "e":
          editMessage(messageIndex);
          break;
        case "d":
          deleteMessage(messageIndex);
          break;
        default:
          System.out.println("Unrecognized command");
          break;
      }

      // handle past most recent message
      if (messageIndex < 0)
        messageIndex = 0;
      try {
        int numMessages = chat.getNumMessages();
        if (messageIndex > numMessages)
          messageIndex = numMessages;
      } catch (Exception e) {
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
    String topic = inputReader.nextLine();

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
      // get user public key from server
      Key userPubKey = chat.getServer().getUserPubKey(freshUsername);

      // attempt to add user to chat
      if (userPubKey != null && chat.addUser(freshUsername, Crypto.sealObject(chatKey, userPubKey)))
        System.out.println(
            "User \"" + freshUsername + "\" added to chat \"" + chat.getTopic() + "\"");
      else
        System.out.println(
            "Unable to add \"" + freshUsername + "\" to chat \"" + chat.getTopic() + "\"");
    } catch (Exception e) {
      System.err.println("ChatHandler, addUser exception: " + e.toString());
    }
  }

  public void editMessage(int messageIndex) {
    try {
      int numMessages = chat.getNumMessages();
      Message oldMessage = chat.getMessage(messageIndex);
      messageIndex = numMessages - messageIndex;
      // handle edition of bottom/top messages
      if (!oldMessage.getEditable())
        System.out.println("Unable to edit: no editable message selected");
      else {
        if (username.equals(oldMessage.getAuthor())) {
          Scanner inputReader = new Scanner(System.in);
          String messageContents = inputReader.nextLine();
          chat.editMessage(messageIndex, messageContents);
        } else {
          System.out.println("Unable to edit: you are not the author of this message");
        }
      }

    } catch (Exception e) {
      System.err.println("ChatHandler, editMessage exception: " + e.toString());
    }
  }

  public void deleteMessage(int messageIndex) {
    try {
      int numMessages = chat.getNumMessages();
      Message message = chat.getMessage(messageIndex);
      messageIndex = numMessages - messageIndex;
      // handle deletion of bottom/top messages
      if (!message.getEditable())
        System.out.println("Unable to delete: no editable message selected");
      else {
        if (username.equals(message.getAuthor())) {
          chat.deleteMessage(messageIndex);
        } else {
          System.out.println("Unable to delete: you are not the author of this message");
        }
      }

    } catch (Exception e) {
      System.err.println("ChatHandler, deleteMessage exception: " + e.toString());
    }
  }
}
