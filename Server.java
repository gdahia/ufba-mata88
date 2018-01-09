import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.Key;

public interface Server extends Remote {
  public Session getSession(String username, String userCredentials) throws RemoteException;
  public boolean addUser(String username, Key userCredentials) throws RemoteException;
  public void removeUser(String username) throws RemoteException;
  public boolean addUserToChat(String username, Chat chat) throws RemoteException;
  public String getVerificationCode(String username) throws RemoteException;
  public String getEncryptedVerificationCode(String username) throws RemoteException;
  public Key getPubKey() throws RemoteException;
  public Key getUserPubKey(String username) throws RemoteException;
}
