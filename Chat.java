import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Vector;
import javax.crypto.SealedObject;

public interface Chat extends Remote {
  public void sendMessage(Message message) throws RemoteException;
  public Message getMessage(int messageIndex) throws RemoteException;
  public void setTopic(String topic) throws RemoteException;
  public String getTopic() throws RemoteException;
  public Vector<String> getUsernames() throws RemoteException;
  public boolean addUser(String username, SealedObject encryptedUserKey) throws RemoteException;
  public int getNumMessages() throws RemoteException;
  public void removeUser(String username) throws RemoteException;
  public void editMessage(int messageIndex, String messageContents) throws RemoteException;
  public void deleteMessage(int messageIndex) throws RemoteException;
  public SealedObject getUserEncryptedChatKey(String username) throws RemoteException;
  public Server getServer() throws RemoteException;
}
