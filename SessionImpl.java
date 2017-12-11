import java.util.Vector;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class SessionImpl extends UnicastRemoteObject implements Session {
  private Server server;
  private String username;
  private Vector<Chat> chats;

  public SessionImpl(String username, Server server) throws RemoteException {
    super();
    this.username = username;
    this.server = server;
    chats = new Vector<Chat>();
  }

  public void addChat(Chat chat) throws RemoteException {
    chats.add(chat);
  }

  public Vector<String> getChatList() throws RemoteException {
    // get updated copy of chat topics
    Vector<String> chatNames = new Vector<String>();
    for (Chat chat : chats) chatNames.add(chat.getTopic());
    return chatNames;
  }

  public void newChat() {
    try {
      server.addChat(this);
    } catch (Exception e) {
      System.err.println("SessionImpl, newChat exception: " + e.toString());
    }
  }

  public Chat getChat(int index) {
    return chats.get(index);
  }

  public String getUsername() {
    return username;
  }
}
