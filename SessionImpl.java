import java.util.Vector;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class SessionImpl extends UnicastRemoteObject implements Session {
  private Server server;
  private Vector<Chat> chats;
  private Vector<String> chatnames;

  public SessionImpl(Server server) throws RemoteException {
    super();
    this.server = server;
    chats = new Vector<Chat>();
    chatnames = new Vector<String>();
  }

  public Vector<String> getChatList() {
    return chatnames;
  }

  public boolean newChat(String username) {
    if (chatnames.contains(username))
      return false;
    chatnames.add(username);
    return true;
  }

  public Chat getChat(int index) {
    return chats.get(index);
  }
}
