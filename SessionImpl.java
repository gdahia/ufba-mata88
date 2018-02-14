import java.util.ArrayList;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import javax.crypto.SealedObject;

public class SessionImpl extends UnicastRemoteObject implements Session {
  private Server server;
  private String username;
  private ArrayList<Chat> chats;

  public SessionImpl(String username, Server server) throws RemoteException {
    super();
    this.username = username;
    this.server = server;
    chats = new ArrayList<Chat>();
  }

  public synchronized void addChat(Chat chat) throws RemoteException {
    chats.add(chat);
  }

  public synchronized ArrayList<String> getChatList() throws RemoteException {
    // get updated copy of chat topics
    ArrayList<String> chatNames = new ArrayList<String>();
    for (Chat chat : chats) chatNames.add(chat.getTopic());
    return chatNames;
  }

  public synchronized void newChat(SealedObject encryptedChatKey) {
    try {
      // create new chat
      Chat chat = new ChatImpl(server, username, encryptedChatKey);

      // add chat
      addChat(chat);

      // output message for creation
      System.out.println("User \"" + username + "\" created a new chat");
    } catch (Exception e) {
      System.err.println("SessionImpl, newChat exception: " + e.toString());
    }
  }

  public synchronized Chat getChat(int index) {
    return chats.get(index);
  }

  public synchronized String getUsername() {
    return username;
  }

  public synchronized void delete() throws RemoteException {
    // remove from server
    server.removeUser(username);

    // remove from every participating chat
    for (Chat chat : chats) chat.removeUser(username);
  }

  public synchronized void leaveChat(int index) throws RemoteException {
    // remove user from chat
    chats.get(index).removeUser(username);

    // remove chat from user session
    chats.remove(index);
  }
}
