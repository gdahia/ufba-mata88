import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Vector;

public interface Session extends Remote {
  public Vector<String> getChatList() throws RemoteException;
  public String getUsername() throws RemoteException;
  public boolean newChat(String username) throws RemoteException;
}
