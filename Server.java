import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Server extends Remote {
  public Session getSession(String username, String userCredentials) throws RemoteException;
  public void addChat(Session sess) throws RemoteException;
  public void connectNewReplica(Server newReplica) throws RemoteException;
  public void addReplica(Server server) throws RemoteException;
  public boolean addUser(String username, String userCredentials) throws RemoteException;
  public void removeUser(String username) throws RemoteException;
  public boolean addUserToChat(String username, Chat chat) throws RemoteException;
}
