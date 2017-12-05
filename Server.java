import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Server extends Remote {
  public boolean verifyCredentials(String username, String userCredentials) throws RemoteException;
  public boolean addUser(String username, String userCredentials) throws RemoteException;
}
