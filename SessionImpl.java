import java.util.Vector;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class SessionImpl extends UnicastRemoteObject implements Session {
  private Server server;
  private String username;
  private Vector<Chat> chats;
  private Vector<String> chatnames;

  public SessionImpl(String username, Server server) throws RemoteException {
    super();
    this.username = username;
    this.server = server;
    chats = new Vector<Chat>();
    chatnames = new Vector<String>();
  }

  public void addChat(Chat chat) throws RemoteException {
    chats.add(chat);
    chatnames.add(chat.getTopic());
  }

  public Vector<String> getChatList() {
    return chatnames;
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
