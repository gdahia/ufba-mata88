import java.rmi.Remote;
import java.rmi.RemoteException;
import javax.crypto.SealedObject;
import java.util.ArrayList;

public interface Session extends Remote {
  public void addChat(Chat chat) throws RemoteException;
  public ArrayList<String> getChatList() throws RemoteException;
  public void newChat(SealedObject encryptedChatKey) throws RemoteException;
  public Chat getChat(int index) throws RemoteException;
  public String getUsername() throws RemoteException;
  public void delete() throws RemoteException;
  public void leaveChat(int index) throws RemoteException;
}
