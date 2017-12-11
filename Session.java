import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Vector;

public interface Session extends Remote {
  public void addChat(Chat chat) throws RemoteException;
  public Vector<String> getChatList() throws RemoteException;
  public void newChat() throws RemoteException;
  public Chat getChat(int index) throws RemoteException;
  public String getUsername() throws RemoteException;
}
