import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.Key;
import java.security.PublicKey;

public interface Server extends Remote {
  public Session getSession(String username, String userCredentials) throws RemoteException;
  public void addChat(Session sess) throws RemoteException;
  public boolean addUser(String username, PublicKey userCredentials) throws RemoteException;
  public void removeUser(String username) throws RemoteException;
  public boolean addUserToChat(String username, Chat chat) throws RemoteException;
  public String getVerificationCode(String username) throws RemoteException;
  public String getEncryptedVerificationCode(String username, Key encryptionKey)
      throws RemoteException;
  public Key getPubKey() throws RemoteException;
}
