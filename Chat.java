import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Vector;

public interface Chat extends Remote {
  public void sendMessage(Message message) throws RemoteException;
  public Message getMessage(int messageIndex) throws RemoteException;
  public void setTopic(String topic) throws RemoteException;
  public String getTopic() throws RemoteException;
  public Vector<String> getUsernames() throws RemoteException;
  public boolean addUser(String username) throws RemoteException;
  public int getNumMessages() throws RemoteException;
  public void removeUser(String username) throws RemoteException;
  public void editMessage(int messageIndex, String messageContents) throws RemoteException;
  public void deleteMessage(int messageIndex) throws RemoteException;
}
