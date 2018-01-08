import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Vector;
import javax.crypto.SealedObject;

public class ChatImpl extends UnicastRemoteObject implements Chat {
  private static Message bottomMessage = new Message("System", "<<At end.>>", false);
  private static Message topMessage = new Message("System", "<<No older message>>.", false);
  private Vector<String> users;
  private Vector<Message> messages;
  private Vector<SealedObject> encryptedChatKeys;
  private String topic;
  private Server server;

  public ChatImpl(Server server, String username, SealedObject encryptedChatKey)
      throws RemoteException {
    // hold server reference
    this.server = server;

    // create users vector and put chat creator in it
    users = new Vector<String>();
    users.add(username);

    // create messages vector and put empty message in it
    messages = new Vector<Message>();
    messages.add(topMessage);

    // create chat keys vector and put creator chat key in it
    encryptedChatKeys = new Vector<SealedObject>();
    encryptedChatKeys.add(encryptedChatKey);

    // get generic chat topic
    topic = this.toString();

    // add chat creation message
    Message creation = new Message("System", "<<\"" + username + "\" created this chat>>", false);
    sendMessage(creation);
  }

  public void sendMessage(Message message) {
    messages.add(message);
  }

  public Message getMessage(int messageIndex) {
    if (messageIndex == 0)
      return bottomMessage;
    else if (messageIndex < messages.size())
      return messages.get(messages.size() - messageIndex);
    else
      return topMessage;
  }

  public void setTopic(String topic) {
    this.topic = topic;
  }

  public Vector<String> getUsernames() {
    return users;
  }

  public String getTopic() {
    return topic;
  }

  public boolean addUser(String username, SealedObject encryptedChatKey) {
    try {
      // only add users that are not already in chat
      if (users.contains(username) || !server.addUserToChat(username, this))
        return false;
      else {
        users.add(username);
        encryptedChatKeys.add(encryptedChatKey);
        return true;
      }
    } catch (Exception e) {
      System.err.println("ChatImpl, addUser exception: " + e.toString());
      return false;
    }
  }

  public int getNumMessages() {
    return messages.size();
  }

  public void removeUser(String username) {
    // remove user key
    encryptedChatKeys.remove(getUserIndex(username));

    // effectively remove user
    users.remove(username);

    // message chat with user removal
    Message userLeft = new Message("System", "<<\"" + username + "\" has left this chat>>", false);
    this.sendMessage(userLeft);
  }

  public void editMessage(int messageIndex, String messageContents) {
    messages.get(messageIndex).setContents(messageContents);
  }

  public void deleteMessage(int messageIndex) {
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
