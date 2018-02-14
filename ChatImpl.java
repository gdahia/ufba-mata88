import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import javax.crypto.SealedObject;

public class ChatImpl extends UnicastRemoteObject implements Chat {
  private static Message bottomMessage = new Message("System", "<<At end.>>", false);
  private static Message topMessage = new Message("System", "<<No older message>>.", false);
  private ArrayList<String> users;
  private ArrayList<Message> messages;
  private ArrayList<SealedObject> encryptedChatKeys;
  private String topic;
  private Server server;

  public ChatImpl(Server server, String username, SealedObject encryptedChatKey)
      throws RemoteException {
    // hold server reference
    this.server = server;

    // create users vector and put chat creator in it
    users = new ArrayList<String>();
    users.add(username);

    // create messages vector and put empty message in it
    messages = new ArrayList<Message>();
    messages.add(topMessage);

    // create chat keys vector and put creator chat key in it
    encryptedChatKeys = new ArrayList<SealedObject>();
    encryptedChatKeys.add(encryptedChatKey);

    // get generic chat topic
    topic = this.toString();

    // add chat creation message
    Message creation = new Message("System", "<<\"" + username + "\" created this chat>>", false);
    sendMessage(creation);
  }

  public synchronized void sendMessage(Message message) {
    messages.add(message);
  }

  public synchronized Message getMessage(int messageIndex) {
    if (messageIndex == 0)
      return bottomMessage;
    else if (messageIndex < messages.size())
      return messages.get(messages.size() - messageIndex);
    else
      return topMessage;
  }

  public synchronized void setTopic(String topic) {
    this.topic = topic;
  }

  public synchronized ArrayList<String> getUsernames() {
    return users;
  }

  public synchronized String getTopic() {
    return topic;
  }

  public synchronized boolean addUser(String username, SealedObject encryptedChatKey) {
    try {
      // check if user is to be added to the chat
      if (users.contains(username) || !server.addUserToChat(username, this))
        return false;
      else {
        // send system message of added user
        Message newUser =
            new Message("System", "<<\"" + username + "\" was added to this chat>>", false);
        sendMessage(newUser);

        // effectively add user
        users.add(username);
        encryptedChatKeys.add(encryptedChatKey);
        return true;
      }
    } catch (Exception e) {
      System.err.println("ChatImpl, addUser exception: " + e.toString());
      return false;
    }
  }

  public synchronized int getNumMessages() {
    return messages.size();
  }

  public synchronized void removeUser(String username) {
    // remove user key
    encryptedChatKeys.remove(getUserIndex(username));

    // effectively remove user
    users.remove(username);

    // message chat with user removal
    Message userLeft = new Message("System", "<<\"" + username + "\" has left this chat>>", false);
    this.sendMessage(userLeft);
  }

  public synchronized void editMessage(int messageIndex, String messageContents) {
    messages.get(messageIndex).setContents(messageContents);
  }

  public synchronized void deleteMessage(int messageIndex) {
    messages.remove(messageIndex);
  }

  public SealedObject getUserEncryptedChatKey(String username) {
    return encryptedChatKeys.get(getUserIndex(username));
  }

  private int getUserIndex(String username) {
    return users.indexOf(username);
  }

  public Server getServer() {
    return server;
  }
}
