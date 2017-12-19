import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Vector;

public class ChatImpl extends UnicastRemoteObject implements Chat {
  private static Message bottomMessage = new Message("System", "<<At end.>>");
  private static Message topMessage = new Message("System", "<<No older message>>.");
  private Vector<String> users;
  private Vector<Message> messages;
  private String topic;
  private Server server;

  public ChatImpl(Server server, String username) throws RemoteException {
    // hold server reference
    this.server = server;

    // create users vector and put chat creator in it
    users = new Vector<String>();
    users.add(username);

    // create messages vector and put empty message in it
    messages = new Vector<Message>();
    messages.add(topMessage);

    // get generic chat topic
    topic = this.toString();
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

  public boolean addUser(String username) {
    try {
      // only add users that are not already in chat
      if (users.contains(username) || !server.addUserToChat(username, this))
        return false;
      else {
        users.add(username);
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
    users.remove(username);
  }

  public void editMessage(int messageIndex, String messageContents){
  	messages.get(messageIndex).setContents(messageContents);
  }
}
